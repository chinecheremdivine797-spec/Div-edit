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
  onProgress?:(value:number)=>void;
}) {
  const f=await loadMediaEngine(opts.onProgress);
  const active=opts.layers.filter(l=>l.visible!==false && ["video","image","audio","text","vfx","shape"].includes(l.kind));
  const files:string[]=[]; const inputArgs:string[]=[];
  const num=(v:any,d:number)=>Number.isFinite(Number(v))?Number(v):d;
  const esc=(v:string)=>v.replace(/\\/g,"\\\\").replace(/'/g,"\\'");
  const writeSvg=async(name:string,svg:string)=>{const blob=new Blob([svg],{type:"image/svg+xml"});await f.writeFile(name,await fetchFile(URL.createObjectURL(blob)));};
  for(const l of active){
    if(l.src){
      const ext=l.kind==="image"?"png":(String(l.src).toLowerCase().includes(".mov")?"mov":"mp4");
      const name="in_"+l.id.replace(/[^a-zA-Z0-9_-]/g,"_")+"."+ext;
      await f.writeFile(name,await fetchFile(l.src)); files.push(name); inputArgs.push("-i",name); l.__input=inputArgs.length/2-1;
    } else if(l.kind==="text"||l.kind==="shape"||l.kind==="vfx"){
      const name="overlay_"+l.id.replace(/[^a-zA-Z0-9_-]/g,"_")+".svg";
      const text=l.kind==="text"?esc(String(l.name||"DIV EDIT TEXT")):""; const fill=esc(l.color||"#ffffff"); const vfx=l.kind==="vfx";
      const svg='<svg xmlns="http://www.w3.org/2000/svg" width="'+opts.width+'" height="'+opts.height+'"><rect width="100%" height="100%" fill="'+fill+'" opacity="'+(l.kind==="shape"?num(l.opacity,1):0)+'"/>'+(text?'<text x="50%" y="50%" text-anchor="middle" dominant-baseline="middle" font-family="Arial" font-size="'+Math.max(24,Math.round(opts.height*.07))+'" font-weight="900" fill="'+fill+'">'+text+'</text>':"")+(vfx?'<circle cx="50%" cy="50%" r="'+Math.min(opts.width,opts.height)*.28+'" fill="none" stroke="'+fill+'" stroke-width="28" opacity=".8"/><circle cx="50%" cy="50%" r="'+Math.min(opts.width,opts.height)*.12+'" fill="'+fill+'" opacity=".28"/>':"")+'</svg>';
      await writeSvg(name,svg); files.push(name); inputArgs.push("-loop","1","-i",name); l.__input=inputArgs.length/2-1;
    }
  }
  if(!inputArgs.length) throw new Error("No renderable layers");
  const filter:string[]=[]; const videos:string[]=[]; const audios:string[]=[];
  for(const l of active){
    const idx=l.__input; if(idx===undefined) continue;
    const start=Math.max(0,num(l.start,0)), dur=Math.max(.01,num(l.duration,1)), end=Math.min(opts.duration,start+dur);
    if(l.kind==="audio"){
      const af=["aresample=async=1",atempoChain(Math.max(.1,Math.min(100,num(l.speed,1)))),"adelay="+Math.round(start*1000)+":all=1"].join(",");
      filter.push("["+idx+":a]"+af+"[a"+idx+"]"); audios.push("[a"+idx+"]"); continue;
    }
    const scale=num(l.scale,1), opacity=Math.max(0,Math.min(1,num(l.opacity,1)));
    const adj=l.colorAdjust||{}; const brightness=num(adj.brightness,0)/100, contrast=num(adj.contrast,1), saturation=num(adj.saturation,1), blur=num(adj.blur,0);
    const fs:string[]=[];
    if(l.kind==="video") fs.push("setpts=PTS/"+Math.max(.1,Math.min(100,num(l.speed,1))));
    fs.push("fps="+opts.fps,"scale="+Math.max(2,Math.round(opts.width*scale))+":-2:force_original_aspect_ratio=decrease","pad="+opts.width+":"+opts.height+":(ow-iw)/2:(oh-ih)/2:color=black");
    if(l.kind!=="video") fs.push("tpad=stop_mode=clone:stop_duration="+Math.max(0,end-start));
    if(l.effect==="Reverse"&&l.kind==="video") fs.push("reverse");
    if(l.effect==="Freeze"&&l.kind==="video") fs.push("tpad=stop_mode=clone:stop_duration=2");
    if(brightness||contrast!==1||saturation!==1) fs.push("eq=brightness="+brightness+":contrast="+contrast+":saturation="+saturation);
    if(blur>0) fs.push("boxblur="+Math.min(20,blur));
    if(opacity<1) fs.push("format=rgba,colorchannelmixer=aa="+opacity);
    if(l.mask?.type==="circle") fs.push("geq=lum='lum(X,Y)':a='if(gt((X-W/2)^2+(Y-H/2)^2,(min(W,H)*.42)^2),0,alpha(X,Y))'");
    fs.push("setpts=PTS-STARTPTS+"+start+"/TB");
    const label="v"+idx; filter.push("["+idx+":v]"+fs.join(",")+"["+label+"]"); videos.push("["+label+"]");
  }
  if(!videos.length){filter.push("color=c=black:s="+opts.width+"x"+opts.height+":r="+opts.fps+":d="+opts.duration+"[base]");videos.push("[base]");}
  let cur=videos[0];
  for(let i=1;i<videos.length;i++){const out="comp"+i;filter.push(cur+videos[i]+"overlay=eof_action=pass:shortest=0["+out+"]");cur="["+out+"]";}
  filter.push(cur+"format=yuv420p[vout]");
  if(audios.length) filter.push(audios.join("")+"amix=inputs="+audios.length+":duration=longest:dropout_transition=2,atrim=duration="+opts.duration+"[aout]");
  else filter.push("anullsrc=r=48000:cl=stereo,atrim=duration="+opts.duration+"[aout]");
  const args=[...inputArgs,"-filter_complex",filter.join(";"),"-map","[vout]","-map","[aout]","-t",String(opts.duration),"-r",String(opts.fps),"-c:v","libx264","-preset","veryfast","-pix_fmt","yuv420p","-b:v",String(Math.max(2,opts.bitrate||12))+"M","-c:a","aac","-b:a","192k","-movflags","+faststart","project.mp4"];
  await f.exec(args); const out=await f.readFile("project.mp4");
  for(const name of files){try{await f.deleteFile(name)}catch{}} try{await f.deleteFile("project.mp4")}catch{}
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
