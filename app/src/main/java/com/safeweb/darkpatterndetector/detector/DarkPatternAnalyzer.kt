package com.safeweb.darkpatterndetector.detector

import android.content.Context
import android.graphics.Bitmap

/**
 * Orchestrator that selects the best available detection engine.
 * Tries Gemini Nano first (best accuracy), falls back to OCR + regex.
 * All processing is 100% on-device.
 */
class DarkPatternAnalyzer(private val context: Context) {

    /**
     * Analyze a screenshot for dark patterns.
     *
     * @param bitmap The screenshot to analyze (in-memory only)
     * @param isAiAvailable Whether Gemini Nano is available for this session
     * @return Analysis results
     */
    suspend fun analyze(bitmap: Bitmap, isAiAvailable: Boolean): AnalysisResult {
        // Use pre-determined availability to avoid slow system checks
        return if (isAiAvailable) {
            val result = GeminiNanoDetector(context).analyze(bitmap)
            if (result.patterns.isNotEmpty()) {
                // Gemini Nano found patterns — trust it
                result
            } else {
                // Gemini Nano found nothing — fallback to OCR safety net
                val ocrResult = OcrFallbackDetector(context).analyze(bitmap)
                if (ocrResult.patterns.isNotEmpty()) ocrResult else result
            }
        } else {
            // AI not available for this session, immediate OCR fallback
            OcrFallbackDetector(context).analyze(bitmap)
        }
    }
}
