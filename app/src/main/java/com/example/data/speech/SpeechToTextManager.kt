package com.example.data.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechToTextManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _liveSpokenText = MutableStateFlow("")
    val liveSpokenText: StateFlow<String> = _liveSpokenText.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(onTextRecognized: (String) -> Unit) {
        if (!isAvailable()) {
            _errorMessage.value = "Pengenal suara tidak tersedia di perangkat ini."
            return
        }

        stopListening()
        _errorMessage.value = null
        _liveSpokenText.value = ""

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        val errorDesc = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Kesalahan audio saat merekam."
                            SpeechRecognizer.ERROR_CLIENT -> null // normal cancellation
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Izin mikrofon belum diberikan."
                            SpeechRecognizer.ERROR_NETWORK -> "Kesalahan jaringan koneksi suara."
                            SpeechRecognizer.ERROR_NO_MATCH -> "Tidak ada kata yang terdeteksi."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Pengenal suara sedang sibuk."
                            SpeechRecognizer.ERROR_SERVER -> "Kesalahan server pengenal suara."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> null
                            else -> "Gagal mengenali suara ($error)."
                        }
                        if (errorDesc != null) {
                            _errorMessage.value = errorDesc
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _liveSpokenText.value = text
                            onTextRecognized(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _liveSpokenText.value = text
                            onTextRecognized(text)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "id-ID")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            Log.e("STT", "Error starting speech recognizer", e)
            _isListening.value = false
            _errorMessage.value = "Gagal memulai perekam: ${e.message}"
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("STT", "Error stopping speech recognizer", e)
        } finally {
            speechRecognizer = null
            _isListening.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun destroy() {
        stopListening()
    }
}
