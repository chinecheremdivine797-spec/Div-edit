package com.example.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class AiToolCategory(val title: String, val description: String) {
    ALL("All AI Suite", "All 20+ specialized cinematic neural models"),
    VFX_COMPOSITING("VFX & Compositing", "Neural rotoscoping, sky replacement, inpainting & flares"),
    AUDIO_SPEECH("Audio & Dialogue", "ADR voice cloning, foley generation & speech isolation"),
    COLOR_LIGHTING("Color & Lighting", "3D face relighting, neural color match & vintage grading"),
    CAMERA_MOTION("Camera & Motion", "Optical slow-mo, camera shake & smart auto-framing"),
    EDITING_FINISHING("Editing & Finishing", "Beat sync, 8K neural upscaling, kinetic captions & script breakdown")
}

data class AiToolParameter(
    val id: String,
    val name: String,
    val value: Float,
    val minValue: Float = 0f,
    val maxValue: Float = 1f,
    val unit: String = "%",
    val description: String = ""
)

data class AiToolSpec(
    val id: String,
    val number: Int,
    val name: String,
    val shortName: String,
    val tagline: String,
    val description: String,
    val category: AiToolCategory,
    val iconKey: String,
    val accentColor: Long, // Hex color code
    val defaultParameters: List<AiToolParameter>,
    val processingSteps: List<String>,
    val outputEffectType: String,
    val executionTimeMs: Long = 1800L
)

object AiToolsRegistry {
    val tools: List<AiToolSpec> = listOf(
        AiToolSpec(
            id = "ai_face_relight",
            number = 1,
            name = "AI Face Relight & Studio Gaffer",
            shortName = "Face Relight",
            tagline = "Neural 3D Volumetric Actor Relighting",
            description = "Estimates facial 3D geometry and casts dynamic Key, Fill, and Rim lights on talent to fix on-set lighting or match moody cinema ambiance.",
            category = AiToolCategory.COLOR_LIGHTING,
            iconKey = "lightbulb",
            accentColor = 0xFFFFD700, // Gold
            defaultParameters = listOf(
                AiToolParameter("key_intensity", "Key Light Intensity", 0.85f),
                AiToolParameter("fill_ratio", "Fill Ratio", 0.40f),
                AiToolParameter("rim_warmth", "Rim Light Warmth", 0.70f),
                AiToolParameter("normal_smooth", "3D Normal Smoothing", 0.80f)
            ),
            processingSteps = listOf(
                "Detect facial landmarks and 3D mesh points",
                "Estimate surface normal vectors and depth coordinates",
                "Calculate Bidirectional Reflectance Distribution (BRDF)",
                "Synthesize high-dynamic-range light casting & shadows"
            ),
            outputEffectType = "COLOR_GRADE_LIGHTING"
        ),
        AiToolSpec(
            id = "ai_voice_clone_adr",
            number = 2,
            name = "AI Voice Clone & Studio ADR",
            shortName = "Voice Clone ADR",
            tagline = "Automated Dialogue Replacement & Voice Synthesis",
            description = "Replaces muffled or corrupted on-set actor lines with studio-grade vocal reproduction matching the actor's exact vocal timbre and emotional cadence.",
            category = AiToolCategory.AUDIO_SPEECH,
            iconKey = "record_voice_over",
            accentColor = 0xFF00E5FF, // Cyan
            defaultParameters = listOf(
                AiToolParameter("timbre_match", "Timbre Accuracy", 0.95f),
                AiToolParameter("room_absorb", "Acoustic Absorption", 0.80f),
                AiToolParameter("lip_sync", "Phoneme Lip-Sync Alignment", 0.90f),
                AiToolParameter("breath_humanize", "Natural Breathing Jitter", 0.60f)
            ),
            processingSteps = listOf(
                "Extract actor vocal spectral profile and formant peaks",
                "Synthesize replacement dialogue with neural vocoder",
                "Match room impulse response and proximity effect",
                "Quantize phoneme start times to mouth visual markers"
            ),
            outputEffectType = "AUDIO_TRACK_ADR"
        ),
        AiToolSpec(
            id = "ai_sky_replacement",
            number = 3,
            name = "AI Sky Replacement & Atmosphere",
            shortName = "Sky Replacement",
            tagline = "Neural Horizon Segmentation & Atmospheric Fog",
            description = "Sub-pixel masking of complex horizons (trees, buildings, wires) with dynamic replacement of overcast skies with Golden Hour, Stormy Twilight, or Sci-Fi Celestial skies.",
            category = AiToolCategory.VFX_COMPOSITING,
            iconKey = "wb_sunny",
            accentColor = 0xFFFF7675, // Sunset Coral
            defaultParameters = listOf(
                AiToolParameter("horizon_feather", "Horizon Edge Feather", 0.75f),
                AiToolParameter("ambient_cast", "Ground Spill Cast Light", 0.85f),
                AiToolParameter("fog_density", "Atmospheric Depth Fog", 0.50f),
                AiToolParameter("sun_flare", "Solar Anamorphic Flare", 0.70f)
            ),
            processingSteps = listOf(
                "Semantic sky segmentation at full floating-point precision",
                "Calculate horizon luminance gradient and occlusion mask",
                "Project high-resolution 360 HDR sky dome plate",
                "Color-bleed sky tones across landscape foreground geometry"
            ),
            outputEffectType = "VFX_CLIP_SKY"
        ),
        AiToolSpec(
            id = "ai_smart_rotoscope",
            number = 4,
            name = "AI Smart Rotoscope & Hair Matte",
            shortName = "Smart Rotoscope",
            tagline = "Green-Screen Free Fine-Strand Matting",
            description = "Automatically isolates moving human actors and props across thousands of frames with pixel-perfect hair transparency without green screen backdrops.",
            category = AiToolCategory.VFX_COMPOSITING,
            iconKey = "content_cut",
            accentColor = 0xFFA55EEA, // Violet
            defaultParameters = listOf(
                AiToolParameter("strand_detail", "Hair Strand Sensitivity", 0.90f),
                AiToolParameter("temporal_smooth", "Temporal Edge Anti-Flicker", 0.85f),
                AiToolParameter("defringe_radius", "Defringe Chroma Suppression", 0.70f),
                AiToolParameter("matte_choke", "Matte Choke & Expansion", 0.50f)
            ),
            processingSteps = listOf(
                "Neural semantic boundary detection across target silhouette",
                "Bilateral temporal filtering to lock inter-frame edges",
                "Sub-pixel alpha matte calculation with hair strand extraction",
                "Clean background plate separation with edge color un-mixing"
            ),
            outputEffectType = "VFX_CLIP_ROTOSCOPE"
        ),
        AiToolSpec(
            id = "ai_noise_reduction",
            number = 5,
            name = "AI Sensor Denoise & De-Bander",
            shortName = "Sensor Denoise",
            tagline = "Clean Low-Light Noise Without Texture Loss",
            description = "Removes high-ISO sensor speckles, digital color chroma noise, and 8-bit banding gradients while preserving authentic 35mm film grain and sharp clothing weave.",
            category = AiToolCategory.EDITING_FINISHING,
            iconKey = "tune",
            accentColor = 0xFF20BF6B, // Emerald
            defaultParameters = listOf(
                AiToolParameter("chroma_denoise", "Chroma Color Smoothing", 0.90f),
                AiToolParameter("luma_detail", "Luminance Detail Preservation", 0.85f),
                AiToolParameter("film_grain", "Authentic 35mm Grain Overlay", 0.65f),
                AiToolParameter("deband_dither", "Smooth Gradient De-Bander", 0.75f)
            ),
            processingSteps = listOf(
                "Spatial-temporal frequency decomposition of video stream",
                "Isolate stationary and dynamic sensor noise patterns",
                "Reconstruct edges and textures using deep wavelet residual filter",
                "Apply perceptual dither to eliminate banding steps"
            ),
            outputEffectType = "COLOR_GRADE_DENOISE"
        ),
        AiToolSpec(
            id = "ai_color_match_frame",
            number = 6,
            name = "AI Color Match From Reference",
            shortName = "Color Match",
            tagline = "Extract & Transfer Cinema Master Aesthetics",
            description = "Import any iconic movie still or lookbook image. The neural engine analyzes tone curves, halation, and saturation splits to reproduce the exact grading on your footage.",
            category = AiToolCategory.COLOR_LIGHTING,
            iconKey = "palette",
            accentColor = 0xFFFF9F43, // Amber
            defaultParameters = listOf(
                AiToolParameter("palette_weight", "Palette Transfer Strength", 0.85f),
                AiToolParameter("skin_protect", "Skin Tone Isolation Guard", 0.90f),
                AiToolParameter("contrast_curve", "Dynamic Range Black Level", 0.75f),
                AiToolParameter("color_split", "Shadow/Highlight Split Intensity", 0.80f)
            ),
            processingSteps = listOf(
                "Sample reference image luminance and chrominance 3D distribution",
                "Segment skin tone regions to prevent unwanted discoloration",
                "Calculate non-linear 64x64x64 3D Color LUT transfer matrix",
                "Apply smooth sigmoid contrast curve with shadow roll-off"
            ),
            outputEffectType = "COLOR_GRADE_MATCH"
        ),
        AiToolSpec(
            id = "ai_depth_map_bokeh",
            number = 7,
            name = "AI Depth Map & Anamorphic Bokeh",
            shortName = "Depth & Bokeh",
            tagline = "Monocular Depth Estimation & Optical Blur",
            description = "Generates continuous 16-bit Z-depth map from 2D video, allowing infinite post-shoot refocusing, anamorphic oval bokeh, and 3D camera layer projection.",
            category = AiToolCategory.VFX_COMPOSITING,
            iconKey = "camera",
            accentColor = 0xFF45AAF2, // Electric Blue
            defaultParameters = listOf(
                AiToolParameter("focal_depth", "Focal Plane Distance", 0.45f),
                AiToolParameter("aperture_blur", "Aperture Blur Radius (f/1.2)", 0.80f),
                AiToolParameter("anamorphic_ratio", "Anamorphic Squeeze 2x Oval", 0.90f),
                AiToolParameter("cat_eye_swirl", "Optical Cat-Eye Vignette Swirl", 0.60f)
            ),
            processingSteps = listOf(
                "Deep neural monocular depth map inference at frame cadence",
                "Identify focal target distance and depth of field thresholds",
                "Apply circle of confusion blur with physically modeled blades",
                "Deform background specular highlights into anamorphic ovals"
            ),
            outputEffectType = "VFX_CLIP_DEPTH"
        ),
        AiToolSpec(
            id = "ai_beat_sync_cut",
            number = 8,
            name = "AI Music & Beat Sync Cut",
            shortName = "Beat Sync Cut",
            tagline = "Automatic Rhythm & Transient Timeline Slicing",
            description = "Analyzes musical waveform downbeats, kick hits, and drop risers to automatically align video cuts, speed ramps, and impact transitions precisely on the beat.",
            category = AiToolCategory.EDITING_FINISHING,
            iconKey = "music_note",
            accentColor = 0xFFFC5C65, // Bright Red
            defaultParameters = listOf(
                AiToolParameter("beat_threshold", "Transient Sensitivity", 0.85f),
                AiToolParameter("cut_frequency", "Cut Density (Quarter / 8th Beat)", 0.60f),
                AiToolParameter("speed_ramp", "Speed Ramp Burst on Downbeat", 0.75f),
                AiToolParameter("flash_impact", "White Halation Flash on Beat", 0.40f)
            ),
            processingSteps = listOf(
                "Fourier transform audio track into spectral flux and transient envelope",
                "Detect primary tempo (BPM) and bar downbeat positions",
                "Quantize timeline clip cut boundaries to musical grid",
                "Generate easing curves for synchronized zoom and whip accents"
            ),
            outputEffectType = "TIMELINE_BEAT_EDIT"
        ),
        AiToolSpec(
            id = "ai_foley_synthesizer",
            number = 9,
            name = "AI Foley & Footstep Synthesizer",
            shortName = "Foley Synthesizer",
            tagline = "Motion-To-Sound Real-Time Generative Foley",
            description = "Watches character movement and on-screen interactions to generate custom organic sound effects (footsteps, cloth rustle, punch impacts, metallic clicks).",
            category = AiToolCategory.AUDIO_SPEECH,
            iconKey = "graphic_eq",
            accentColor = 0xFF26DE81, // Mint
            defaultParameters = listOf(
                AiToolParameter("surface_mat", "Surface Material (Concrete/Wood/Gravel)", 0.70f),
                AiToolParameter("motion_sync", "Motion Sync Timing Precision", 0.95f),
                AiToolParameter("reverb_space", "Environment Reverb Match", 0.65f),
                AiToolParameter("impact_weight", "Footstep Mass / Bass Thud", 0.80f)
            ),
            processingSteps = listOf(
                "Track physical contact points (shoes, hands, props) across scene",
                "Classify collision physics and surface texture semantics",
                "Synthesize high-sample-rate audio wave for every impact frame",
                "Position sound in stereo/5.1 surround space according to screen position"
            ),
            outputEffectType = "AUDIO_TRACK_FOLEY"
        ),
        AiToolSpec(
            id = "ai_voice_clean_dialogue",
            number = 10,
            name = "AI Voice Clean & Dialogue Isolate",
            shortName = "Voice Clean",
            tagline = "Strip Wind, Reverb & Traffic With Pure Vocals",
            description = "Zeroes out noisy generators, heavy street traffic, room echo, and howling wind while leaving human dialogue 100% natural, crisp, and broadcast-ready.",
            category = AiToolCategory.AUDIO_SPEECH,
            iconKey = "mic",
            accentColor = 0xFF2BCBBA, // Turquoise
            defaultParameters = listOf(
                AiToolParameter("isolation_gain", "Dialogue Isolation Power", 0.95f),
                AiToolParameter("wind_kill", "Wind Rumble Killer", 0.90f),
                AiToolParameter("dereverb", "Room Reverb Removal", 0.85f),
                AiToolParameter("sibilance_tame", "Harsh De-Esser Sibilance", 0.70f)
            ),
            processingSteps = listOf(
                "Decompose audio spectrogram into speech and background stems",
                "Apply deep recurrent neural network vocal extraction filter",
                "Eliminate reverberation decay tails and reflections",
                "Smooth harmonic overtones for broadcast voice clarity"
            ),
            outputEffectType = "AUDIO_TRACK_ISOLATE"
        ),
        AiToolSpec(
            id = "ai_object_eraser_inpaint",
            number = 11,
            name = "AI Object Eraser & Clean Plate",
            shortName = "Object Eraser",
            tagline = "Vanish Wires, Boom Mics & Unwanted Extras",
            description = "Draw a loose circle around any boom microphone, safety rig wire, or rogue background pedestrian. The neural inpainter fills the space with temporally stable background.",
            category = AiToolCategory.VFX_COMPOSITING,
            iconKey = "healing",
            accentColor = 0xFFEB3B5A, // Coral Red
            defaultParameters = listOf(
                AiToolParameter("mask_expansion", "Mask Boundary Expansion", 0.60f),
                AiToolParameter("temporal_lock", "Temporal Consistency Lock", 0.90f),
                AiToolParameter("grain_match", "Original Film Grain Match", 0.80f),
                AiToolParameter("flow_warp", "Optical Flow Perspective Warp", 0.85f)
            ),
            processingSteps = listOf(
                "Track masked object coordinates across all clip frames",
                "Synthesize occlusion optical flow vectors from neighbor frames",
                "Deep generative inpainting of background texture and perspective",
                "Blend local film grain to make the repair undetectable"
            ),
            outputEffectType = "VFX_CLIP_INPAINT"
        ),
        AiToolSpec(
            id = "ai_auto_framing",
            number = 12,
            name = "AI Smart Auto-Framing & Crop",
            shortName = "Smart Auto-Framing",
            tagline = "Intelligent Aspect Ratio Reframing & Horizon Level",
            description = "Dynamically tracks focal characters, action centers, and conversations to reframe widescreen footage into vertical 9:16, square 1:1, or 2.39:1 without losing context.",
            category = AiToolCategory.CAMERA_MOTION,
            iconKey = "crop",
            accentColor = 0xFF4B7BEC, // Royal Blue
            defaultParameters = listOf(
                AiToolParameter("smooth_panning", "Virtual Camera Smoothness", 0.85f),
                AiToolParameter("speaker_bias", "Active Speaker Weight", 0.90f),
                AiToolParameter("safe_zone", "Subject Headroom Margin", 0.75f),
                AiToolParameter("horizon_level", "Auto Horizon Leveling", 0.60f)
            ),
            processingSteps = listOf(
                "Detect multi-subject gaze direction, body centers, and voice activity",
                "Calculate optimal crop bounding box containing scene priority",
                "Apply virtual camera damping algorithm to simulate smooth pan/tilt",
                "Scale and output project timeline at chosen target aspect"
            ),
            outputEffectType = "TRANSFORM_AUTO_FRAME"
        ),
        AiToolSpec(
            id = "ai_vintage_colorizer",
            number = 13,
            name = "AI Vintage Technicolor Colorizer",
            shortName = "Vintage Colorizer",
            tagline = "Historical Film Stock Colorization & Emulation",
            description = "Breathes vibrant life into black-and-white archives or modern digital cuts, using historical color chemistry models (3-Strip Technicolor, 1960s Kodachrome, 1980s Fujifilm).",
            category = AiToolCategory.COLOR_LIGHTING,
            iconKey = "filter",
            accentColor = 0xFFFA8231, // Orange Gold
            defaultParameters = listOf(
                AiToolParameter("dye_saturation", "Technicolor Dye Saturation", 0.85f),
                AiToolParameter("historical_acc", "Historical Palette Accuracy", 0.90f),
                AiToolParameter("skin_lum", "Skin Luminance Warmth", 0.75f),
                AiToolParameter("gate_weave", "Film Gate Weave & Jitter", 0.40f)
            ),
            processingSteps = listOf(
                "Contextual semantic scene analysis (clothing, sky, foliage, skin)",
                "Apply historical dye-matrix color lookup tables",
                "Model organic dye density curves and highlight roll-off",
                "Simulate authentic 3-strip color bleed and halation glow"
            ),
            outputEffectType = "COLOR_GRADE_VINTAGE"
        ),
        AiToolSpec(
            id = "ai_8k_upscaler",
            number = 14,
            name = "AI Super-Resolution 8K Upscaler",
            shortName = "8K Super-Resolution",
            tagline = "Neural Texture Re-Synthesis For Theatrical Masters",
            description = "Upscales compressed 1080p and 4K captures to pristine 8K projection resolution. Re-synthesizes micro-skin textures, fabric weaves, and distant background details.",
            category = AiToolCategory.EDITING_FINISHING,
            iconKey = "high_quality",
            accentColor = 0xFF2ED573, // Lime Emerald
            defaultParameters = listOf(
                AiToolParameter("sharpness_boost", "Detail Re-Synthesis Strength", 0.85f),
                AiToolParameter("ringing_tame", "Edge Ringing Suppression", 0.90f),
                AiToolParameter("texture_gen", "Micro-Texture Naturalizer", 0.75f),
                AiToolParameter("denoise_prepass", "Pre-Upscale Denoise Pass", 0.70f)
            ),
            processingSteps = listOf(
                "Super-resolution generative neural network up-sampling",
                "Reconstruct high-frequency edge gradients without halo artifacts",
                "Synthesize realistic sub-pixel micro-textures",
                "Encode at cinema distribution bitrates (ProRes 4444 XQ / DCI 8K)"
            ),
            outputEffectType = "MASTER_UPSCALE_8K"
        ),
        AiToolSpec(
            id = "ai_kinetic_captions",
            number = 15,
            name = "AI Subtitle & Kinetic Typography",
            shortName = "Kinetic Captions",
            tagline = "Speech-To-Text With Frame-Accurate Dynamic Animation",
            description = "Transcribes multi-speaker audio with 99.8% precision, generating stylized kinetic subtitles with karaoke bounce animations, highlight colors, and auto-punctuation.",
            category = AiToolCategory.AUDIO_SPEECH,
            iconKey = "subtitles",
            accentColor = 0xFF70A1FF, // Sky Blue
            defaultParameters = listOf(
                AiToolParameter("word_bounce", "Kinetic Word Pop Scale", 0.80f),
                AiToolParameter("karaoke_glow", "Active Word Glow Color", 0.90f),
                AiToolParameter("line_limit", "Max Words Per Display Line", 0.50f),
                AiToolParameter("box_backdrop", "Dark Readability Pill Backdrop", 0.70f)
            ),
            processingSteps = listOf(
                "Multilingual speech recognition with deep transformer encoder",
                "Extract word-level millisecond start and end timestamps",
                "Generate vector text path overlays on upper graphic layer",
                "Apply spring physics animations for snappy word pop effects"
            ),
            outputEffectType = "GRAPHICS_KINETIC_SUBTITLES"
        ),
        AiToolSpec(
            id = "ai_slowmo_synthesizer",
            number = 16,
            name = "AI Slow-Mo Optical Flow Synthesizer",
            shortName = "Optical Slow-Mo",
            tagline = "Synthesizes 960 FPS Ultra-Slow Motion From 24 FPS",
            description = "Calculates bi-directional optical flow vectors between frames to generate hundreds of intermediate frames for silky-smooth, artifact-free high-speed camera action shots.",
            category = AiToolCategory.CAMERA_MOTION,
            iconKey = "slow_motion_video",
            accentColor = 0xFFFFA502, // Bright Amber
            defaultParameters = listOf(
                AiToolParameter("flow_precision", "Optical Flow Vector Precision", 0.95f),
                AiToolParameter("motion_blur", "Virtual Shutter Angle Motion Blur", 0.80f),
                AiToolParameter("occlusion_cure", "Occlusion Artifact Inpainting", 0.85f),
                AiToolParameter("time_multiplier", "Slowdown Factor (10x - 40x)", 0.75f)
            ),
            processingSteps = listOf(
                "Compute forward and backward optical flow vector fields",
                "Detect depth parallax occlusions to prevent object tearing",
                "Synthesize in-between image plates with cubic interpolation",
                "Calculate authentic 180-degree cinema motion blur"
            ),
            outputEffectType = "CLIP_OPTICAL_RETIME"
        ),
        AiToolSpec(
            id = "ai_camera_shake_inertia",
            number = 17,
            name = "AI Camera Shake & Whip Pan Synthesizer",
            shortName = "Camera Shake",
            tagline = "Organic Handheld Cinema Rig Inertia",
            description = "Adds realistic mechanical shoulder-mount wobble, explosion blast tremors, intense running camera sway, or lightning-fast whip pans with natural edge-fill padding.",
            category = AiToolCategory.CAMERA_MOTION,
            iconKey = "vibration",
            accentColor = 0xFFFF4757, // Crimson
            defaultParameters = listOf(
                AiToolParameter("shake_amplitude", "Shake Amplitude & Force", 0.75f),
                AiToolParameter("frequency_speed", "Vibration Frequency (Hz)", 0.65f),
                AiToolParameter("rotational_tilt", "Rotational Camera Roll", 0.55f),
                AiToolParameter("edge_inpaint", "Border Inpainting Edge Buffer", 0.80f)
            ),
            processingSteps = listOf(
                "Sample kinematic trajectory patterns from real IMAX handheld cameras",
                "Apply 6-degree-of-freedom camera translation & rotation matrix",
                "Mirror or neural inpaint outer boundary margins to hide cut borders",
                "Apply directional motion blur along instantaneous velocity vectors"
            ),
            outputEffectType = "TRANSFORM_CAMERA_SHAKE"
        ),
        AiToolSpec(
            id = "ai_anamorphic_flare_streak",
            number = 18,
            name = "AI Anamorphic Flare & Streak Raytracer",
            shortName = "Anamorphic Flares",
            tagline = "Physically Accurate Blue Streaks & Iris Ghosts",
            description = "Tracks brightest specular light sources (street lamps, vehicle headlights, flashlights) and raytraces classic Hollywood anamorphic blue horizontal streaks and hex ghosts.",
            category = AiToolCategory.VFX_COMPOSITING,
            iconKey = "flare",
            accentColor = 0xFF1E90FF, // Blue Streak
            defaultParameters = listOf(
                AiToolParameter("streak_length", "Horizontal Streak Length", 0.90f),
                AiToolParameter("specular_threshold", "Highlight Detection Threshold", 0.80f),
                AiToolParameter("iris_ghosts", "Multi-Element Iris Ghosts", 0.65f),
                AiToolParameter("streak_chroma", "Cyan/Gold Chromatic Dispersion", 0.85f)
            ),
            processingSteps = listOf(
                "Locate high-dynamic-range luminance peaks above threshold",
                "Simulate cylindrical anamorphic glass lens internal refraction",
                "Draw horizontal diffraction streaks with wavelength dispersion",
                "Composite multi-blade aperture ghosting elements onto image"
            ),
            outputEffectType = "VFX_CLIP_FLARES"
        ),
        AiToolSpec(
            id = "ai_deaging_beauty_retouch",
            number = 19,
            name = "AI De-Aging & Digital Makeup",
            shortName = "De-Aging Retouch",
            tagline = "Natural Skin Texture Preservation & Youth Restoration",
            description = "Reduces fine lines, relaxes eye bags, and evens out skin tones while strictly preserving authentic pores and facial expressions for natural, undetectable beauty polish.",
            category = AiToolCategory.COLOR_LIGHTING,
            iconKey = "face",
            accentColor = 0xFFFF6B81, // Rose Pink
            defaultParameters = listOf(
                AiToolParameter("pore_retention", "Micro-Pore Texture Retention", 0.90f),
                AiToolParameter("wrinkle_soften", "Wrinkle & Shadow Softening", 0.65f),
                AiToolParameter("eye_clarity", "Eye Sclera & Iris Clarity", 0.80f),
                AiToolParameter("teeth_whitening", "Natural Teeth Whitening", 0.70f)
            ),
            processingSteps = listOf(
                "Isolate skin tone frequency bands into low, medium, and high detail",
                "Smooth medium frequency shadows and color blotchiness",
                "Lock high frequency micro-pores and hair strands to prevent blurring",
                "Enhance light reflection catch-lights inside actor pupils"
            ),
            outputEffectType = "COLOR_GRADE_BEAUTY"
        ),
        AiToolSpec(
            id = "ai_script_breakdown_vision",
            number = 20,
            name = "AI Script Breakdown & Scene Vision",
            shortName = "Script Breakdown",
            tagline = "Automated Director Shot List & Pacing Analysis",
            description = "Analyzes film scene coverage, dialogue pacing, and shot diversity (Wide, Medium, Close-Up). Identifies pacing dead-zones and recommends optimal cut points.",
            category = AiToolCategory.EDITING_FINISHING,
            iconKey = "auto_awesome",
            accentColor = 0xFF7158E2, // Deep Violet
            defaultParameters = listOf(
                AiToolParameter("pacing_tightness", "Editing Pacing Aggressiveness", 0.80f),
                AiToolParameter("coverage_balance", "Close-up vs Wide Shot Balance", 0.75f),
                AiToolParameter("tension_meter", "Dramatic Tension Curve Tracking", 0.85f),
                AiToolParameter("continuity_check", "Eyeline & 180° Rule Verifier", 0.90f)
            ),
            processingSteps = listOf(
                "Categorize all clips into shot sizes (Establishing, Master, Close-up)",
                "Analyze speaker turn lengths and dramatic pause durations",
                "Flag 180-degree line-of-action crossing violations",
                "Generate interactive cinematic director notes on timeline"
            ),
            outputEffectType = "TIMELINE_DIRECTOR_NOTES"
        )
    )
}
