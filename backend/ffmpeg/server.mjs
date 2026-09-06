import Fastify from 'fastify';
import multipart from '@fastify/multipart';
import { spawn } from 'node:child_process';
import { mkdir, mkdtemp, readFile, rm, writeFile } from 'node:fs/promises';
import { createWriteStream } from 'node:fs';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { pipeline } from 'node:stream/promises';
import { randomUUID } from 'node:crypto';

const app = Fastify({ logger: true, bodyLimit: 2 * 1024 * 1024 * 1024 });
await app.register(multipart, { limits: { fileSize: 2 * 1024 * 1024 * 1024, files: 64, fields: 16 } });

app.get('/health', async () => ({ ok: true, service: 'div-edit-ffmpeg', ffmpeg: await ffmpegVersion() }));

app.post('/v1/render-timeline', async (request, reply) => {
  const work = await mkdtemp(join(tmpdir(), 'div-edit-'));
  try {
    let manifest = null;
    const media = [];
    for await (const part of request.parts()) {
      if (part.type === 'file') {
        const path = join(work, `media_${media.length}.${extension(part.filename)}`);
        await pipeline(part.file, createWriteStream(path));
        if (part.file.truncated) return reply.code(413).send({ error: 'Media file exceeded the configured size limit' });
        media.push(path);
      } else if (part.fieldname === 'manifest') {
        manifest = JSON.parse(part.value);
      }
    }

    if (!manifest || !Array.isArray(manifest.clips) || manifest.clips.length !== media.length) {
      return reply.code(400).send({ error: 'Manifest and media count do not match' });
    }

    const normalized = manifest.clips.map((clip, i) => ({
      file: media[i],
      trimIn: Math.max(0, Number(clip.trimInMs || 0)) / 1000,
      trimOut: Math.max(0, Number(clip.trimOutMs || 0)) / 1000,
      speed: Math.max(0.01, Number(clip.speed || 1)),
      volume: Math.max(0, Number(clip.volume ?? 1))
    }));

    const segmentPaths = [];
    for (let i = 0; i < normalized.length; i++) {
      const item = normalized[i];
      const out = join(work, `segment_${i}.mp4`);
      const duration = item.trimOut > 0 ? Math.max(0.01, item.trimOut - item.trimIn) : null;
      const args = ['-y', '-ss', String(item.trimIn), '-i', item.file];
      if (duration !== null) args.push('-t', String(duration));
      const vf = item.speed === 1 ? [] : [`setpts=${1 / item.speed}*PTS`];
      const af = item.speed === 1 ? [] : [`atempo=${clampAtempo(item.speed)}`];
      if (vf.length || af.length) {
        if (vf.length) args.push('-vf', vf.join(','));
        if (af.length) args.push('-af', af.join(','));
      }
      args.push('-af', `volume=${item.volume}`, '-c:v', 'libx264', '-preset', 'medium', '-crf', qualityCrf(manifest.quality), '-c:a', 'aac', '-b:a', '192k', '-movflags', '+faststart', out);
      await runFfmpeg(args);
      segmentPaths.push(out);
    }

    const concat = join(work, 'concat.txt');
    await writeFile(concat, segmentPaths.map(p => `file '${p.replaceAll("'", "'\\''")}'`).join('\n'));
    const output = join(work, `export_${randomUUID()}.mp4`);
    const [width, height] = [Number(manifest.width || 1920), Number(manifest.height || 1080)];
    const fps = Number(manifest.fps || 30);
    await runFfmpeg(['-y', '-f', 'concat', '-safe', '0', '-i', concat, '-vf', `scale=${width}:${height}:force_original_aspect_ratio=decrease,pad=${width}:${height}:(ow-iw)/2:(oh-ih)/2`, '-r', String(fps), '-c:v', 'libx264', '-preset', 'medium', '-crf', qualityCrf(manifest.quality), '-c:a', 'aac', '-b:a', '192k', '-movflags', '+faststart', output]);

    const buffer = await readFile(output);
    reply.type('video/mp4').send(buffer);
  } catch (error) {
    request.log.error(error);
    return reply.code(500).send({ error: error?.message || 'FFmpeg render failed' });
  } finally {
    await rm(work, { recursive: true, force: true });
  }
});

function extension(name = '') {
  const ext = name.split('.').pop()?.toLowerCase();
  return ['mp4', 'mov', 'mkv', 'webm', 'avi', 'm4v'].includes(ext) ? ext : 'bin';
}
function qualityCrf(q) { return q === 'Maximum' ? '16' : q === 'Standard' ? '23' : '18'; }
function clampAtempo(speed) { return Math.max(0.5, Math.min(2, speed)); }
function runFfmpeg(args) {
  return new Promise((resolve, reject) => {
    const child = spawn('ffmpeg', args, { stdio: ['ignore', 'pipe', 'pipe'] });
    let stderr = '';
    child.stderr.on('data', d => { stderr += d.toString(); });
    child.on('error', reject);
    child.on('close', code => code === 0 ? resolve() : reject(new Error(`ffmpeg exited ${code}: ${stderr.slice(-4000)}`)));
  });
}
function ffmpegVersion() {
  return new Promise(resolve => {
    const child = spawn('ffmpeg', ['-version']);
    let first = '';
    child.stdout.on('data', d => { first += d.toString(); });
    child.on('close', () => resolve(first.split('\n')[0] || 'unavailable'));
    child.on('error', () => resolve('unavailable'));
  });
}

const port = Number(process.env.PORT || 8080);
await mkdir(tmpdir(), { recursive: true });
await app.listen({ host: '0.0.0.0', port });
