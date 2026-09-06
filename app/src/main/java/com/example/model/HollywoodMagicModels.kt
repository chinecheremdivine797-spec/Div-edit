package com.example.model

enum class HollywoodMagicCategory(val title: String) {
    SIGNATURE("Signature Spatial"),
    ELEMENTAL("Elemental & Energy"),
    CINEMATIC_ACTION("Cinematic & Action"),
    SUPERNATURAL("Supernatural & Sci-Fi")
}

enum class HollywoodMagicType(
    val title: String,
    val description: String,
    val category: HollywoodMagicCategory,
    val iconName: String,
    val defaultIntensity: Float,
    val particleType: String
) {
    APPEAR(
        "Appear",
        "Make a person or object materialize seamlessly into the scene.",
        HollywoodMagicCategory.SIGNATURE,
        "auto_awesome",
        0.85f,
        "Gold Light Flurry"
    ),
    DISAPPEAR(
        "Disappear",
        "Detect, track, mask, reconstruct background, and vanish the subject completely.",
        HollywoodMagicCategory.SIGNATURE,
        "visibility_off",
        1.0f,
        "Temporal Distortion"
    ),
    TELEPORT(
        "Teleport",
        "Spatial displacement with refraction shockwave, quantum trail, and re-entry burst.",
        HollywoodMagicCategory.SIGNATURE,
        "bolt",
        0.9f,
        "Cyan Quantum Spark"
    ),
    CLONE(
        "Clone",
        "Create multiple realistic versions of a person moving independently in the same scene.",
        HollywoodMagicCategory.SIGNATURE,
        "content_copy",
        0.8f,
        "None (Clean Plate Alpha)"
    ),
    INVISIBILITY(
        "Invisibility",
        "Refractive optical camouflage revealing the background behind the moving subject.",
        HollywoodMagicCategory.SIGNATURE,
        "lens_blur",
        0.95f,
        "Heat Shimmer Wave"
    ),
    MORPH(
        "Morph",
        "Seamless fluid mesh transformation between two character silhouettes or objects.",
        HollywoodMagicCategory.SIGNATURE,
        "transform",
        0.9f,
        "Mesh Warp Distortion"
    ),
    MATERIALIZE(
        "Materialize",
        "Make a person or object coalesce from dense smoke, luminous particles, or cosmic light.",
        HollywoodMagicCategory.SIGNATURE,
        "flare",
        0.85f,
        "Luminescent Embers"
    ),
    VANISH(
        "Vanish",
        "Dissolve subject into ascending particle turbulence, volumetric fog, and light burst.",
        HollywoodMagicCategory.SIGNATURE,
        "grain",
        0.9f,
        "Dark Dust Particles"
    ),
    GHOST(
        "Ghost",
        "Ethereal transparent presence with spectral glow, lagging echo trails, and float physics.",
        HollywoodMagicCategory.SUPERNATURAL,
        "blur_on",
        0.65f,
        "Spectral Aura"
    ),
    TIME_FREEZE(
        "Time Freeze",
        "Freeze selected subject or background debris in mid-air while camera/other actors continue.",
        HollywoodMagicCategory.CINEMATIC_ACTION,
        "pause_circle",
        1.0f,
        "Frozen Glass Shards"
    ),
    SUPER_SPEED(
        "Super Speed",
        "Anamorphic directional motion blur streaks, sonic snap rings, and hyper-velocity trails.",
        HollywoodMagicCategory.CINEMATIC_ACTION,
        "speed",
        0.9f,
        "Electric Streak Ribbons"
    ),
    SLOW_MOTION(
        "Slow Motion",
        "Optical flow frame interpolation for hyper-crisp 1000 FPS Hollywood action ramp.",
        HollywoodMagicCategory.CINEMATIC_ACTION,
        "slow_motion_video",
        0.75f,
        "Optical Flow Vector"
    ),
    ENERGY(
        "Energy",
        "Luminous plasma field enveloping the actor with interactive body contour lighting.",
        HollywoodMagicCategory.ELEMENTAL,
        "light_mode",
        0.8f,
        "Plasma Ion Tendrils"
    ),
    LIGHTNING(
        "Lightning",
        "Fractal high-voltage electrical arcs arcing off the subject's hands and eyes.",
        HollywoodMagicCategory.ELEMENTAL,
        "flash_on",
        0.95f,
        "High-Voltage Arcs"
    ),
    FIRE(
        "Fire",
        "Volumetric pyro simulation with heat distortion, ember dissipation, and cast illumination.",
        HollywoodMagicCategory.ELEMENTAL,
        "local_fire_department",
        0.85f,
        "Pyro Flame Mesh"
    ),
    SMOKE(
        "Smoke",
        "Dense photorealistic fluid smoke billowing around character silhouettes.",
        HollywoodMagicCategory.ELEMENTAL,
        "cloud",
        0.8f,
        "Volumetric Plumes"
    ),
    SPARKS(
        "Sparks",
        "Heavy metal grinder / magical spark shower bouncing off environment surfaces.",
        HollywoodMagicCategory.ELEMENTAL,
        "scatter_plot",
        0.85f,
        "Ricochet Spark Emitter"
    ),
    EXPLOSION(
        "Explosion",
        "Hollywood pyrotechnic blast with fireball, secondary debris shrapnel, and camera shockwave.",
        HollywoodMagicCategory.CINEMATIC_ACTION,
        "whatshot",
        1.0f,
        "Fireball & Blast Shrapnel"
    ),
    PORTAL(
        "Portal",
        "Doctor Strange / Stargate style spinning vortex of molten sparks revealing another reality.",
        HollywoodMagicCategory.SUPERNATURAL,
        "motion_photos_on",
        0.95f,
        "Molten Gold Ring"
    ),
    SHOCKWAVE(
        "Shockwave",
        "High-energy spherical air refraction distorting the background in a concentric wave.",
        HollywoodMagicCategory.CINEMATIC_ACTION,
        "radio_button_checked",
        0.85f,
        "Normal Map Refraction"
    ),
    MAGIC_TRANSFORMATION(
        "Magic Transformation",
        "Dramatic costume/entity transfiguration with crystalline cocoon and radiance flare.",
        HollywoodMagicCategory.SUPERNATURAL,
        "auto_fix_high",
        0.9f,
        "Prismatic Crystal Shell"
    ),
    SUPERHERO_LANDING(
        "Superhero Landing",
        "Epic three-point ground punch with crater shock, dust ring blast, and camera shake slam.",
        HollywoodMagicCategory.CINEMATIC_ACTION,
        "pan_tool_alt",
        1.0f,
        "Radial Crater Dust"
    ),
    FLYING_EFFECT(
        "Flying Effect",
        "Anti-gravity wire removal, air turbulence wind on clothes, and sonic contrails.",
        HollywoodMagicCategory.CINEMATIC_ACTION,
        "flight_takeoff",
        0.8f,
        "Vapor Jet Stream"
    ),
    SHADOW_MAGIC(
        "Shadow Magic",
        "Sentient umbral tendrils creeping outward from actor shadow with light absorption.",
        HollywoodMagicCategory.SUPERNATURAL,
        "nights_stay",
        0.85f,
        "Black Smoke Tendrils"
    ),
    HORROR(
        "Horror",
        "Supernatural entity flicker, chromatic eye glow, creeping dark vignette, and film jitter.",
        HollywoodMagicCategory.SUPERNATURAL,
        "sentiment_very_dissatisfied",
        0.9f,
        "Glitch Subliminal Echoes"
    ),
    DREAM(
        "Dream",
        "Ethereal anamorphic bloom, gentle chromatic dispersion, halation, and floating dust motes.",
        HollywoodMagicCategory.CINEMATIC_ACTION,
        "bubble_chart",
        0.7f,
        "Atmospheric Motes"
    ),
    SCI_FI(
        "Sci-Fi",
        "Cybernetic holographic scanlines, target telemetry HUD, and hard-light energy shield.",
        HollywoodMagicCategory.SUPERNATURAL,
        "memory",
        0.85f,
        "Holo Raster Scan"
    )
}

data class MagicCompositingStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val status: String = "Ready" // "Ready", "Processing", "Complete"
)

data class HollywoodMagicProcessState(
    val magicType: HollywoodMagicType,
    val selectedClipTitle: String,
    val intensity: Float = 0.85f,
    val blendMode: BlendModeType = BlendModeType.SCREEN,
    val particleColorHex: Long = 0xFFFFB834,
    val steps: List<MagicCompositingStep> = listOf(
        MagicCompositingStep(1, "Detect Subject", "Neural segmentation bounding box and semantic subject boundary"),
        MagicCompositingStep(2, "Track Subject", "Sub-pixel motion vector extraction and optical point tracking"),
        MagicCompositingStep(3, "Generate Mask", "Sub-pixel alpha matte edge feathering and hair rotoscopy"),
        MagicCompositingStep(4, "Understand Background", "Monocular depth estimation and scene lighting geometry"),
        MagicCompositingStep(5, "Reconstruct Background", "Temporal neural inpainting clean plate reconstruction"),
        MagicCompositingStep(6, "Remove Subject", "Seamless alpha separation with environmental edge fill"),
        MagicCompositingStep(7, "Add Requested Effect", "GPU particle simulation, light emission, and volumetric pass"),
        MagicCompositingStep(8, "Composite Result", "Environmental cast lighting, reflection match, and color halation"),
        MagicCompositingStep(9, "Return Editable Result", "Multi-track VFX clip stack with editable keyframe handles")
    ),
    val currentStepIndex: Int = 0,
    val isProcessing: Boolean = false,
    val isComplete: Boolean = false
)
