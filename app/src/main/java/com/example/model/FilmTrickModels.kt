package com.example.model

enum class FilmTrickCategory(val title: String) {
    ALL("All Tricks"),
    MAGIC("Magic"),
    ACTION("Action"),
    HORROR("Horror"),
    FANTASY("Fantasy"),
    SCI_FI("Sci-Fi"),
    DRAMA("Drama"),
    THRILLER("Thriller"),
    SUPERNATURAL("Supernatural"),
    CINEMATIC("Cinematic"),
    ANIMATION("Animation"),
    TRANSITIONS("Transitions"),
    BRANDING("Branding")
}

data class FilmTrickPreset(
    val id: String,
    val name: String,
    val description: String,
    val category: FilmTrickCategory,
    val durationMs: Long = 1800L,
    val intensity: Float = 0.8f,
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false,
    val tagColor: Long = 0xFFFF7675
)

data class CustomFilmTrick(
    val id: String,
    val name: String,
    val baseCategory: FilmTrickCategory = FilmTrickCategory.CINEMATIC,
    val includesVideo: Boolean = true,
    val includesImageOverlay: Boolean = false,
    val maskType: String = "Bezier Feather",
    val particleType: String = "Anamorphic Dust",
    val audioCue: String = "Cinematic Boom & Sub Drop",
    val transitionCurve: String = "Cubic Bezier Fast Out",
    val keyframeCount: Int = 4,
    val blendMode: BlendModeType = BlendModeType.SCREEN
)
