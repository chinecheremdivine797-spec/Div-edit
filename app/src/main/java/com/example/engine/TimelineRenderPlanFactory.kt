package com.example.engine

import com.example.model.FilmProject
import com.example.model.MediaAsset
import com.example.model.TimelineTrack
import com.example.model.TrackType

/**
 * Converts the editor's non-destructive FilmProject model into the render plan
 * consumed by the export/processing layer. Missing media paths are preserved so
 * validation can report them instead of silently skipping clips.
 */
object TimelineRenderPlanFactory {
    fun fromProject(
        project: FilmProject,
        mediaAssets: List<MediaAsset>
    ): TimelineRenderPlan {
        val assetsById = mediaAssets.associateBy { it.id }

        val renderTracks = project.tracks.map { track ->
            RenderTrack(
                id = track.id,
                kind = trackKind(track),
                clips = track.clips.map { clip ->
                    val asset = assetsById[clip.assetId]
                    RenderClip(
                        id = clip.id,
                        sourcePath = asset?.uriString.orEmpty(),
                        startMs = clip.startMs,
                        endMs = clip.endMs,
                        speed = clip.speed,
                        volume = clip.volume
                    )
                }
            )
        }

        return TimelineRenderPlan(
            videoTracks = renderTracks.filter { it.kind == TrackKind.VIDEO },
            audioTracks = renderTracks.filter { it.kind == TrackKind.AUDIO },
            overlays = renderTracks.filter { it.kind != TrackKind.VIDEO && it.kind != TrackKind.AUDIO }
        )
    }

    private fun trackKind(track: TimelineTrack): TrackKind = when (track.type) {
        TrackType.VIDEO_V1, TrackType.VIDEO_V2 -> TrackKind.VIDEO
        TrackType.AUDIO_A1, TrackType.AUDIO_A2, TrackType.SFX_A3 -> TrackKind.AUDIO
        TrackType.VFX -> TrackKind.OVERLAY
        TrackType.TITLES -> TrackKind.TEXT
        TrackType.OVERLAY -> TrackKind.OVERLAY
    }
}
