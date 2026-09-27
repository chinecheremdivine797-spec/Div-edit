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

function kfExpr(k:any[]|undefined, base:number, start:number, duration:number){
  const ks=(k||[]).slice().sort((a,b)=>a.time-b.time).map(x=>({t:Math.max(0,Math.min(duration,Number(x.time)-start)),v:Number(x.value)}));
  if(!ks.length)return String(base);
  let e=String(ks[ks.length-1].v);
  for(let i=ks.length-2;i>=0;i--){const a=ks[i],b=ks[i+1],span=Math.max(.0001,b.t-a.t);e='if(between(t,'+a.t+','+b.t+'),'+a.v+'+('+b.v+'-'+a.v+')*(t-'+a.t+')/'+span+','+e+')';}
  return ks[0].t>0?'if(lt(t,'+ks[0].t+'),'+base+','+e+')':e;
}

function alphaExpr(l:any,d:number){
  const tr=l.transition||{}; const td=Math.max(.001,Number(tr.duration)||0);
  const s='if(lt(t,'+td+'),t/'+td+',1)'; const e='if(gt(t,'+(d-td)+'),('+d+'-t)/'+td+',1)';
  if(tr.type==='fade'||tr.type==='dissolve')return 'min('+s+','+e+')';
  if(tr.type==='wipe')return 'if(lt(t,'+td+'),if(lt(X,W*t/'+td+'),1,0),if(gt(t,'+(d-td)+'),if(gt(X,W*(1-(t-'+(d-td)+')/'+td+')),1,0),1))';
  if(tr.type==='zoom')return 'min('+s+','+e+')';
  return '1';
}

function maskFilter(m:any){
  if(!m||m.type==='none')return ''; const f=Math.max(0,Number(m.feather)||0);
  if(m.type==='circle'){const r='min(W,H)*.45',d='sqrt((X-W/2)*(X-W/2)+(Y-H/2)*(Y-H/2))';return f?"geq=lum='lum(X,Y)':a='if(lt("+d+","+r+"-"+f+"),alpha(X,Y),if(gt("+d+","+r+"),0,alpha(X,Y)*("+r+"-"+d+")/"+f+"))'":"geq=lum='lum(X,Y)':a='if(lt("+d+","+r+"),alpha(X,Y),0)'";}
  if(m.type==='rectangle')return f?"geq=lum='lum(X,Y)':a='if(gt(X,"+f+"),if(lt(X,W-"+f+"),if(gt(Y,"+f+"),if(lt(Y,H-"+f+"),alpha(X,Y),alpha(X,Y)*(H-Y)/"+f+"),alpha(X,Y)*Y/"+f+"),alpha(X,Y)*(W-X)/"+f+"),alpha(X,Y)*X/"+f+")'":"geq=lum='lum(X,Y)':a='if(between(X,0,W),if(between(Y,0,H),alpha(X,Y),0),0)'";
  if(m.type==='split')return "geq=lum='lum(X,Y)':a='if(lt(X,W/2),alpha(X,Y),0)'";
  if(m.type==='line')return "geq=lum='lum(X,Y)':a='if(gt(Y,H/2),alpha(X,Y),0)'";
  return '';
}

function richColor(a:any){const x=a||{};const o:string[]=[];const b=(Number(x.brightness)||0)/100,c=Number.isFinite(Number(x.contrast))?Number(x.contrast):1,s=Number.isFinite(Number(x.saturation))?Number(x.saturation):1;if(b||c!==1||s!==1)o.push('eq=brightness='+b+':contrast='+c+':saturation='+s);if(Number(x.blur)>0)o.push('boxblur='+Math.min(30,Number(x.blur)));if(Number(x.hue))o.push('hue=h='+Number(x.hue));if(x.grayscale)o.push('hue=s=0');if(Number(x.sharpen)>0)o.push('unsharp=5:5:'+Math.min(5,Number(x.sharpen))+':5:5:0');if(Number(x.vignette)>0)o.push('vignette=PI/4');return o;}

function vfxSvg(l:any,w:number,h:number){const c=String(l.color||'#8de8ff').replace(/&/g,'&amp;').replace(/"/g,'&quot;');const e=String(l.effect||'Fire').toLowerCase();if(e.includes('lightning'))return '<svg xmlns="http://www.w3.org/2000/svg" width="'+w+'" height="'+h+'"><path d="M'+w*.18+' '+h*.55+' L'+w*.38+' '+h*.32+' L'+w*.48+' '+h*.56+' L'+w*.65+' '+h*.28+' L'+w*.82+' '+h*.5+'" fill="none" stroke="'+c+'" stroke-width="'+Math.max(10,w*.012)+'" stroke-linecap="round"/></svg>';if(e.includes('smoke')||e.includes('fog')||e.includes('dust'))return '<svg xmlns="http://www.w3.org/2000/svg" width="'+w+'" height="'+h+'"><g fill="#d0d0d0" opacity=".3">'+Array.from({length:10},(_,i)=>'<circle cx="'+w*(.15+i*.08)+'" cy="'+h*(.35+(i%4)*.08)+'" r="'+w*.06+'"/>').join('')+'</g></svg>';return '<svg xmlns="http://www.w3.org/2000/svg" width="'+w+'" height="'+h+'"><defs><radialGradient id="r"><stop offset="0" stop-color="#fff"/><stop offset=".2" stop-color="'+c+'"/><stop offset=".55" stop-color="#ff6420"/><stop offset="1" stop-color="transparent"/></radialGradient></defs><circle cx="'+w/2+'" cy="'+h/2+'" r="'+Math.min(w,h)*.3+'" fill="url(#r)"/></svg>';}

export async function renderProject(opts:{layers:any[];duration:number;fps:number;width:number;height:number;bitrate?:number;onProgress?:(value:number)=>void;drawLayer?:(g:CanvasRenderingContext2D,l:any,time:number,media:any)=>void;}){
  const f=await loadMediaEngine(opts.onProgress); const active=opts.layers.filter(l=>l.visible!==false&&['video','image','audio','text','vfx','shape'].includes(l.kind));
  const inputs:string[]=[]; const files:string[]=[]; const indexes=new Map<string,number>();
  const writeSvg=async(n:string,s:string)=>{const u=URL.createObjectURL(new Blob([s],{type:'image/svg+xml'}));await f.writeFile(n,await fetchFile(u));URL.revokeObjectURL(u);};
  for(const l of active){
    if(l.src){const ext=l.kind==='image'?'png':(String(l.src).toLowerCase().includes('.mov')?'mov':'mp4');const n='in_'+String(l.id).replace(/[^a-zA-Z0-9_-]/g,'_')+'.'+ext;await f.writeFile(n,await fetchFile(l.src));files.push(n);inputs.push('-i',n);indexes.set(l.id,inputs.filter(x=>x==='-i').length-1);}
    else if(l.kind==='text'||l.kind==='shape'||l.kind==='vfx'){const n='overlay_'+String(l.id).replace(/[^a-zA-Z0-9_-]/g,'_')+'.svg';let s='';if(l.kind==='vfx')s=vfxSvg(l,opts.width,opts.height);else{const fill=String(l.color||'#fff').replace(/"/g,'&quot;');s='<svg xmlns="http://www.w3.org/2000/svg" width="'+opts.width+'" height="'+opts.height+'"><rect width="100%" height="100%" fill="'+fill+'" opacity="'+(l.kind==='shape'?Number(l.opacity)||1:0)+'"/>'+(l.kind==='text'?'<text x="50%" y="50%" text-anchor="middle" dominant-baseline="middle" font-family="Arial" font-size="'+Math.max(24,Math.round(opts.height*.07))+'" font-weight="900" fill="'+fill+'">'+String(l.name||'DIV EDIT TEXT')+'</text>':'')+'</svg>';}await writeSvg(n,s);files.push(n);inputs.push('-loop','1','-i',n);indexes.set(l.id,inputs.filter(x=>x==='-i').length-1);}
  }
  if(!inputs.length)throw new Error('No renderable layers');
  const fc:string[]=[];const vids:string[]=[];const auds:string[]=[];
  for(const l of active){const idx=indexes.get(l.id);if(idx===undefined)continue;const start=Math.max(0,Number(l.start)||0),dur=Math.max(.01,Number(l.duration)||1),local=Math.min(dur,Math.max(.01,opts.duration-start));
    if(l.kind==='audio'){fc.push('['+idx+':a]atrim=duration='+local+','+atempoChain(Number(l.speed)||1)+',adelay='+Math.round(start*1000)+':all=1[a'+idx+']');auds.push('[a'+idx+']');continue;}
    const kx=kfExpr(l.keyframes?.x,l.x,start,dur),ky=kfExpr(l.keyframes?.y,l.y,start,dur),ks=kfExpr(l.keyframes?.scale,l.scale,start,dur),kr=kfExpr(l.keyframes?.rotation,l.rotation,start,dur),ko=kfExpr(l.keyframes?.opacity,l.opacity,start,dur);
    const fs:string[]=[];if(l.kind==='video'){fs.push('setpts=PTS/'+Math.max(.1,Math.min(100,Number(l.speed)||1)));if(l.effect==='Reverse')fs.push('reverse');if(l.effect==='Freeze')fs.push('tpad=stop_mode=clone:stop_duration=2');}else fs.push('tpad=stop_mode=clone:stop_duration='+local);
    const cr=l.crop||{x:0,y:0,width:1,height:1};if(Number(cr.width)<.999||Number(cr.height)<.999||Number(cr.x)!==0||Number(cr.y)!==0)fs.push("crop=w='iw*"+Math.max(.01,Math.min(1,Number(cr.width)||1))+"':h='ih*"+Math.max(.01,Math.min(1,Number(cr.height)||1))+"':x='iw*"+Math.max(0,Math.min(1,Number(cr.x)||0))+"':y='ih*"+Math.max(0,Math.min(1,Number(cr.y)||0))+"'");
    fs.push("scale=w='max(2,iw*"+ks+")':h='-1':force_original_aspect_ratio=decrease","scale=w='min(iw,"+opts.width+")':h='min(ih,"+opts.height+" )'");
    fs.push("format=rgba,colorchannelmixer=aa='"+ko+"*"+alphaExpr(l,local)+"'");fs.push(...richColor(l.colorAdjust));const mf=maskFilter(l.mask);if(mf)fs.push(mf);fs.push('setpts=PTS-STARTPTS');
    const label='v'+idx;fc.push('['+idx+':v]'+fs.join(',')+'['+label+']');const x='('+opts.width+'-overlay_w)/2+('+kx+'-'+opts.width+'/2)',y='('+opts.height+'-overlay_h)/2+('+ky+'-'+opts.height+'/2)';vids.push({label,x,y,rotation:kr} as any);
  }
  fc.push('color=c=black:s='+opts.width+'x'+opts.height+':r='+opts.fps+':d='+opts.duration+'[base]');let cur='[base]';let n=0;
  for(const v of vids){const out='comp'+(++n);fc.push(cur+'['+v.label+']overlay=x=\''+v.x+'\':y=\''+v.y+'\':eval=frame:eof_action=pass:shortest=0['+out+']');cur='['+out+']';}
  fc.push(cur+'format=yuv420p[vout]');if(auds.length)fc.push(auds.join('')+'amix=inputs='+auds.length+':duration=longest:dropout_transition=2,atrim=duration='+opts.duration+'[aout]');else fc.push('anullsrc=r=48000:cl=stereo,atrim=duration='+opts.duration+'[aout]');
  const args=[...inputs,'-filter_complex',fc.join(';'),'-map','[vout]','-map','[aout]','-t',String(opts.duration),'-r',String(opts.fps),'-c:v','libx264','-preset','veryfast','-pix_fmt','yuv420p','-b:v',String(Math.max(2,opts.bitrate||12))+'M','-c:a','aac','-b:a','192k','-movflags','+faststart','project.mp4'];
  await f.exec(args);const out=await f.readFile('project.mp4');for(const n of files){try{await f.deleteFile(n)}catch{}}try{await f.deleteFile('project.mp4')}catch{}return new Blob([out],{type:'video/mp4'});
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
