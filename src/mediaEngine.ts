import { FFmpeg } from "@ffmpeg/ffmpeg";
import { fetchFile, toBlobURL } from "@ffmpeg/util";

let ffmpeg: FFmpeg | null = null;
let loaded = false;

export async function loadMediaEngine(onProgress?: (value:number)=>void) {
  if (loaded && ffmpeg) return ffmpeg;
  ffmpeg = new FFmpeg();
  if (onProgress) ffmpeg.on("progress", ({ progress }) => onProgress(progress));
  const base = "https://cdn.jsdelivr.net/npm/@ffmpeg/core@0.12.6/dist/esm";
  await ffmpeg.load({
    coreURL: await toBlobURL(base + "/ffmpeg-core.js", "text/javascript"),
    wasmURL: await toBlobURL(base + "/ffmpeg-core.wasm", "application/wasm")
  });
  loaded = true;
  return ffmpeg;
}

function atempoChain(speed:number) {
  let v = speed;
  const parts:string[] = [];
  while (v > 2) { parts.push("atempo=2"); v /= 2; }
  while (v < 0.5) { parts.push("atempo=0.5"); v /= 0.5; }
  parts.push("atempo=" + v.toFixed(5));
  return parts.join(",");
}

export async function exportVideoClip(opts:{
  src:string;
  duration:number;
  speed:number;
  reverse?:boolean;
  freeze?:boolean;
  fps:number;
  width:number;
  height:number;
  bitrate?:number;
  onProgress?: (value:number)=>void;
}) {
  const f = await loadMediaEngine(opts.onProgress);
  const input = await fetchFile(opts.src);
  const inputName = "input" + (opts.src.toLowerCase().includes(".mov") ? ".mov" : ".mp4");
  await f.writeFile(inputName, input);

  const filters:string[] = [
    `scale=${opts.width}:${opts.height}:force_original_aspect_ratio=decrease,pad=${opts.width}:${opts.height}:(ow-iw)/2:(oh-ih)/2`,
    `setpts=PTS/${Math.max(0.1,Math.min(100,opts.speed))}`
  ];
  if (opts.reverse) filters.push("reverse");
  if (opts.freeze) filters.push("tpad=stop_mode=clone:stop_duration=2");
  const audio = opts.reverse ? "areverse," + atempoChain(opts.speed) : atempoChain(opts.speed);
  const args = [
    "-i", inputName,
    "-t", String(Math.max(0.1, opts.duration / Math.max(0.1, opts.speed) + (opts.freeze ? 2 : 0))),
    "-vf", filters.join(","),
    "-af", audio,
    "-r", String(opts.fps),
    "-c:v", "libx264",
    "-preset", "veryfast",
    "-pix_fmt", "yuv420p",
    "-b:v", String(Math.max(2, opts.bitrate || 12)) + "M",
    "-c:a", "aac",
    "-b:a", "192k",
    "-movflags", "+faststart",
    "output.mp4"
  ];
  await f.exec(args);
  const data = await f.readFile("output.mp4");
  await f.deleteFile(inputName);
  await f.deleteFile("output.mp4");
  return new Blob([data], {type:"video/mp4"});
}
