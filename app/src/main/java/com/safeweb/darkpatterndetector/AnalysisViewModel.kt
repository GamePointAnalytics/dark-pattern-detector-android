package com.safeweb.darkpatterndetector

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safeweb.darkpatterndetector.detector.AnalysisResult
import com.safeweb.darkpatterndetector.detector.DarkPatternAnalyzer
import com.safeweb.darkpatterndetector.detector.GeminiNanoDetector
import com.safeweb.darkpatterndetector.detector.WebsiteTextAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import java.io.InputStream

/**
 * ViewModel for the analysis flow.
 * Manages the screenshot bitmap (in-memory only) and analysis state.
 */
class AnalysisViewModel : ViewModel() {

    sealed class UiState {
        data object Idle : UiState()
        data object Analyzing : UiState()
        data class Results(
            val result: AnalysisResult,
            val screenshotBitmap: Bitmap? = null
        ) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Session-level check for AI availability
    private var isAiAvailable: Boolean? = null

    // Hold the bitmap in memory only — never persisted
    private var currentBitmap: Bitmap? = null

    /**
     * Check if Gemini Nano is available on this device once per session.
     */
    private suspend fun checkAiAvailability(context: Context): Boolean {
        if (isAiAvailable != null) return isAiAvailable!!
        val available = GeminiNanoDetector.isAvailable(context)
        isAiAvailable = available
        return available
    }

    /**
     * Load a bitmap from a content URI and start analysis.
     */
    fun analyzeFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.Analyzing

            try {
                val bitmap = loadBitmap(context, uri)
                if (bitmap == null) {
                    _uiState.value = UiState.Error("Could not load image")
                    return@launch
                }

                currentBitmap = bitmap
                val aiReady = checkAiAvailability(context)
                val analyzer = DarkPatternAnalyzer(context)
                val result = analyzer.analyze(bitmap, aiReady)

                _uiState.value = UiState.Results(
                    result = result,
                    screenshotBitmap = bitmap
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Analyze website text.
     */
    fun analyzeWebsite(context: Context, text: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Analyzing
            try {
                val aiReady = checkAiAvailability(context)
                val analyzer = WebsiteTextAnalyzer()
                val result = analyzer.analyze(text, aiReady)

                _uiState.value = UiState.Results(
                    result = result
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Website analysis failed: ${e.message}")
            }
        }
    }

    /**
     * Reset to idle state and discard the bitmap from memory.
     */
    fun reset() {
        currentBitmap?.recycle()
        currentBitmap = null
        _uiState.value = UiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        currentBitmap?.recycle()
        currentBitmap = null
    }

    private suspend fun loadBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }
        } catch (_: Exception) {
            null
        }
    }
}
