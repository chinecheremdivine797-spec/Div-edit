package com.example.engine

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

/** HTTPS-only client for the production FFmpeg renderer. */
class RemoteFfmpegClient(
    private val context: Context,
    private val baseUrl: String,
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.MINUTES)
        .build()
) {
    data class Result(val success: Boolean, val output: File?, val log: String, val error: String? = null)

    suspend fun process(operation: String, arguments: List<String>, input: File, extension: String = ".mp4"): Result = withContext(Dispatchers.IO) {
        if (!input.exists() || input.length() == 0L) {
            return@withContext Result(false, null, "", "Input media is missing or empty")
        }
        val normalizedBase = validateBaseUrl() ?: return@withContext Result(false, null, "", "FFmpeg backend must use HTTPS")
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("operation", operation)
            .addFormDataPart("arguments", arguments.joinToString("\u001f"))
            .addFormDataPart("media", input.name, input.asRequestBody("application/octet-stream".toMediaType()))
            .build()
        return@withContext execute("$normalizedBase/v1/process", body, extension)
    }

    suspend fun renderTimeline(
        clips: List<TimelineRenderRequest.Clip>,
        media: List<File>,
        width: Int,
        height: Int,
        fps: Int,
        quality: String
    ): Result = withContext(Dispatchers.IO) {
        if (clips.isEmpty() || media.isEmpty() || clips.size != media.size) {
            return@withContext Result(false, null, "", "Timeline render requires matching clip and media lists")
        }
        if (media.any { !it.exists() || it.length() == 0L }) {
            return@withContext Result(false, null, "", "One or more timeline media files are missing or empty")
        }
        val normalizedBase = validateBaseUrl() ?: return@withContext Result(false, null, "", "FFmpeg backend must use HTTPS")
        val manifest = buildString {
            append("{\"width\":").append(width)
            append(",\"height\":").append(height)
            append(",\"fps\":").append(fps)
            append(",\"quality\":\"").append(quality.replace("\"", "")).append("\"")
            append(",\"clips\":[")
            clips.forEachIndexed { index, clip ->
                if (index > 0) append(',')
                append("{\"index\":").append(clip.index)
                append(",\"startMs\":").append(clip.startMs)
                append(",\"endMs\":").append(clip.endMs)
                append(",\"trimInMs\":").append(clip.trimInMs)
                append(",\"trimOutMs\":").append(clip.trimOutMs)
                append(",\"speed\":").append(clip.speed)
                append(",\"volume\":").append(clip.volume).append('}')
            }
            append("]}")
        }
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("manifest", manifest)
        media.forEachIndexed { index, file ->
            builder.addFormDataPart("media", "clip_$index.${file.extension.ifBlank { "mp4" }}", file.asRequestBody("application/octet-stream".toMediaType()))
        }
        execute("$normalizedBase/v1/render-timeline", builder.build(), ".mp4")
    }

    private fun validateBaseUrl(): String? = baseUrl.trimEnd('/').takeIf { it.startsWith("https://") }

    private fun execute(url: String, body: okhttp3.RequestBody, extension: String): Result {
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Accept", "video/mp4,application/octet-stream")
            .build()
        return try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result(false, null, "HTTP ${response.code}", "FFmpeg backend returned HTTP ${response.code}")
                }
                val responseBody = response.body ?: return Result(false, null, "", "Backend returned an empty response")
                val output = File(context.cacheDir, "remote_render_${UUID.randomUUID()}$extension")
                responseBody.byteStream().use { inputStream -> output.outputStream().use { outputStream -> inputStream.copyTo(outputStream) } }
                if (output.length() == 0L) {
                    output.delete()
                    Result(false, null, "", "Backend returned an empty media file")
                } else {
                    Result(true, output, "Remote FFmpeg processing completed")
                }
            }
        } catch (e: IOException) {
            Result(false, null, "", "FFmpeg backend connection failed: ${e.message ?: "network error"}")
        }
    }
}
