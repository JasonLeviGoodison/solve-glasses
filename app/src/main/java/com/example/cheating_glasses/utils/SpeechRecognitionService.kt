package com.example.cheating_glasses.utils

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

class SpeechRecognitionService(private val context: Context) {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private val _transcriptFlow = MutableStateFlow("")
    val transcriptFlow: StateFlow<String> = _transcriptFlow.asStateFlow()
    
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()
    
    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()
    
    var onPartialResults: ((String) -> Unit)? = null
    
    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d("SpeechRecognition", "Ready for speech")
            _isListening.value = true
        }
        
        override fun onBeginningOfSpeech() {
            Log.d("SpeechRecognition", "Beginning of speech")
        }
        
        override fun onRmsChanged(rmsdB: Float) {}
        
        override fun onBufferReceived(buffer: ByteArray?) {}
        
        override fun onEndOfSpeech() {
            Log.d("SpeechRecognition", "End of speech")
            _isListening.value = false
        }
        
        override fun onError(error: Int) {
            _isListening.value = false
            val errorMessage = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No match"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
            Log.e("SpeechRecognition", "Error: $errorMessage (code: $error)")
            _errorFlow.value = errorMessage
            
            if (error != SpeechRecognizer.ERROR_NO_MATCH && 
                error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                restartListening()
            } else {
                restartListening()
            }
        }
        
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val text = matches[0]
                Log.d("SpeechRecognition", "Final results: $text")
                _transcriptFlow.value = text
            }
            restartListening()
        }
        
        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val text = matches[0]
                Log.d("SpeechRecognition", "Partial results: $text")
                _transcriptFlow.value = text
                onPartialResults?.invoke(text)
            }
        }
        
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
    
    fun startListening() {
        Log.d("SpeechRecognition", "startListening() called")
        
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            val error = "Speech recognition not available on this device"
            Log.e("SpeechRecognition", error)
            _errorFlow.value = error
            _isListening.value = false
            return // Don't retry if fundamentally unavailable
        }
        
        if (speechRecognizer == null) {
            Log.d("SpeechRecognition", "Creating new SpeechRecognizer")
            // Try Vuzix speech service first, then default
            val componentName = android.content.ComponentName(
                "com.vuzix.speechrecognitionservice",
                "com.vuzix.speechrecognitionservice.VuzixSpeechRecognitionService"
            )
            
            speechRecognizer = try {
                Log.d("SpeechRecognition", "Attempting to use Vuzix speech service")
                SpeechRecognizer.createSpeechRecognizer(context, componentName).apply {
                    setRecognitionListener(recognitionListener)
                }
            } catch (e: Exception) {
                Log.w("SpeechRecognition", "Vuzix service not available, using default: ${e.message}")
                SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(recognitionListener)
                }
            }
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
        }
        
        Log.d("SpeechRecognition", "Starting speech recognition")
        speechRecognizer?.startListening(intent)
    }
    
    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }
    
    private fun restartListening() {
        // Only restart if recognition is available
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w("SpeechRecognition", "Not restarting - speech recognition unavailable")
            _isListening.value = false
            return
        }
        
        speechRecognizer?.cancel()
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            startListening()
        }, 100)
    }
    
    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        _isListening.value = false
    }
}

