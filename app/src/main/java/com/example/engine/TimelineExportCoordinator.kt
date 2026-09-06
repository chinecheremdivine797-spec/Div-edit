package com.example.engine

import android.content.Context
import android.net.Uri
import com.example.model.FilmProject
import com.example.model.MediaAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Builds an export from the actual FilmProject timeline.
 * No simulated progress and no source-file renaming are used.
 *
 * The current production path is a remote FFmpeg renderer. Until an HTTPS
 * renderer URL is configured, export fails explicitly instead of pretending
 * that an MP4 was produced.
 */
class TimelineExportCoordinator(
    private val context: Context,
    private val backendUrl: String
) {
    data class Request(
        val project: FilmProject,
        val assets: List<MediaAsset>,
        val resolution: String = "1080p",
        val fps: Int = 30,
        val quality: String = "High"
    )

    data class Result(
        val success: Boolean,
        val output: File? = null,
        val progress: Int = 0,
        val message: String
    )

    suspend fun export(request: Request): Result = withContext(Dispatchers.IO) {
        val videoClips = request.project.tracks
            .flatMap { it.clips }
            .filter { clip ->
                request.assets.firstOrNull { it.id == clip.assetId }?.let { asset ->
                    asset.type.name.contains("VIDEO") ||
                        asset.type.name == "LIVE_ACTION" ||
                        asset.type.name == "ANIMATION" ||
                        asset.type.name == "RENDERED_SCENE"
                } ?: false
            }
            .sortedBy { it.startMs }

        if (videoClips.isEmpty()) {
            return@withContext Result(false, message = "No video clips with source media are available on the timeline")
        }

        if (!backendUrl.trim().startsWith("https://")) {
            return@withContext Result(false, message = "Real MP4 export is not configured: add an HTTPS FFmpeg backend URL")
        }

        val files = mutableListOf<File>()
        try {
            for (clip in videoClips) {
                val asset = request.assets.firstOrNull { it.id == clip.assetId }
                    ?: return@withContext Result(false, message = "Missing media asset for clip '${clip.title}'")
                val source = materialize(asset.uriString, asset.name)
                    ?: return@withContext Result(false, message = "Source media is unavailable for '${asset.name}'")
                files += source
            }

            val renderClips = videoClips.mapIndexed { index, clip ->
                TimelineRenderRequest.Clip(
                    index = index,
                    startMs = clip.startMs,
                    endMs = clip.endMs,
                    trimInMs = clip.trimInMs,
                    trimOutMs = clip.trimOutMs,
                    speed = clip.speed.coerceAtLeast(0.01f),
                    volume = clip.volume.coerceAtLeast(0f)
                )
            }

            val size = resolution(request.resolution)
            val client = RemoteFfmpegClient(context, backendUrl)
            val remote = client.renderTimeline(
                clips = renderClips,
                media = files,
                width = size.first,
                height = size.second,
                fps = request.fps,
                quality = request.quality
            )

            if (!remote.success || remote.output == null) {
                return@withContext Result(false, message = remote.error ?: remote.log.ifBlank { "FFmpeg backend export failed" })
            }

            Result(true, remote.output, 100, "MP4 export complete: ${remote.output.name}")
        } finally {
            files.filter { it.parentFile == context.cacheDir }.forEach { it.delete() }
        }
    }

    private fun materialize(uriString: String?, displayName: String): File? {
        if (uriString.isNullOrBlank()) return null
        val uri = Uri.parse(uriString)
        if (uri.scheme == "file") return File(uri.path ?: return null).takeIf { it.exists() && it.length() > 0L }

        return runCatching {
            val extension = displayName.substringAfterLast('.', "mp4").takeIf { it.isNotBlank() } ?: "mp4"
            val output = File(context.cacheDir, "export_source_${UUID.randomUUID()}.$extension")
            context.contentResolver.openInputStream(uri)?.use { input ->
                output.outputStream().use { out -> input.copyTo(out) }
            } ?: return null
            output.takeIf { it.exists() && it.length() > 0L }
        }.getOrNull()
    }

    private fun resolution(value: String): Pair<Int, Int> = when (value) {
        "720p" -> 1280 to 720
        "1440p" -> 2560 to 1440
        "4K" -> 3840 to 2160
        else -> 1920 to 1080
    }
}

data class TimelineRenderRequest(
    val clips: List<Clip> = emptyList()
) {
    data class Clip(
        val index: Int,
        val startMs: Long,
        val endMs: Long,
        val trimInMs: Long,
        val trimOutMs: Long,
        val speed: Float,
        val volume: Float
    )
}
