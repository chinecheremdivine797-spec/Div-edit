package com.example.engine

data class ExportSettings(
    val width: Int = 1920,
    val height: Int = 1080,
    val fps: Int = 30,
    val quality: Quality = Quality.HIGH,
    val format: Format = Format.MP4
) {
    enum class Quality(val crf: Int) { STANDARD(23), HIGH(18), MAXIMUM(14) }
    enum class Format { MP4 }
}

fun ExportSettings.toFfmpegArguments(): List<String> = buildList {
    addAll(listOf("-c:v", "libx264", "-preset", "medium", "-crf", quality.crf.toString()))
    addAll(listOf("-r", fps.toString()))
    addAll(listOf("-vf", "scale=$width:$height"))
    addAll(listOf("-c:a", "aac", "-b:a", "192k", "-movflags", "+faststart"))
}
