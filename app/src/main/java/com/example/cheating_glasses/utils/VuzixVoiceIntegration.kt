package com.example.cheating_glasses.utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.vuzix.sdk.speechrecognitionservice.VuzixSpeechClient

class VuzixVoiceIntegration(
    private val activity: Activity,
    private val onPhrase: (String) -> Unit
) : BroadcastReceiver() {

    companion object {
        private const val LOG_TAG = "VuzixVoice"
        private const val KEYWORD_SOLVE = "solve"
        private const val MATCH_SOLVE = "MATCH_SOLVE"
    }

    private var speechClient: VuzixSpeechClient? = null

    fun start() {
        try {
            // Register receiver
            activity.registerReceiver(this, IntentFilter(VuzixSpeechClient.ACTION_VOICE_COMMAND))

            // Init client and vocabulary
            speechClient = VuzixSpeechClient(activity).apply {
                // Clear any previous app-added phrases if desired
                // deleteAllPhrases()

                // Ensure wake words exist for consistency
                insertWakeWordPhrase("hello vuzix")
                insertVoiceOffPhrase("voice off")

                // Add our trigger; use substitution to avoid underscore handling
                insertPhrase(KEYWORD_SOLVE, MATCH_SOLVE)

                Log.i(LOG_TAG, dump())
            }
        } catch (e: RuntimeException) {
            // Non‑Vuzix hardware or proguard/R8 issue
            Log.e(LOG_TAG, "VuzixSpeechClient init failed: ${e.message}")
        } catch (e: NoClassDefFoundError) {
            Log.e(LOG_TAG, "VuzixSpeechClient API unsupported on this device")
        }
    }

    fun stop() {
        try {
            activity.unregisterReceiver(this)
        } catch (_: Exception) {
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == VuzixSpeechClient.ACTION_VOICE_COMMAND) {
            val phrase = intent.getStringExtra(VuzixSpeechClient.PHRASE_STRING_EXTRA)
            if (phrase == MATCH_SOLVE) {
                onPhrase(KEYWORD_SOLVE)
            }
        }
    }
}


