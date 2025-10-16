package com.example.cheating_glasses.utils

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class OpenAIClient(private val apiKey: String) {

    private val client = OkHttpClient.Builder()
        .callTimeout(java.time.Duration.ofSeconds(60))
        .connectTimeout(java.time.Duration.ofSeconds(20))
        .readTimeout(java.time.Duration.ofSeconds(60))
        .writeTimeout(java.time.Duration.ofSeconds(60))
        .build()
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    fun buildVisionPayload(systemPrompt: String, transcriptBeforeSolve: String, imageDataUrl: String): String {
        val sys = systemPrompt.ifEmpty { "" }
        return """
            {
              "model": "gpt-4.1",
              "messages": [
                {"role": "system", "content": ${jsonEscape(sys)}},
                {"role": "user", "content": [
                  {"type": "text", "text": ${jsonEscape(transcriptBeforeSolve)}},
                  {"type": "image_url", "image_url": {"url": ${jsonEscape(imageDataUrl)}}}
                ]}
              ]
            }
        """.trimIndent()
    }

    fun callChatCompletions(payload: String): String {
        val requestBody: RequestBody = payload.toRequestBody(jsonMedia)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()
        return executeWithRetry(request, maxAttempts = 3)
    }

    private fun executeWithRetry(request: Request, maxAttempts: Int): String {
        var attempt = 0
        var delayMs = 500L
        var lastErr: Exception? = null
        while (attempt < maxAttempts) {
            try {
                client.newCall(request).execute().use { resp ->
                    val body = resp.body?.string() ?: ""
                    if (!resp.isSuccessful) throw IllegalStateException("HTTP ${resp.code}: $body")
                    return body
                }
            } catch (e: Exception) {
                lastErr = e
                try { Thread.sleep(delayMs) } catch (_: InterruptedException) {}
                delayMs = (delayMs * 2).coerceAtMost(4000L)
                attempt++
            }
        }
        throw IllegalStateException("OpenAI request failed after $maxAttempts attempts: ${lastErr?.message}")
    }

    fun parseFirstMessageContent(responseJson: String): String {
        return try {
            val obj = JSONObject(responseJson)
            val choices = obj.getJSONArray("choices")
            if (choices.length() == 0) return responseJson
            val message = choices.getJSONObject(0).getJSONObject("message")
            message.getString("content")
        } catch (e: Exception) {
            responseJson
        }
    }

    fun imageUriToDataUrl(contentResolver: ContentResolver, imageUri: String): String {
        val uri = Uri.parse(imageUri)
        val maxDim = 512

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri).use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        }

        val (srcW, srcH) = bounds.outWidth to bounds.outHeight
        val sample = calculateInSampleSize(srcW, srcH, maxDim, maxDim)

        val decodeOpts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.RGB_565
        }

        val bitmap = contentResolver.openInputStream(uri).use { input ->
            BitmapFactory.decodeStream(input, null, decodeOpts)
        } ?: throw IllegalStateException("Failed to decode image")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        bitmap.recycle()

        val b64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
        return "data:image/jpeg;base64,$b64"
    }

    private fun calculateInSampleSize(srcW: Int, srcH: Int, reqW: Int, reqH: Int): Int {
        var inSampleSize = 1
        if (srcH > reqH || srcW > reqW) {
            var halfH = srcH / 2
            var halfW = srcW / 2
            while ((halfH / inSampleSize) >= reqH && (halfW / inSampleSize) >= reqW) {
                inSampleSize *= 2
            }
        }
        return inSampleSize.coerceAtLeast(1)
    }

    private fun jsonEscape(s: String): String {
        val escaped = s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
        return "\"$escaped\""
    }
}


