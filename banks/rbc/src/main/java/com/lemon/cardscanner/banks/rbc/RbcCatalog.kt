package com.lemon.cardscanner.banks.rbc

import com.lemon.cardscanner.core.BonusInfo
import com.lemon.cardscanner.core.CardConfig

/**
 * RBC cards. Facts verified 2026-09-28 from rbcroyalbank.com, milesopedia.com
 * and moneywehave.com. binPrefixes stay empty until verified from a real card scan.
 */
object RbcCatalog {
    const val ISSUER = "RBC"

    val cards: List<CardConfig> = listOf(
        CardConfig(
            id = "rbc-avion-visa-infinite",
            name = "RBC Avion Visa Infinite",
            issuer = ISSUER,
            network = "Visa",
            annualFeeCad = 120,
            annualFeeNote = "Primary cardholder",
            welcomeBonus = BonusInfo(
                headline = "Up to 70,000 Avion points (offer ends Nov 25, 2026)",
                details = listOf(
                    "35,000 Avion points on approval",
                    "20,000 Avion points on \$5,000 spend in the first 6 months",
                ),
                url = "https://www.rbcroyalbank.com/credit-cards/camp/iav/avion-infinite-visa-met/",
            ),
            earnRates = mapOf(
                "Travel purchases" to "1.25 Avion points per \$1",
                "All other eligible purchases" to "1 Avion point per \$1",
            ),
            perks = listOf(
                "Save 3¢/L on fuel and earn 20% more points at Petro-Canada when cards are linked",
                "Extra Be Well points at Rexall when cards are linked",
                "12-month complimentary DashPass subscription (activate via DoorDash)",
            ),
            transferPartners = listOf(
                "British Airways Avios — 1:1",
                "Cathay Pacific Asia Miles — 1:1",
                "WestJet Rewards — 1:1",
                "American Airlines AAdvantage — 10,000 Avion → 7,000 miles",
            ),
            offerPageUrl = "https://www.rbcroyalbank.com/credit-cards/camp/iav/avion-infinite-visa-met/",
            sourcesNote = "Facts verified 2026-09-28 from rbcroyalbank.com, milesopedia.com and moneywehave.com.",
        ),
    )
}
