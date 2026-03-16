package com.safeweb.darkpatterndetector.detector

import android.util.Log
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerateContentRequest
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.TextPart
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

private const val TAG = "WebsiteTextAnalyzer"

/**
 * Analyzes extracted website text for dark patterns.
 *
 * Strategy:
 *  1. Try Gemini Nano if available (best accuracy).
 *  2. Fall back to local regex matching.
 */
class WebsiteTextAnalyzer {

    /**
     * Analyze website text.
     * 
     * @param extractedText The text to analyze
     * @param isAiAvailable Whether Gemini Nano is available for this session
     */
    suspend fun analyze(extractedText: String, isAiAvailable: Boolean): AnalysisResult {
        val startTime = System.currentTimeMillis()

        // --- Attempt 1: Gemini Nano Prompt API (only if available) ---
        var nanoResult: List<DarkPattern>? = null
        if (isAiAvailable) {
            nanoResult = try {
                withTimeoutOrNull(8.seconds) { // Reduced timeout for better session feel
                    tryGeminiNano(extractedText)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini Nano process failed", e)
                null
            }
        }

        if (nanoResult != null) {
            val elapsed = System.currentTimeMillis() - startTime
            return AnalysisResult(
                patterns = nanoResult,
                modelUsed = "Gemini Nano (Text, on-device)",
                analysisTimeMs = elapsed,
                extractedText = extractedText
            )
        }

        // --- Attempt 2: Regex fallback ---
        val regexPatterns = TextPatternDetector.matchPatterns(extractedText)
        val elapsed = System.currentTimeMillis() - startTime
        return AnalysisResult(
            patterns = regexPatterns,
            modelUsed = "Regex (on-device fallback)",
            analysisTimeMs = elapsed,
            extractedText = extractedText
        )
    }

    private suspend fun tryGeminiNano(text: String): List<DarkPattern>? {
        return try {
            val client = Generation.getClient()

            // Safety check
            val statusInt = client.checkStatus()
            if (statusInt != FeatureStatus.AVAILABLE) return null

            val promptText = buildPrompt(text)
            val request = GenerateContentRequest.builder(TextPart(promptText)).build()
            
            val response = client.generateContent(request)
            val responseText = response.candidates.firstOrNull()?.text

            parseNanoResponse(responseText)
        } catch (_: Exception) {
            null
        }
    }

    private fun buildPrompt(websiteText: String): String {
        val truncated = if (websiteText.length > 1000) websiteText.take(1000) + "..." else websiteText
        return """
            Analyze the following website text for dark patterns.
            Output format: PATTERN: <type>|<text excerpt>
            Types: FAKE_URGENCY, FAKE_SCARCITY, FAKE_SOCIAL_PROOF, CONFIRMSHAMING, HIDDEN_COSTS, HIDDEN_SUBSCRIPTION, NAGGING, OBSTRUCTION, PRESELECTION, TRICK_WORDING, FORCED_ACTION, SNEAKING.
            If none, output: NONE
            
            Text: $truncated
        """.trimIndent()
    }

    private fun parseNanoResponse(response: String?): List<DarkPattern>? {
        if (response.isNullOrBlank() || response.trim().uppercase() == "NONE") return null

        val patterns = mutableListOf<DarkPattern>()
        val lineRegex = Regex("""PATTERN:\s*(\w+)\|(.+)""", RegexOption.IGNORE_CASE)

        for (line in response.lines()) {
            val match = lineRegex.find(line.trim()) ?: continue
            val typeName = match.groupValues[1]
            val matchedText = match.groupValues[2].trim()
            val patternType = PatternType.fromString(typeName)
            if (patternType != PatternType.UNKNOWN) {
                patterns.add(
                    DarkPattern(
                        type = patternType,
                        description = "${patternType.displayName} detected by Gemini Nano",
                        confidence = 0.85f,
                        matchedText = matchedText
                    )
                )
            }
        }

        return if (patterns.isNotEmpty()) patterns else null
    }
}
