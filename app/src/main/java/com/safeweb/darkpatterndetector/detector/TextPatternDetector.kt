package com.safeweb.darkpatterndetector.detector

object TextPatternDetector {
    // Regex patterns for dark pattern detection (ported from Chrome extension)
    private val regexPatterns = mapOf(
        PatternType.FAKE_URGENCY to listOf(
            Regex("\\b(hurry|rush|act now|time is running out|offer ends|limited time|don't miss|expires?|flash sale|last day|ends (today|tonight|soon))\\b", RegexOption.IGNORE_CASE),
            Regex("\\d+\\s*:\\s*\\d+\\s*:\\s*\\d+", RegexOption.IGNORE_CASE), // Countdown timers
            Regex("only \\d+ (hours?|minutes?|days?) left", RegexOption.IGNORE_CASE)
        ),
        PatternType.FAKE_SCARCITY to listOf(
            Regex("\\b(only \\d+ left|almost (sold out|gone)|low stock|selling fast|few (left|remaining)|limited (stock|quantity|edition))\\b", RegexOption.IGNORE_CASE),
            Regex("\\b(while supplies last|last chance to buy)\\b", RegexOption.IGNORE_CASE)
        ),
        PatternType.FAKE_SOCIAL_PROOF to listOf(
            Regex("\\d+\\s*(people|users|customers|shoppers)\\s*(are )?(viewing|watching|looking|bought)", RegexOption.IGNORE_CASE),
            Regex("\\b(in \\d+ carts?|trending|bestseller|most popular|highly rated)\\b", RegexOption.IGNORE_CASE),
            Regex("purchased \\d+ times", RegexOption.IGNORE_CASE)
        ),
        PatternType.CONFIRMSHAMING to listOf(
            Regex("\\b(no thanks,? i (don't|hate|prefer)|i('d)? rather|skip (this|the) (great|amazing|exclusive))\\b", RegexOption.IGNORE_CASE),
            Regex("\\b(i don't (want|need|care)|i('ll)? pay (more|full price))\\b", RegexOption.IGNORE_CASE)
        ),
        PatternType.HIDDEN_COSTS to listOf(
            Regex("\\b(service fee|handling (fee|charge)|processing fee|convenience fee|booking fee|admin fee|surcharge|platform fee)\\b", RegexOption.IGNORE_CASE),
            Regex("\\b(additional (taxes|fees|charges)|taxes and fees)\\b", RegexOption.IGNORE_CASE)
        ),
        PatternType.HIDDEN_SUBSCRIPTION to listOf(
            Regex("\\b(free trial.{0,20}\\$\\d|auto(-|\\s)?renew|automatically (renews?|charged)|recurring.{0,15}(charge|payment|billing))\\b", RegexOption.IGNORE_CASE),
            Regex("\\b(billed (monthly|annually|weekly)|cancel anytime|subscription continues)\\b", RegexOption.IGNORE_CASE)
        ),
        PatternType.NAGGING to listOf(
            Regex("\\b(complete your (profile|purchase)|enable notifications|left items? in your cart|don't forget to|we noticed you)\\b", RegexOption.IGNORE_CASE),
            Regex("\\b(your cart is waiting|come back|unfinished)\\b", RegexOption.IGNORE_CASE)
        ),
        PatternType.OBSTRUCTION to listOf(
            Regex("\\b(call (to|us to) cancel|cancellation requires|speak(ing)? to a representative|before you (go|leave))\\b", RegexOption.IGNORE_CASE),
            Regex("\\b(you'll lose|lose (all|your) (benefits|access|data))\\b", RegexOption.IGNORE_CASE)
        ),
        PatternType.PRESELECTION to listOf(
            Regex("\\b(sign me up|subscribe to|opt[- ]?in|add (gift wrapping|extended warranty|insurance|protection))\\b", RegexOption.IGNORE_CASE)
        ),
        PatternType.TRICK_WORDING to listOf(
            Regex("\\b(uncheck to|opt out of not|do not wish to not|untick to disable)\\b", RegexOption.IGNORE_CASE)
        ),
        PatternType.FORCED_ACTION to listOf(
            Regex("\\b(create an account to|sign up to (view|access|continue)|download (our|the) app to|share with \\d+ friends)\\b", RegexOption.IGNORE_CASE)
        ),
        PatternType.SNEAKING to listOf(
            Regex("\\b(added (extra|insurance|warranty)|donation|automatically added|item(s)? (you might like|suggested))\\b", RegexOption.IGNORE_CASE)
        )
    )

    /**
     * Match extracted text against regex patterns.
     */
    fun matchPatterns(text: String): List<DarkPattern> {
        val detected = mutableListOf<DarkPattern>()

        for ((type, regexList) in regexPatterns) {
            for (regex in regexList) {
                val match = regex.find(text)
                if (match != null) {
                    detected.add(
                        DarkPattern(
                            type = type,
                            description = "${type.displayName} detected via text analysis",
                            confidence = 0.6f,
                            matchedText = match.value
                        )
                    )
                    break // One match per type
                }
            }
        }

        return detected
    }
}
