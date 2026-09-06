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

/**
 * Optional production FFmpeg backend client.
 * The backend URL is injected at runtime; no secret is stored in the APK.
 * Expected API contract: POST /v1/process with multipart fields `operation`,
 * `arguments`, and `media`; response body is the rendered media bytes.
 */
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
        val normalizedBase = baseUrl.trimEnd('/')
        if (!normalizedBase.startsWith("https://")) {
            return@withContext Result(false, null, "", "FFmpeg backend must use HTTPS")
        }

        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("operation", operation)
            .addFormDataPart("arguments", arguments.joinToString("\u001f"))
            .addFormDataPart("media", input.name, input.asRequestBody("application/octet-stream".toMediaType()))
            .build()

        val request = Request.Builder()
            .url("$normalizedBase/v1/process")
            .post(body)
            .header("Accept", "video/mp4,application/octet-stream")
            .build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result(false, null, "HTTP ${response.code}", "FFmpeg backend returned HTTP ${response.code}")
                }
                val responseBody = response.body ?: return@withContext Result(false, null, "", "Backend returned an empty response")
                val output = File(context.cacheDir, "remote_${operation}_${UUID.randomUUID()}$extension")
                responseBody.byteStream().use { inputStream -> output.outputStream().use { outputStream -> inputStream.copyTo(outputStream) } }
                if (output.length() == 0L) {
                    output.delete()
                    return@withContext Result(false, null, "", "Backend returned an empty media file")
                }
                Result(true, output, "Remote FFmpeg processing completed")
            }
        } catch (e: IOException) {
            Result(false, null, "", "FFmpeg backend connection failed: ${e.message ?: "network error"}")
        }
    }
}
