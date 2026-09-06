package com.example.engine

import com.example.model.AssetType
import com.example.model.FilmProject
import com.example.model.TimelineTrack
import com.example.model.TrackType

/**
 * Converts the editor's non-destructive FilmProject model into the render plan
 * consumed by the export/processing layer. Missing media paths are preserved so
 * validation can report them instead of silently skipping clips.
 */
object TimelineRenderPlanFactory {
    fun fromProject(project: FilmProject): TimelineRenderPlan {
        val assetsById = project.tracks
            .flatMap { it.clips }
            .associateBy { it.assetId }

        val tracks = project.tracks.map { track ->
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
            projectId = project.id,
            durationMs = project.durationMs,
            fps = project.fps.fps,
            tracks = tracks
        )
    }

    private fun trackKind(track: TimelineTrack): TrackKind = when (track.type) {
        TrackType.VIDEO_V1, TrackType.VIDEO_V2 -> TrackKind.VIDEO
        TrackType.AUDIO_A1, TrackType.AUDIO_A2, TrackType.SFX_A3 -> TrackKind.AUDIO
        TrackType.VFX -> TrackKind.VFX
        TrackType.TITLES -> TrackKind.TEXT
        TrackType.OVERLAY -> TrackKind.OVERLAY
    }

    fun supportedSource(assetType: AssetType): Boolean = when (assetType) {
        AssetType.LIVE_ACTION,
        AssetType.ANIMATION,
        AssetType.RENDERED_SCENE,
        AssetType.IMAGE_SEQUENCE,
        AssetType.STILL_IMAGE,
        AssetType.AUDIO_SCORE,
        AssetType.VOICE_OVER,
        AssetType.FOLEY_SFX,
        AssetType.VFX_ASSET -> true
        AssetType.LOGO -> true
    }
}
