package com.example.cheating_glasses

import android.content.Context
import android.util.Log
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cheating_glasses.utils.CameraManager
import com.example.cheating_glasses.utils.OpenAIClient
import com.example.cheating_glasses.utils.VoskTranscriber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val API_KEY = "KEY_HERE"

const val SYSTEM_PROMPT = """
You are a helpful interview coding assistant. You are given a problem and a transcript of the user's conversation. You need to help the user solve the problem.
These are coding problems. The problem is that the user has a small screen so you must be susinct and use bullet points. TEll them exactly how to solve the problem (alg, the 'trick' and try to keep it as suscint as possible)

You must return nothing except these bullet points and help with the problem requested:

= Example:
User: *Image of 1+1*
Output: "- This is a simple math arithmetic problem
        - 1+1 = 2 - the answer is 2"
"""

class VuzixViewModel : ViewModel() {
    
    private var cameraManager: CameraManager? = null
    private var appContext: Context? = null
    private var vosk: VoskTranscriber? = null
    private var openAI: OpenAIClient? = null
    
    private val _transcript = MutableStateFlow("")
    val transcript: StateFlow<String> = _transcript.asStateFlow()
    
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()
    
    private val _keywordDetected = MutableStateFlow(false)
    val keywordDetected: StateFlow<Boolean> = _keywordDetected.asStateFlow()
    
    private val _lastCapturedImage = MutableStateFlow<String?>(null)
    val lastCapturedImage: StateFlow<String?> = _lastCapturedImage.asStateFlow()
    
    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()
    
    private val _captureStatus = MutableStateFlow<CameraManager.CaptureStatus>(CameraManager.CaptureStatus.Idle)
    val captureStatus: StateFlow<CameraManager.CaptureStatus> = _captureStatus.asStateFlow()

    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()
    
    // Accumulate full transcript until keyword is spoken
    private var accumulatedFinalText: String = ""
    private var lastPartialText: String = ""
    private var transcriptSnapshotBeforeSolve: String = ""
    
    fun initialize(context: Context, lifecycleOwner: LifecycleOwner, previewView: PreviewView? = null) {
        appContext = context.applicationContext
        Log.d("VuzixVM", "initialize: Vosk-only mode")
        
        cameraManager = CameraManager(context).apply {
            initializeCamera(lifecycleOwner, previewView)
        }

        // On-device STT using Vosk for live transcript
        vosk = VoskTranscriber(context)
        vosk?.onSolveDetected = {
            _keywordDetected.value = true
            _statusMessage.value = "Keyword 'solve' detected! Capturing photo..."
            capturePhoto()
            transcriptSnapshotBeforeSolve = _transcript.value.trim()
            lastPartialText = ""
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                _keywordDetected.value = false
            }, 2000)
        }
        viewModelScope.launch {
            vosk?.partial?.collect { text ->
                if (text.isNotEmpty()) {
                    Log.i("Transcript", text)
                    lastPartialText = text
                    val combined = (accumulatedFinalText + " " + lastPartialText).trim()
                    _transcript.value = combined
                    // keyword detection handled inside VoskTranscriber
                }
            }
        }
        viewModelScope.launch {
            vosk?.final?.collect { text ->
                if (text.isNotEmpty()) {
                    Log.i("Transcript", text)
                    accumulatedFinalText = (accumulatedFinalText + " " + text).trim()
                    lastPartialText = ""
                    _transcript.value = accumulatedFinalText
                    // keyword detection handled inside VoskTranscriber
                }
            }
        }
        viewModelScope.launch {
            vosk?.status?.collect { s ->
                if (s.isNotEmpty()) _statusMessage.value = s
            }
        }
        
        // Removed Android SpeechRecognizer flows (Vosk-only)
        
        viewModelScope.launch {
            cameraManager?.captureStatus?.collect { status ->
                _captureStatus.value = status
            }
        }
        
        viewModelScope.launch {
            cameraManager?.lastCapturedImageUri?.collect { uri ->
                uri?.let {
                    _lastCapturedImage.value = it
                    _statusMessage.value = "Photo saved successfully!"
                    // After capture + solve, send to OpenAI
                    if (transcriptSnapshotBeforeSolve.isNotEmpty()) {
                        sendToOpenAI(transcriptSnapshotBeforeSolve, it)
                        transcriptSnapshotBeforeSolve = ""
                    }
                }
            }
        }
        
        // Removed SpeechRecognizer error flow
    }
    
    fun startListening() {
        Log.d("VuzixVM", "startListening -> Vosk")
        _isListening.value = true
        vosk?.start()
        _statusMessage.value = "On-device transcription active (Vosk)"
    }
    
    fun stopListening() {
       // speechRecognitionService?.stopListening()
        viewModelScope.launch { vosk?.stop() }
        _statusMessage.value = "Stopped listening"
    }
    
    fun capturePhoto() {
        cameraManager?.capturePhoto(
            onSuccess = { uri ->
                _lastCapturedImage.value = uri
                _statusMessage.value = "Photo captured and saved!"
            },
            onError = { error ->
                _statusMessage.value = "Error: $error"
            }
        )
    }

    private fun sendToOpenAI(transcriptBeforeSolve: String, imageUri: String) {
        val ctx = appContext ?: return
        if (openAI == null) {
            // Hardcoded API key per request - replace later
            openAI = OpenAIClient(API_KEY)
        }
        viewModelScope.launch {
            try {
                val dataUrl = withContext(Dispatchers.IO) {
                    openAI!!.imageUriToDataUrl(ctx.contentResolver, imageUri)
                }
                val payload = openAI!!.buildVisionPayload(SYSTEM_PROMPT, transcriptBeforeSolve, dataUrl)
                val response = withContext(Dispatchers.IO) {
                    openAI!!.callChatCompletions(payload)
                }
                val content = openAI!!.parseFirstMessageContent(response)
                _aiResponse.value = content
                _statusMessage.value = "AI response received"
            } catch (e: Exception) {
                _aiResponse.value = "OpenAI error: ${e.message}"
                _statusMessage.value = "OpenAI error"
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        cameraManager?.shutdown()
        viewModelScope.launch { vosk?.stop() }
    }
}

