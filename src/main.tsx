import React, { useEffect, useMemo, useRef, useState } from "react";
import { createRoot } from "react-dom/client";
import {
  Play, Pause, Square, Upload, Save, Undo2, Redo2, Scissors, Trash2,
  Sparkles, Layers, Download, Maximize, Plus, Eye, Lock
} from "lucide-react";
import "./styles.css";

type Keyframe = { time: number; value: number };
type LayerKind = "video" | "image" | "audio" | "text" | "vfx";
type Layer = {
  id: string; name: string; kind: LayerKind; src?: string;
  start: number; duration: number; visible: boolean; locked: boolean;
  x: number; y: number; scale: number; rotation: number; opacity: number;
  blend: string; effect?: string; keyframes: Record<string, Keyframe[]>;
};
type Project = { name: string; width: number; height: number; fps: number; duration: number; layers: Layer[] };

const makeId = () => crypto.randomUUID();
const initialProject = (): Project => ({
  name: "Magic Project", width: 1080, height: 1920, fps: 30, duration: 10, layers: []
});

const magicEffects = [
  "Fire", "Fireball", "Fire in Hand", "Fire Trail", "Fire Weapon",
  "Fire Explosion", "Embers", "Sparks", "Lightning", "Electric Hand",
  "Lightning Beam", "Energy Ball", "Energy Beam", "Energy Blast",
  "Energy Shield", "Smoke", "Fog", "Dust", "Magic Portal",
  "Fire Portal", "Lightning Portal", "Magic Circle", "Shockwave",
  "Glow", "Motion Trail", "Camera Shake"
];

function App() {
  const [project, setProject] = useState<Project>(initialProject);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [time, setTime] = useState(0);
  const [playing, setPlaying] = useState(false);
  const [history, setHistory] = useState<Project[]>([]);
  const [future, setFuture] = useState<Project[]>([]);
  const [tab, setTab] = useState("Magic");
  const [zoom, setZoom] = useState(1);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const videos = useRef<Record<string, HTMLVideoElement | null>>({});

  const selected = project.layers.find(layer => layer.id === selectedId);

  const commit = (next: Project) => {
    setHistory(h => [...h, project].slice(-50));
    setFuture([]);
    setProject(next);
  };

  useEffect(() => {
    if (!playing) return;
    let frame = 0;
    const tick = () => {
      setTime(current => {
        const next = current + 1 / project.fps;
        if (next >= project.duration) {
          setPlaying(false);
          return 0;
        }
        return next;
      });
      frame = requestAnimationFrame(tick);
    };
    frame = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(frame);
  }, [playing, project.fps, project.duration]);

  useEffect(() => { draw(); }, [project, time, selectedId]);

  const draw = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = "#05070b";
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    const scale = Math.min(canvas.width / project.width, canvas.height / project.height);
    ctx.save();
    ctx.translate(
      (canvas.width - project.width * scale) / 2,
      (canvas.height - project.height * scale) / 2
    );
    ctx.scale(scale, scale);

    project.layers
      .filter(layer => layer.visible && time >= layer.start && time <= layer.start + layer.duration)
      .forEach(layer => {
        ctx.save();
        ctx.globalAlpha = layer.opacity;
        ctx.globalCompositeOperation =
          layer.blend === "Add" ? "lighter" :
          layer.blend === "Screen" ? "screen" :
          layer.blend === "Multiply" ? "multiply" : "source-over";

        ctx.translate(layer.x, layer.y);
        ctx.rotate(layer.rotation * Math.PI / 180);
        ctx.scale(layer.scale, layer.scale);

        if (layer.kind === "text") {
          ctx.fillStyle = "#fff";
          ctx.font = "bold 80px Arial";
          ctx.textAlign = "center";
          ctx.shadowBlur = 18;
          ctx.shadowColor = "#7d6cff";
          ctx.fillText(layer.name, 0, 0);
        } else if (layer.kind === "vfx") {
          drawEffect(ctx, layer.effect || "Fire", 0, 0, time);
        } else {
          const video = videos.current[layer.id];
          if (video && video.readyState >= 2) {
            ctx.drawImage(video, -project.width / 2, -project.height / 2, project.width, project.height);
          }
        }
        ctx.restore();
      });

    ctx.restore();
  };

  const drawEffect = (
    ctx: CanvasRenderingContext2D,
    effect: string,
    x: number,
    y: number,
    t: number
  ) => {
    ctx.save();
    ctx.translate(x, y);
    const pulse = 0.8 + 0.2 * Math.sin(t * 8);

    if (effect.includes("Lightning")) {
      ctx.strokeStyle = "#9deaff";
      ctx.lineWidth = 14;
      ctx.shadowBlur = 35;
      ctx.shadowColor = "#48cfff";
      ctx.beginPath();
      for (let i = 0; i < 9; i++) {
        ctx.lineTo((i - 4) * 55, (i % 2 ? 75 : -75) * pulse);
      }
      ctx.stroke();
    } else if (effect.includes("Circle") || effect.includes("Portal")) {
      ctx.strokeStyle = "#66d8ff";
      ctx.lineWidth = 12;
      ctx.shadowBlur = 30;
      ctx.shadowColor = "#55cfff";
      for (let radius = 220; radius > 30; radius -= 38) {
        ctx.beginPath();
        ctx.arc(0, 0, radius, 0, Math.PI * 2);
        ctx.stroke();
      }
    } else {
      const gradient = ctx.createRadialGradient(0, 0, 10, 0, 0, 240);
      gradient.addColorStop(0, "#fff");
      gradient.addColorStop(0.2, "#ffe06b");
      gradient.addColorStop(0.55, "#ff641c");
      gradient.addColorStop(1, "transparent");
      ctx.fillStyle = gradient;
      ctx.shadowBlur = 55;
      ctx.shadowColor = "#ff5b18";
      ctx.beginPath();
      ctx.arc(0, 0, 230 * pulse, 0, Math.PI * 2);
      ctx.fill();
    }
    ctx.restore();
  };

  const addLayer = (kind: LayerKind, name: string, src?: string) => {
    const layer: Layer = {
      id: makeId(), name, kind, src, start: time, duration: 3,
      visible: true, locked: false, x: project.width / 2, y: project.height / 2,
      scale: 1, rotation: 0, opacity: 1,
      blend: kind === "vfx" ? "Add" : "Normal", keyframes: {}
    };
    commit({
      ...project,
      layers: [...project.layers, layer],
      duration: Math.max(project.duration, time + 3)
    });
    setSelectedId(layer.id);
  };

  const importFiles = (event: React.ChangeEvent<HTMLInputElement>) => {
    Array.from(event.target.files || []).forEach(file => {
      const kind: LayerKind =
        file.type.startsWith("audio") ? "audio" :
        file.type.startsWith("image") ? "image" : "video";
      addLayer(kind, file.name, URL.createObjectURL(file));
    });
    event.target.value = "";
  };

  const split = () => {
    if (!selected) return;
    const elapsed = time - selected.start;
    if (elapsed <= 0 || elapsed >= selected.duration) return;
    const first: Layer = { ...selected, duration: elapsed };
    const second: Layer = {
      ...selected, id: makeId(), start: time, duration: selected.duration - elapsed
    };
    commit({
      ...project,
      layers: project.layers.flatMap(layer =>
        layer.id === selected.id ? [first, second] : [layer]
      )
    });
  };

  const removeSelected = () => {
    if (!selectedId) return;
    commit({ ...project, layers: project.layers.filter(layer => layer.id !== selectedId) });
    setSelectedId(null);
  };

  const saveProject = () => {
    const data = JSON.stringify({
      ...project,
      layers: project.layers.map(({ src, ...layer }) => layer)
    }, null, 2);
    const link = document.createElement("a");
    link.href = URL.createObjectURL(new Blob([data], { type: "application/json" }));
    link.download = "magic-editor-project.json";
    link.click();
  };

  const undo = () => {
    if (!history.length) return;
    setFuture(current => [project, ...current]);
    setProject(history[history.length - 1]);
    setHistory(current => current.slice(0, -1));
  };

  const redo = () => {
    if (!future.length) return;
    setHistory(current => [...current, project]);
    setProject(future[0]);
    setFuture(current => current.slice(1));
  };

  const exportVideo = async () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const recorder = new MediaRecorder(canvas.captureStream(project.fps), { mimeType: "video/webm" });
    const chunks: Blob[] = [];
    recorder.ondataavailable = event => chunks.push(event.data);
    recorder.onstop = () => {
      const link = document.createElement("a");
      link.href = URL.createObjectURL(new Blob(chunks, { type: "video/webm" }));
      link.download = "magic-editor.webm";
      link.click();
    };
    setTime(0);
    setPlaying(true);
    recorder.start();
    window.setTimeout(() => {
      recorder.stop();
      setPlaying(false);
    }, project.duration * 1000 + 300);
  };

  const groups = useMemo<Record<string, string[]>>(() => ({
    Media: ["Import Media"],
    Effects: ["Glow", "Motion Blur", "Camera Shake", "Flash", "Ripple", "Warp", "Radial Blur", "Chromatic Aberration"],
    Magic: magicEffects,
    Layers: ["Duplicate", "Reorder", "Lock", "Hide"],
    Text: ["Add Text", "Fade", "Slide", "Zoom", "Typewriter", "Bounce", "Shake"],
    Audio: ["Music", "Sound Effects", "Volume", "Fade In", "Fade Out"],
    Transitions: ["Fade", "Dissolve", "Flash", "Zoom", "Spin", "Swipe", "Glitch", "Blur", "Light Burst", "Portal"],
    Masks: ["Rectangle", "Circle", "Polygon", "Free Draw"],
    Tracking: ["Position", "Scale", "Rotation"],
    Keyframes: ["Position", "Scale", "Rotation", "Opacity", "Blur", "Glow", "Intensity"]
  }), []);

  const handleTool = (tool: string) => {
    if (tool === "Import Media") {
      document.querySelector<HTMLInputElement>('input[type="file"]')?.click();
    } else if (tool === "Add Text") {
      addLayer("text", "MAGIC TEXT");
    } else if (magicEffects.includes(tool)) {
      addLayer("vfx", tool);
    }
  };

  return (
    <div className="app">
      <header>
        <b><Sparkles /> MAGIC EDITOR</b>
        <button onClick={() => setProject(initialProject())}>New Project</button>
        <label>
          <Upload /> Import
          <input hidden multiple type="file" accept="video/*,image/*,audio/*" onChange={importFiles} />
        </label>
        <button onClick={saveProject}><Save /> Save</button>
        <button onClick={undo}><Undo2 /></button>
        <button onClick={redo}><Redo2 /></button>
        <span className="grow" />
        <button onClick={() => document.documentElement.requestFullscreen()}><Maximize /></button>
        <button className="primary" onClick={exportVideo}><Download /> Export</button>
      </header>

      <div className="body">
        <aside className="left">
          {Object.keys(groups).map(group => (
            <button className={tab === group ? "chosen" : ""} onClick={() => setTab(group)} key={group}>
              <Layers /> {group}
            </button>
          ))}
          <div className="tools">
            {groups[tab].map(tool => (
              <button key={tool} onClick={() => handleTool(tool)}>
                <Plus /> {tool}
              </button>
            ))}
          </div>
        </aside>

        <main>
          <div className="preview">
            <canvas ref={canvasRef} width={540} height={960} />
            <div className="controls">
              <button onClick={() => setPlaying(value => !value)}>
                {playing ? <Pause /> : <Play />}
              </button>
              <button onClick={() => { setPlaying(false); setTime(0); }}><Square /></button>
              <button onClick={() => setTime(value => Math.max(0, value - 1 / project.fps))}>◀</button>
              <button onClick={() => setTime(value => Math.min(project.duration, value + 1 / project.fps))}>▶</button>
              <input
                type="range"
                min="0"
                max={project.duration}
                step={1 / project.fps}
                value={time}
                onChange={event => setTime(Number(event.target.value))}
              />
              <span>{time.toFixed(2)}s</span>
            </div>
          </div>
        </main>

        <aside className="right">
          <h3>INSPECTOR</h3>
          {selected ? (
            <>
              <strong>{selected.name}</strong>
              {[
                ["x", "Position X", 0, 1080],
                ["y", "Position Y", 0, 1920],
                ["scale", "Scale", 0.1, 4],
                ["rotation", "Rotation", -360, 360],
                ["opacity", "Opacity", 0, 1]
              ].map(([key, label, min, max]) => (
                <label className="field" key={key}>
                  {label}
                  <input
                    type="range"
                    min={min}
                    max={max}
                    step={key === "opacity" ? 0.01 : 0.1}
                    value={(selected as any)[key]}
                    onChange={event => {
                      const value = Number(event.target.value);
                      setProject(current => ({
                        ...current,
                        layers: current.layers.map(layer =>
                          layer.id === selected.id ? { ...layer, [key]: value } : layer
                        )
                      }));
                    }}
                  />
                </label>
              ))}
              <select
                value={selected.blend}
                onChange={event => commit({
                  ...project,
                  layers: project.layers.map(layer =>
                    layer.id === selected.id ? { ...layer, blend: event.target.value } : layer
                  )
                })}
              >
                {["Normal", "Screen", "Add", "Lighten", "Overlay", "Soft Light", "Multiply", "Darken", "Color Dodge"]
                  .map(mode => <option key={mode}>{mode}</option>)}
              </select>
              <button onClick={() => {
                const keyframe = { time, value: selected.opacity };
                commit({
                  ...project,
                  layers: project.layers.map(layer =>
                    layer.id === selected.id
                      ? { ...layer, keyframes: {
                          ...layer.keyframes,
                          opacity: [...(layer.keyframes.opacity || []), keyframe]
                        }}
                      : layer
                  )
                });
              }}>Add Keyframe</button>
              <button onClick={split}><Scissors /> Split</button>
              <button onClick={removeSelected}><Trash2 /> Delete</button>
            </>
          ) : <p>Select a layer.</p>}
        </aside>
      </div>

      <section className="timeline">
        <div className="thead">
          <b>TIMELINE</b>
          <span>9:16</span>
          <span>{project.fps} FPS</span>
          <input type="range" min="0.5" max="3" step="0.1" value={zoom} onChange={event => setZoom(Number(event.target.value))} />
        </div>
        {project.layers.slice().reverse().map(layer => (
          <div
            className={"track " + (selectedId === layer.id ? "active" : "")}
            onClick={() => setSelectedId(layer.id)}
            key={layer.id}
          >
            <div className="name"><Eye /> <Lock /> {layer.name}</div>
            <div className="lane">
              <div
                className="clip"
                style={{
                  left: (layer.start / project.duration) * 100 * zoom + "%",
                  width: (layer.duration / project.duration) * 100 * zoom + "%"
                }}
              >
                {layer.name}
              </div>
            </div>
          </div>
        ))}
      </section>

      {project.layers.filter(layer => layer.kind === "video" && layer.src).map(layer => (
        <video
          key={layer.id}
          ref={video => { videos.current[layer.id] = video; }}
          src={layer.src}
          muted
          playsInline
          style={{ display: "none" }}
        />
      ))}
    </div>
  );
}

createRoot(document.getElementById("root")!).render(<App />);
