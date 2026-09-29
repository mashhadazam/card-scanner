package com.lemon.cardscanner.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardMatcherTest {

    @Test
    fun `luhn accepts a valid test number`() {
        assertTrue(CardMatcher.luhnValid("4111111111111111"))
        assertTrue(CardMatcher.luhnValid("5555555555554444"))
    }

    @Test
    fun `luhn rejects a mistyped number`() {
        assertFalse(CardMatcher.luhnValid("4111111111111112"))
        assertFalse(CardMatcher.luhnValid("12345abc"))
    }

    @Test
    fun `extractCandidates finds spaced numbers and drops invalid ones`() {
        val text = """
            SOME BANK
            4111 1111 1111 1111
            4111 1111 1111 1112
            VALID THRU 09/28
        """.trimIndent()
        val found = CardMatcher.extractCandidates(text)
        assertEquals(listOf("4111111111111111"), found)
    }

    @Test
    fun `extractExpiry normalizes to MM slash YY`() {
        assertEquals("09/28", CardMatcher.extractExpiry("VALID THRU 09/28"))
        assertEquals("01/30", CardMatcher.extractExpiry("01 / 2030"))
        assertNull(CardMatcher.extractExpiry("no date here"))
    }

    @Test
    fun `extractHolderName picks the embossed caps line`() {
        val text = "4111 1111 1111 1111\nMOHAMMAD A AZAM\nVALID THRU 09/28"
        assertEquals("MOHAMMAD A AZAM", CardMatcher.extractHolderName(text))
    }

    @Test
    fun `matchBin prefers the longest prefix`() {
        val cards = listOf(
            CardConfig(id = "a", name = "A", issuer = "X", network = "Visa", binPrefixes = listOf("411111")),
            CardConfig(id = "b", name = "B", issuer = "X", network = "Visa", binPrefixes = listOf("41111111"))
        )
        val index = CardMatcher.buildBinIndex(cards)
        assertEquals("b", CardMatcher.matchBin("4111111111111111", index))
        assertEquals("a", CardMatcher.matchBin("4111112211111111", index))
        assertNull(CardMatcher.matchBin("5100001111111111", index))
    }
}
