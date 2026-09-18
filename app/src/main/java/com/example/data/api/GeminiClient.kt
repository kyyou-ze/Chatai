package com.example.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GeminiMessage(
    val role: String, // "user" or "model"
    val text: String
)

sealed class GeminiResult {
    data class Success(val text: String) : GeminiResult()
    data class Error(val message: String, val isKeyError: Boolean = false) : GeminiResult()
}

class GeminiClient {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateResponse(
        apiKey: String,
        systemInstruction: String,
        history: List<GeminiMessage>
    ): GeminiResult = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext GeminiResult.Error(
                "API Key belum diatur. Silakan masukkan Google AI Studio API Key terlebih dahulu.",
                isKeyError = true
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val payload = JSONObject().apply {
                // System Instruction
                if (systemInstruction.isNotBlank()) {
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemInstruction)
                            })
                        })
                    })
                }

                // Conversation contents (limit to recent 20 messages for context)
                val recentHistory = if (history.size > 20) history.takeLast(20) else history
                val contentsArray = JSONArray()
                for (msg in recentHistory) {
                    val role = if (msg.role == "user") "user" else "model"
                    contentsArray.put(JSONObject().apply {
                        put("role", role)
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", msg.text)
                            })
                        })
                    })
                }
                put("contents", contentsArray)

                // Generation Config
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.85)
                    put("topP", 0.95)
                })
            }

            val requestBody = payload.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody).optJSONObject("error")
                    val message = errJson?.optString("message") ?: "Error ${response.code}"
                    val status = errJson?.optString("status") ?: ""
                    if (response.code == 400 || response.code == 403 || status == "PERMISSION_DENIED" || status == "API_KEY_INVALID") {
                        return@withContext GeminiResult.Error(
                            "API Key tidak valid atau tidak memiliki akses: $message",
                            isKeyError = true
                        )
                    }
                    message
                } catch (e: Exception) {
                    "HTTP ${response.code}: ${response.message}"
                }
                return@withContext GeminiResult.Error("Gagal menghubungi AI ($errorMsg)")
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext GeminiResult.Error("AI tidak memberikan respons.")
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (text.isBlank()) {
                return@withContext GeminiResult.Error("Respons AI kosong.")
            }

            GeminiResult.Success(text.trim())
        } catch (e: java.net.UnknownHostException) {
            GeminiResult.Error("Koneksi internet bermasalah. Periksa jaringan kamu.")
        } catch (e: java.net.SocketTimeoutException) {
            GeminiResult.Error("Waktu tunggu habis (timeout). Coba kirim pesan lagi.")
        } catch (e: Exception) {
            GeminiResult.Error("Terjadi kesalahan: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun validateApiKey(apiKey: String): Boolean = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey.length < 10) return@withContext false
        try {
            val result = generateResponse(
                apiKey = apiKey,
                systemInstruction = "You are a test helper.",
                history = listOf(GeminiMessage("user", "Hello"))
            )
            result is GeminiResult.Success
        } catch (e: Exception) {
            false
        }
    }
}
