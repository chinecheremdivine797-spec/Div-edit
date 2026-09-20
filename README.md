# Magic Editor

Manual-only browser video editor and VFX compositor. No AI features or AI dependencies.

Preserved from DIV EDIT: media import/library workflow, multi-track timeline, trim/split/join workflow, audio, cinematic/VFX controls, preview, export architecture, projects/drafts, saved presets and watermark/logo controls.

Removed: AI Director, AI tools, AI VFX, AI generation, AI voice, AI captions, AI avatars, AI editing, model/API integrations and AI-specific UI/data models.

## Run
npm install
npm run dev

## Architecture
UI/state/timeline/effects/magic/export are separated conceptually in the source. Browser export uses MediaRecorder/WebM; an isolated renderer interface can later be backed by FFmpeg for MP4.
