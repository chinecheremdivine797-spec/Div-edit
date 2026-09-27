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

export async function renderProject(opts:{
  layers:any[]; duration:number; fps:number; width:number; height:number; bitrate?:number;
  drawLayer:(ctx:CanvasRenderingContext2D,layer:any,time:number,media:HTMLMediaElement|null)=>void;
  onProgress?:(value:number)=>void;
}) {
  const video = document.createElement("canvas");
  video.width=opts.width; video.height=opts.height;
  const ctx=video.getContext("2d")!;
  const media = new Map<string,HTMLMediaElement>();
  for (const l of opts.layers) {
    if ((l.kind==="video"||l.kind==="audio") && l.src) {
      const el=document.createElement(l.kind==="audio"?"audio":"video");
      el.src=l.src; el.preload="auto"; el.crossOrigin="anonymous"; el.muted=l.kind==="video";
      await new Promise<void>(resolve=>{el.onloadedmetadata=()=>resolve();el.onerror=()=>resolve()});
      media.set(l.id,el);
    } else if (l.kind==="image" && l.src) {
      const el=document.createElement("img"); el.src=l.src; await new Promise<void>(resolve=>{el.onload=()=>resolve();el.onerror=()=>resolve()}); media.set(l.id,el);
    }
  }
  const ac=new AudioContext();
  const destination=ac.createMediaStreamDestination();
  const audioNodes:any[]=[];
  const gainNodes=new Map<string,GainNode>();
  for(const l of opts.layers.filter(x=>x.kind==="video"||x.kind==="audio")) {
    const el=media.get(l.id); if(!el || !(el instanceof HTMLMediaElement)) continue;
    const node=ac.createMediaElementSource(el); const gain=ac.createGain(); node.connect(gain).connect(destination); gainNodes.set(l.id,gain); audioNodes.push(node);
  }
  const stream=video.captureStream(opts.fps);
  destination.stream.getAudioTracks().forEach(t=>stream.addTrack(t));
  const rec=new MediaRecorder(stream,{mimeType:"video/webm;codecs=vp9,opus",videoBitsPerSecond:(opts.bitrate||12)*1000000});
  const chunks:Blob[]=[];
  rec.ondataavailable=e=>{if(e.data.size)chunks.push(e.data)};
  for(const l of media.values()) if(l instanceof HTMLMediaElement){l.currentTime=0;l.playbackRate=1;l.muted=false;}
  const start=performance.now();
  rec.start(250);
  for(let frame=0;frame<Math.ceil(opts.duration*opts.fps);frame++){
    const t=frame/opts.fps; ctx.fillStyle="#080808";ctx.fillRect(0,0,opts.width,opts.height);
    for(const l of opts.layers.filter(x=>x.visible!==false && t>=x.start && t<=x.start+x.duration)){
      const m=media.get(l.id)||null;
      const gain=gainNodes.get(l.id); if(gain){const local=t-l.start; const fi=Math.min(1,Math.max(0,local/.35)); const fo=Math.min(1,Math.max(0,(l.duration-local)/.35)); gain.gain.value=Math.max(0,Math.min(1,fi,fo));}
      if(m instanceof HTMLMediaElement){const local=Math.max(0,(t-l.start)*(l.speed||1)); if(isFinite(local) && Math.abs(m.currentTime-local)>0.08) m.currentTime=Math.min(local,Math.max(0,(m.duration||local)-0.02));}
      opts.drawLayer(ctx,l,t,m);
    }
    opts.onProgress?.(frame/Math.max(1,Math.ceil(opts.duration*opts.fps)));
    await new Promise(r=>requestAnimationFrame(r));
  }
  await new Promise<void>(resolve=>{rec.onstop=()=>resolve();rec.stop()});
  audioNodes.forEach(n=>{try{n.disconnect()}catch{}}); await ac.close();
  const webm=new Blob(chunks,{type:"video/webm"});
  const f=await loadMediaEngine(opts.onProgress);
  await f.writeFile("project.webm",await fetchFile(URL.createObjectURL(webm)));
  await f.exec(["-i","project.webm","-c:v","libx264","-preset","veryfast","-pix_fmt","yuv420p","-r",String(opts.fps),"-b:v",String(opts.bitrate||12)+"M","-c:a","aac","-b:a","192k","-movflags","+faststart","project.mp4"]);
  const out=await f.readFile("project.mp4"); await f.deleteFile("project.webm"); await f.deleteFile("project.mp4");
  return new Blob([out],{type:"video/mp4"});
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
