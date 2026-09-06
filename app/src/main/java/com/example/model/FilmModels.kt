package com.example.model

enum class AspectRatioPreset(val label: String, val ratio: Float, val dimensionLabel: String) {
    CINEMA_16_9("16:9 Cinema", 16f / 9f, "3840 x 2160"),
    VERTICAL_9_16("9:16 Vertical", 9f / 16f, "2160 x 3840"),
    SQUARE_1_1("1:1 Square", 1f, "2160 x 2160"),
    PORTRAIT_4_5("4:5 Portrait", 4f / 5f, "1728 x 2160"),
    ANAMORPHIC_2_39("2.39:1 Scope", 2.39f, "4096 x 1714"),
    CUSTOM("Custom Res", 16f / 9f, "Custom DCI")
}

enum class FpsOption(val fps: Int, val label: String) {
    FPS_24(24, "24 FPS (Cinematic standard)"),
    FPS_25(25, "25 FPS (PAL Broadcast)"),
    FPS_30(30, "30 FPS (NTSC/Digital)"),
    FPS_50(50, "50 FPS (High Frame Rate)"),
    FPS_60(60, "60 FPS (Ultra Smooth)")
}

enum class ColorSpaceOption(val label: String) {
    REC_709("Rec.709 (SDR Broadcast)"),
    DCI_P3("DCI-P3 (Theatrical Cinema)"),
    ACES_CG("ACEScg (VFX Academy Standard)"),
    LOG_C("ARRI Log-C Film Emulation")
}

enum class AssetType(val label: String) {
    LIVE_ACTION("Live-Action Video"),
    ANIMATION("Animation Video"),
    RENDERED_SCENE("Rendered Scene"),
    IMAGE_SEQUENCE("Image Sequence"),
    STILL_IMAGE("Still Image"),
    AUDIO_SCORE("Music Score"),
    VOICE_OVER("Voice Recording"),
    FOLEY_SFX("Sound Effect"),
    LOGO("Brand / Logo"),
    VFX_ASSET("VFX Element")
}

data class MediaAsset(
    val id: String,
    val name: String,
    val type: AssetType,
    val durationMs: Long,
    val resolution: String = "4K UHD",
    val fps: Int = 24,
    val colorTag: Long = 0xFF2E86DE,
    val description: String = "",
    val uriString: String? = null,
    val fileExtension: String = "mov",
    val fileSizeBytes: Long = 1024L * 1024L * 128L, // 128MB default
    val folderCategory: String = "Footage",
    val dateAddedMs: Long = System.currentTimeMillis()
)

data class UserAccount(
    val userId: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val idToken: String? = null,
    val tier: String = "CINEMA PRO STUDIO",
    val isSignedIn: Boolean = true,
    val signedInSinceMs: Long = System.currentTimeMillis()
)

data class UserProjectStorage(
    val userId: String,
    val myFilms: List<FilmProject> = emptyList(),
    val drafts: List<FilmProject> = emptyList(),
    val recentProjects: List<FilmProject> = emptyList(),
    val exportedProjects: List<FilmProject> = emptyList(),
    val savedFilmTricks: List<FilmTrickPreset> = emptyList(),
    val savedWatermarkConfig: WatermarkConfig = WatermarkConfig(),
    val savedLogoSequenceConfig: LogoSequenceConfig = LogoSequenceConfig()
)

enum class TrackType(val label: String, val shortTag: String) {
    VIDEO_V1("Video Track 1", "V1"),
    VIDEO_V2("Video Track 2", "V2"),
    VFX("VFX & Magic Track", "FX"),
    OVERLAY("Overlay & Adjustments", "ADJ"),
    TITLES("Text & Titles", "TXT"),
    AUDIO_A1("Master Dialog", "A1"),
    AUDIO_A2("Cinematic Score", "A2"),
    SFX_A3("Foley & Impact SFX", "A3")
}

enum class BlendModeType(val label: String) {
    NORMAL("Normal"),
    SCREEN("Screen (Light/Fire)"),
    MULTIPLY("Multiply (Shadows)"),
    OVERLAY("Overlay (High Contrast)"),
    ADD("Add / Linear Dodge"),
    COLOR_DODGE("Color Dodge (Glow)"),
    SOFT_LIGHT("Soft Light")
}

enum class FilmGrainType(val label: String) {
    NONE("Clean Digital"),
    GRAIN_35MM("35mm Kodak 5219"),
    GRAIN_16MM("16mm Vintage Rough"),
    GRAIN_8MM("8mm Super 8 Grit"),
    GRAIN_IMAX("IMAX 70mm Micro")
}

data class ColorGrade(
    val brightness: Float = 0f,      // -100 to 100
    val contrast: Float = 0f,        // -100 to 100
    val saturation: Float = 0f,      // -100 to 100
    val exposure: Float = 0f,        // -3.0 to +3.0 EV
    val highlights: Float = 0f,      // -100 to 100
    val shadows: Float = 0f,         // -100 to 100
    val temperature: Float = 0f,     // -100 (cool) to 100 (warm)
    val tint: Float = 0f,            // -100 (green) to 100 (magenta)
    val vibrance: Float = 0f,        // -100 to 100
    val sharpen: Float = 15f,        // 0 to 100
    val blur: Float = 0f,            // 0 to 100
    val vignette: Float = 20f,       // 0 to 100
    val filmGrain: FilmGrainType = FilmGrainType.GRAIN_35MM,
    val lutPreset: String = "Teal & Orange Hollywood"
)

data class TransformState(
    val scale: Float = 1.0f,
    val rotationDeg: Float = 0f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val flipH: Boolean = false,
    val flipV: Boolean = false,
    val cropTop: Float = 0f,
    val cropBottom: Float = 0f,
    val cropLeft: Float = 0f,
    val cropRight: Float = 0f
)

data class VfxKeyframe(
    val timeOffsetMs: Long,
    val property: String,
    val value: Float,
    val easing: String = "EaseInOutCubic"
)

data class VfxMask(
    val type: String = "Ellipse", // Ellipse, Rectangle, Freehand Bezier, Chroma Green
    val featherPx: Float = 24f,
    val isInverted: Boolean = false,
    val tolerance: Float = 0.45f // For green screen chroma
)

data class TimelineClip(
    val id: String,
    val trackId: String,
    val assetId: String,
    val title: String,
    val startMs: Long,
    val endMs: Long,
    val trimInMs: Long = 0,
    val trimOutMs: Long = 0,
    val speed: Float = 1.0f,
    val isReversed: Boolean = false,
    val isFreezeFrame: Boolean = false,
    val opacity: Float = 1.0f,
    val blendMode: BlendModeType = BlendModeType.NORMAL,
    val volume: Float = 1.0f,
    val fadeInMs: Long = 0,
    val fadeOutMs: Long = 0,
    val transform: TransformState = TransformState(),
    val colorGrade: ColorGrade = ColorGrade(),
    val appliedMagicId: String? = null,
    val appliedMagicName: String? = null,
    val appliedFilmTrickId: String? = null,
    val appliedFilmTrickName: String? = null,
    val keyframes: List<VfxKeyframe> = emptyList(),
    val mask: VfxMask? = null,
    val clipColor: Long = 0xFF2E86DE
) {
    val durationMs: Long get() = (endMs - startMs).coerceAtLeast(100L)
}

data class TimelineTrack(
    val id: String,
    val type: TrackType,
    val name: String,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val isSolo: Boolean = false,
    val isVisible: Boolean = true,
    val volume: Float = 1.0f,
    val clips: List<TimelineClip> = emptyList()
)

data class FilmMarker(
    val id: String,
    val timeMs: Long,
    val label: String,
    val color: Long = 0xFFFFB834
)

data class FilmProject(
    val id: String,
    val title: String,
    val director: String = "Director",
    val aspectRatio: AspectRatioPreset = AspectRatioPreset.CINEMA_16_9,
    val fps: FpsOption = FpsOption.FPS_24,
    val colorSpace: ColorSpaceOption = ColorSpaceOption.DCI_P3,
    val durationMs: Long = 120_000L, // 2 minutes default
    val tracks: List<TimelineTrack> = emptyList(),
    val markers: List<FilmMarker> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val thumbnailTag: String = "project_thumb_1"
)
