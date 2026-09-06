package com.example.engine

/** Immutable render plan generated from the editor timeline. */
data class TimelineRenderPlan(
    val videoTracks: List<RenderTrack> = emptyList(),
    val audioTracks: List<RenderTrack> = emptyList(),
    val overlays: List<RenderTrack> = emptyList()
) {
    fun orderedClips(): List<RenderClip> =
        (videoTracks + audioTracks + overlays)
            .flatMap { it.clips }
            .sortedBy { it.startMs }
}

data class RenderTrack(
    val id: String,
    val kind: TrackKind,
    val clips: List<RenderClip> = emptyList()
)

enum class TrackKind { VIDEO, AUDIO, OVERLAY, VFX, TEXT, ADJUSTMENT }

data class RenderClip(
    val id: String,
    val sourcePath: String,
    val startMs: Long,
    val endMs: Long,
    val speed: Float = 1f,
    val volume: Float = 1f
)
