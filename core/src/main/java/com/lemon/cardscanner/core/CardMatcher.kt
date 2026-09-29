package com.lemon.cardscanner.core

/**
 * Pure card-scan logic: pull candidate numbers / expiry / holder name out of
 * OCR text and match the BIN against the catalog configs.
 */
object CardMatcher {

    private val numberRegex = Regex("""(?:\d[ ]*){13,19}""")
    private val expiryRegex = Regex("""\b(0[1-9]|1[0-2])\s?/\s?(\d{2}|\d{4})\b""")

    /** Luhn-valid 13-19 digit candidates, most plausible first. */
    fun extractCandidates(text: String): List<String> =
        numberRegex.findAll(text)
            .map { it.value.filter(Char::isDigit) }
            .filter { it.length in 13..19 && luhnValid(it) }
            .distinct()
            .toList()

    fun extractExpiry(text: String): String? =
        expiryRegex.find(text)?.let {
            "${it.groupValues[1]}/${it.groupValues[2].takeLast(2)}"
        }

    /** Best-effort embossed name: longest all-caps, letter-only line. */
    fun extractHolderName(text: String): String? =
        text.lines()
            .map { it.trim() }
            .filter { line ->
                line.length >= 5 &&
                    line.any(Char::isLetter) &&
                    !line.any(Char::isDigit) &&
                    line.all { c -> c.isUpperCase() || c == ' ' || c == '.' || c == '-' || c == '\'' }
            }
            .maxByOrNull { it.length }

    fun luhnValid(number: String): Boolean {
        if (number.any { !it.isDigit() }) return false
        var sum = 0
        var double = false
        for (i in number.length - 1 downTo 0) {
            var d = number[i] - '0'
            if (double) {
                d *= 2
                if (d > 9) d -= 9
            }
            sum += d
            double = !double
        }
        return sum % 10 == 0
    }

    /** Longest BIN prefix wins. Returns the card id, or null when unknown. */
    fun matchBin(number: String, binIndex: Map<String, String>): String? {
        for (len in minOf(8, number.length) downTo 6) {
            binIndex[number.take(len)]?.let { return it }
        }
        return null
    }

    fun buildBinIndex(cards: List<CardConfig>): Map<String, String> =
        cards.flatMap { card -> card.binPrefixes.map { prefix -> prefix to card.id } }.toMap()
}
