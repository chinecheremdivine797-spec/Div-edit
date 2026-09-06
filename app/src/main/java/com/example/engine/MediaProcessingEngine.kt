package com.example.engine

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Processing abstraction used by DIV EDIT AI.
 * The editor never renames an input file to simulate an export: every operation
 * produces a distinct output artifact. FFmpeg execution can be supplied through
 * [CommandRunner] without coupling the UI to a particular FFmpeg distribution.
 */
class MediaProcessingEngine(
    private val context: Context,
    private val commandRunner: CommandRunner = UnavailableCommandRunner
) {
    data class ProcessResult(
        val success: Boolean,
        val output: File?,
        val log: String,
        val error: String? = null
    )

    suspend fun trim(input: File, startMs: Long, endMs: Long): ProcessResult =
        runFfmpeg(
            input = input,
            operation = "trim",
            args = listOf("-ss", seconds(startMs), "-i", input.absolutePath, "-t", seconds(endMs - startMs), "-c", "copy")
        )

    suspend fun extractFrame(input: File, timeMs: Long): ProcessResult =
        runFfmpeg(
            input = input,
            operation = "frame",
            extension = ".jpg",
            args = listOf("-ss", seconds(timeMs), "-i", input.absolutePath, "-frames:v", "1", "-q:v", "2")
        )

    suspend fun transcodeMp4(input: File, width: Int? = null, height: Int? = null, fps: Int? = null): ProcessResult {
        val args = mutableListOf("-i", input.absolutePath)
        if (width != null && height != null) args += listOf("-vf", "scale=$width:$height")
        if (fps != null) args += listOf("-r", fps.toString())
        args += listOf("-c:v", "libx264", "-preset", "medium", "-crf", "18", "-c:a", "aac", "-b:a", "192k", "-movflags", "+faststart")
        return runFfmpeg(input, "mp4", args)
    }

    suspend fun join(inputs: List<File>): ProcessResult {
        if (inputs.isEmpty()) return ProcessResult(false, null, "", "No input files supplied")
        val listFile = File(context.cacheDir, "concat_${UUID.randomUUID()}.txt")
        listFile.writeText(inputs.joinToString("\n") { "file '${it.absolutePath.replace("'", "'\\''")}'" })
        return try {
            runFfmpeg(
                input = inputs.first(),
                operation = "join",
                args = listOf("-f", "concat", "-safe", "0", "-i", listFile.absolutePath, "-c", "copy")
            )
        } finally {
            listFile.delete()
        }
    }

    private suspend fun runFfmpeg(input: File, operation: String, args: List<String>, extension: String = ".mp4"): ProcessResult = withContext(Dispatchers.IO) {
        if (!input.exists()) return@withContext ProcessResult(false, null, "", "Input media does not exist: ${input.name}")
        val output = File(context.cacheDir, "div_edit_${operation}_${UUID.randomUUID()}$extension")
        val result = commandRunner.run(args + output.absolutePath)
        if (result.exitCode == 0 && output.exists() && output.length() > 0L) {
            ProcessResult(true, output, result.log)
        } else {
            output.delete()
            ProcessResult(false, null, result.log, result.error ?: "FFmpeg failed with exit code ${result.exitCode}")
        }
    }

    private fun seconds(ms: Long): String = "%.3f".format(java.util.Locale.US, ms.coerceAtLeast(0L) / 1000.0)
}

data class CommandResult(val exitCode: Int, val log: String, val error: String? = null)

fun interface CommandRunner {
    fun run(arguments: List<String>): CommandResult
}

object UnavailableCommandRunner : CommandRunner {
    override fun run(arguments: List<String>): CommandResult = CommandResult(
        exitCode = -1,
        log = "FFmpeg runner not installed/configured",
        error = "Configure the platform FFmpeg runner before processing media."
    )
}
