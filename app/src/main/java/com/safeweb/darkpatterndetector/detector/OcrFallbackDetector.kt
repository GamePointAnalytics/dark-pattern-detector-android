package com.safeweb.darkpatterndetector.detector

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Fallback detector using ML Kit OCR + regex matching.
 * Used when Gemini Nano is unavailable on the device.
 * Still 100% on-device — no network required.
 */
class OcrFallbackDetector(private val context: Context) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Analyze a screenshot using OCR + regex matching.
     */
    suspend fun analyze(bitmap: Bitmap): AnalysisResult {
        val startTime = System.currentTimeMillis()

        // Step 1: Extract text using ML Kit OCR
        val extractedText = extractText(bitmap)

        // Step 2: Match against regex patterns using shared detector
        val patterns = TextPatternDetector.matchPatterns(extractedText)

        val elapsed = System.currentTimeMillis() - startTime

        return AnalysisResult(
            patterns = patterns,
            modelUsed = "OCR + Regex (fallback)",
            analysisTimeMs = elapsed,
            extractedText = extractedText
        )
    }

    /**
     * Extract text from bitmap using ML Kit Text Recognition.
     */
    private suspend fun extractText(bitmap: Bitmap): String {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        return suspendCancellableCoroutine { cont ->
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    cont.resume(visionText.text)
                }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }
}
