package com.example.data.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentSpeakingMessageId = MutableStateFlow<Long?>(null)
    val currentSpeakingMessageId: StateFlow<Long?> = _currentSpeakingMessageId.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsEngine = tts ?: return
            // Try Indonesian locale first, fallback to US or default
            val idLocale = Locale.Builder().setLanguage("id").setRegion("ID").build()
            val result = ttsEngine.setLanguage(idLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("TTS", "Indonesian TTS not supported, falling back to default locale")
                ttsEngine.language = Locale.getDefault()
            }
            ttsEngine.setPitch(1.05f) // slightly friendly pitch
            ttsEngine.setSpeechRate(1.0f)

            ttsEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentSpeakingMessageId.value = null
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentSpeakingMessageId.value = null
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    _currentSpeakingMessageId.value = null
                }
            })

            isInitialized = true
        } else {
            Log.e("TTS", "Initialization failed with status $status")
            isInitialized = false
        }
    }

    /**
     * Cleans markdown or special symbols from text for clearer speech synthesis
     */
    private fun sanitizeTextForSpeech(rawText: String): String {
        return rawText
            .replace(Regex("[*#_`~>|\\[\\]()]"), " ")
            .replace(Regex("https?://\\S+"), "tautan web")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun speak(messageId: Long, rawText: String) {
        if (!isInitialized || tts == null) {
            Log.w("TTS", "TTS not initialized yet")
            return
        }

        stop()

        val cleanText = sanitizeTextForSpeech(rawText)
        if (cleanText.isBlank()) return

        _currentSpeakingMessageId.value = messageId
        _isSpeaking.value = true

        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "msg_$messageId")
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TTS", "Error stopping TTS", e)
        }
        _isSpeaking.value = false
        _currentSpeakingMessageId.value = null
    }

    fun togglePlay(messageId: Long, text: String) {
        if (_isSpeaking.value && _currentSpeakingMessageId.value == messageId) {
            stop()
        } else {
            speak(messageId, text)
        }
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
