package com.example.model

enum class WatermarkType(val label: String) {
    BRAND_TEXT("Studio Brand Text"),
    PNG_LOGO("Transparent PNG Logo"),
    CUSTOM_GRAPHIC("Filmmaker Graphic / Monogram"),
    TIME_CODE_BURN("SMPTE Production Burn-In")
}

enum class AnchorPosition(val label: String, val normX: Float, val normY: Float) {
    TOP_LEFT("Top Left", 0.08f, 0.08f),
    TOP_CENTER("Top Center", 0.5f, 0.08f),
    TOP_RIGHT("Top Right", 0.92f, 0.08f),
    CENTER("Center", 0.5f, 0.5f),
    BOTTOM_LEFT("Bottom Left", 0.08f, 0.90f),
    BOTTOM_CENTER("Bottom Center", 0.5f, 0.90f),
    BOTTOM_RIGHT("Bottom Right", 0.92f, 0.90f),
    CUSTOM("Custom Freeform X/Y", 0.5f, 0.5f)
}

enum class WatermarkScope(val label: String) {
    ENTIRE_FILM("Entire Film (Head to Tail)"),
    SELECTED_SCENES("Selected Key Scenes Only"),
    SELECTED_CLIP("Currently Selected Clip"),
    SPECIFIC_TIME_RANGE("Specific Timecode Window")
}

data class WatermarkConfig(
    val isEnabled: Boolean = true,
    val type: WatermarkType = WatermarkType.BRAND_TEXT,
    val textContent: String = "DIV EDIT AI // CINEMA MASTER",
    val logoAssetId: String? = null,
    val position: AnchorPosition = AnchorPosition.BOTTOM_RIGHT,
    val customNormX: Float = 0.90f,
    val customNormY: Float = 0.90f,
    val sizePercent: Float = 14f,       // 5% to 50%
    val opacity: Float = 0.75f,         // 0.0 to 1.0
    val rotationDeg: Float = 0f,        // -180 to 180
    val scope: WatermarkScope = WatermarkScope.ENTIRE_FILM,
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 120_000L,
    val fadeInMs: Long = 1000L,
    val fadeOutMs: Long = 1000L,
    val isBurnIn: Boolean = false       // false = Non-destructive overlay; true = Burn-in to video pixels
)

enum class LogoRevealStyle(val label: String, val description: String) {
    FADE_REVEAL("Fade Reveal", "Gentle cinematic luminance bloom and dissolve"),
    LIGHT_REVEAL("Light Reveal", "Anamorphic horizontal flare sweeping across letterforms"),
    SMOKE_REVEAL("Smoke Reveal", "Heavy theatrical smoke parting to reveal logo emblem"),
    PARTICLE_REVEAL("Particle Reveal", "Billions of radiant embers coalescing into shape"),
    ENERGY_REVEAL("Energy Reveal", "Electric arc lightning discharging and forming geometry"),
    GLITCH_REVEAL("Glitch Reveal", "Cybernetic RGB channel split and chromatic raster scan"),
    CINEMATIC_ZOOM("Cinematic Zoom", "Slow dramatic optical push-in with lens distortion"),
    FILM_STYLE_REVEAL("Film-Style Reveal", "Vintage 35mm film burn, sprocket roll, and shutter clap")
}

data class LogoSequenceConfig(
    val isIntroEnabled: Boolean = true,
    val introStyle: LogoRevealStyle = LogoRevealStyle.LIGHT_REVEAL,
    val introTitle: String = "DIV STUDIOS",
    val introSubtitle: String = "A CINEMATIC PICTURES RELEASE",
    val introDurationMs: Long = 3500L,
    val introSoundCue: String = "Deep Cinema Brass Hit & Anamorphic Whoosh",
    
    val isOutroEnabled: Boolean = true,
    val outroStyle: LogoRevealStyle = LogoRevealStyle.PARTICLE_REVEAL,
    val outroTitle: String = "PRODUCED WITH DIV EDIT AI",
    val outroSubtitle: String = "TURN FOOTAGE INTO CINEMA",
    val outroDurationMs: Long = 3000L
)
