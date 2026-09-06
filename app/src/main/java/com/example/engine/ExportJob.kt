package com.example.engine

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ExportJob(private val pipeline: ExportPipeline) {
    enum class State { IDLE, VALIDATING, RENDERING, COMPLETE, FAILED, CANCELLED }

    data class Status(val state: State, val progress: Int, val message: String, val output: File? = null)

    suspend fun run(request: ExportPipeline.ExportRequest): Status = withContext(Dispatchers.IO) {
        try {
            val source = request.source
            if (!source.exists() || source.length() == 0L) {
                return@withContext Status(State.FAILED, 0, "Source media is missing or empty")
            }
            val result = pipeline.exportMp4(request)
            if (result.success) {
                Status(State.COMPLETE, 100, "MP4 export complete", result.output)
            } else {
                Status(State.FAILED, 0, result.error ?: "Export failed")
            }
        } catch (_: CancellationException) {
            Status(State.CANCELLED, 0, "Export cancelled")
        } catch (t: Throwable) {
            Status(State.FAILED, 0, t.message ?: "Unexpected export error")
        }
    }
}
