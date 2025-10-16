package com.example.cheating_glasses.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.vosk.Model
import org.vosk.Recognizer
import java.io.File

class VoskTranscriber(private val context: Context) {

    companion object {
        private const val LOG_TAG = "VoskTranscriber"
        private const val SAMPLE_RATE = 16000
        private const val MODEL_DIR_NAME = "vosk-model-small-en-us-0.15"
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    private var audioRecord: AudioRecord? = null
    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var recordingJob: Job? = null

    private val _partial = MutableStateFlow("")
    val partial: StateFlow<String> = _partial.asStateFlow()

    private val _final = MutableStateFlow("")
    val final: StateFlow<String> = _final.asStateFlow()

    private val _status = MutableStateFlow("")
    val status: StateFlow<String> = _status.asStateFlow()

    var onSolveDetected: (() -> Unit)? = null
    private var lastSolveTriggerMs = 0L

    private fun resolveModelDir(): File {
        val base = context.getExternalFilesDir(null) ?: context.filesDir
        return File(base, MODEL_DIR_NAME)
    }

    fun isModelAvailable(): Boolean {
        val dir = resolveModelDir()
        Log.d(LOG_TAG, "Checking model at: ${dir.absolutePath}")
        return dir.exists() && dir.isDirectory
    }

    fun start() {
        Log.d(LOG_TAG, "start() called")
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            _status.value = "Microphone permission not granted"
            Log.e(LOG_TAG, "Mic permission not granted")
            return
        }

        if (!isModelAvailable()) {
            val expected = resolveModelDir().absolutePath
            val msg = "Vosk model missing at: $expected"
            _status.value = msg
            Log.e(LOG_TAG, msg)
            return
        }

        try {
            val modelPath = resolveModelDir()
            Log.d(LOG_TAG, "Loading model from: ${modelPath.absolutePath}")
            model = Model(modelPath.absolutePath)
            recognizer = Recognizer(model, SAMPLE_RATE.toFloat())
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to init model: ${e.message}", e)
            _status.value = "Failed to init Vosk model"
            return
        }

        val minBuf = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = minBuf.coerceAtLeast(4096) * 2

        // Try VOICE_RECOGNITION first, then fallback to MIC if not recording
        fun createRecorder(source: Int): AudioRecord {
            return AudioRecord(
                source,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
        }

        audioRecord = createRecorder(MediaRecorder.AudioSource.VOICE_RECOGNITION)
        audioRecord?.startRecording()
        val startedWithVr = audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING
        Log.d(LOG_TAG, "AudioRecord start VR state=${audioRecord?.recordingState}")
        if (!startedWithVr) {
            try { audioRecord?.release() } catch (_: Exception) {}
            audioRecord = createRecorder(MediaRecorder.AudioSource.MIC)
            audioRecord?.startRecording()
            Log.d(LOG_TAG, "AudioRecord start MIC state=${audioRecord?.recordingState}")
        }

        if (audioRecord?.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
            _status.value = "AudioRecord failed to start"
            Log.e(LOG_TAG, "AudioRecord failed to start with both sources")
            return
        }
        Log.d(LOG_TAG, "AudioRecord started")
        _status.value = "Vosk listening..."

        recordingJob = scope.launch {
            val buffer = ShortArray(4096)
            while (isActive) {
                val n = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (n > 0) {
                    val rec = recognizer ?: continue
                    // Optional: simple RMS to verify mic activity
                    var sum = 0.0
                    for (i in 0 until n) { val s = buffer[i].toInt(); sum += s * s }
                    val rms = kotlin.math.sqrt(sum / n).toInt()
                    if (rms % 3000 == 0) { // sparse log
                        Log.d(LOG_TAG, "audio rms=$rms, n=$n")
                    }

                    if (rec.acceptWaveForm(buffer, n)) {
                        val result = rec.result
                        // result is JSON, but simplest: extract "text" quickly
                        val text = extractJsonField(result, "text")
                        if (text.isNotEmpty()) {
                            Log.d(LOG_TAG, "final: $text")
                            Log.i("Transcript", text)
                            _final.value = text
                        }
                    } else {
                        val p = rec.partialResult
                        Log.d(LOG_TAG, "raw partial: $p")
                        val text = extractJsonField(p, "partial")
                        Log.d(LOG_TAG, text)
                        if (text.isNotEmpty()) {
                            Log.d(LOG_TAG, "partial: $text")
                            Log.i("Transcript", text)
                            _partial.value = text
                            if (containsWord(text, "solve")) maybeTriggerSolve()
                        }
                    }
                }
            }
        }
    }

    suspend fun stop() {
        try {
            recordingJob?.cancelAndJoin()
        } catch (_: Exception) {}
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {}
        audioRecord = null
        recognizer?.close()
        model?.close()
        recognizer = null
        model = null
        _status.value = "Vosk stopped"
        Log.d(LOG_TAG, "stop() complete")
    }

    private fun extractJsonField(json: String, key: String): String {
        val regex = Regex("\"" + Regex.escape(key) + "\"\\s*:\\s*\"([^\"]*)\"", RegexOption.MULTILINE)
        return regex.find(json)?.groupValues?.get(1) ?: ""
    }

    private fun containsWord(haystack: String, word: String): Boolean {
        return Regex("\\b" + Regex.escape(word) + "\\b", RegexOption.IGNORE_CASE).containsMatchIn(haystack)
    }

    private fun maybeTriggerSolve() {
        val now = System.currentTimeMillis()
        if (now - lastSolveTriggerMs < 1500) return
        lastSolveTriggerMs = now
        Log.d("onSolveDetected", "onSolveDetected")
        onSolveDetected?.invoke()
    }
}


