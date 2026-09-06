package com.example.engine

import java.io.File

/** Coordinates editor export without mutating source media. */
class ExportPipeline(private val processor: MediaProcessingEngine) {
    data class ExportRequest(
        val source: File,
        val width: Int = 1920,
        val height: Int = 1080,
        val fps: Int = 24
    )

    suspend fun exportMp4(request: ExportRequest): MediaProcessingEngine.ProcessResult =
        processor.transcodeMp4(
            input = request.source,
            width = request.width,
            height = request.height,
            fps = request.fps
        )
}
