package com.example.cheating_glasses.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class KeywordDetector(private val keyword: String = "solve") {
    
    private val _keywordDetected = MutableStateFlow(false)
    val keywordDetected: StateFlow<Boolean> = _keywordDetected.asStateFlow()
    
    private val _lastDetectedText = MutableStateFlow("")
    val lastDetectedText: StateFlow<String> = _lastDetectedText.asStateFlow()
    
    var onKeywordDetected: ((String) -> Unit)? = null
    
    fun processText(text: String) {
        val normalizedText = text.lowercase(Locale.getDefault())
        val normalizedKeyword = keyword.lowercase(Locale.getDefault())
        
        if (normalizedText.contains(normalizedKeyword)) {
            _keywordDetected.value = true
            _lastDetectedText.value = text
            onKeywordDetected?.invoke(text)
            
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                _keywordDetected.value = false
            }, 1000)
        }
    }
    
    fun reset() {
        _keywordDetected.value = false
        _lastDetectedText.value = ""
    }
}

