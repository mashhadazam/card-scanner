package com.lemon.cardscanner.banks.cibc

import com.lemon.cardscanner.core.BonusInfo
import com.lemon.cardscanner.core.CardConfig

/**
 * CIBC cards. Facts verified 2026-09-28 from cibc.com and RedFlagDeals.
 * binPrefixes stay empty until verified from a real card scan.
 */
object CibcCatalog {
    const val ISSUER = "CIBC"

    val cards: List<CardConfig> = listOf(
        CardConfig(
            id = "cibc-aventura-visa-infinite",
            name = "CIBC Aventura Visa Infinite",
            issuer = ISSUER,
            network = "Visa",
            annualFeeCad = 139,
            annualFeeNote = "\$139 primary / \$50 per authorized user (up to 3) — rebated in the first year",
            welcomeBonus = BonusInfo(
                headline = "Up to 60,000 Aventura points + first-year fee rebate (up to \$1,800 value)",
                details = listOf(
                    "15,000 Aventura points on first purchase",
                    "30,000 Aventura points on \$3,000 spend in the first 4 monthly statements",
                    "15,000 Aventura points on \$5,000 in net purchases in the first 4 monthly statements",
                    "4 complimentary lounge visits at 1,200+ lounges (Visa Airport Companion)",
                    "NEXUS application fee rebate (\$160 value)",
                ),
                url = "https://www.cibc.com/en/special-offers/aventura-rewards-inspire.html",
            ),
            earnRates = mapOf(
                "Travel through the CIBC Rewards Centre" to "2 points per \$1",
                "Gas, EV charging, grocery and drug stores" to "1.5 points per \$1",
                "All other purchases" to "1 point per \$1",
            ),
            perks = listOf(
                "4 complimentary lounge visits per year (Visa Airport Companion)",
                "NEXUS application fee rebate",
                "Book travel with Aventura points via CIBC Rewards Centre",
            ),
            offerPageUrl = "https://www.cibc.com/en/special-offers/aventura-rewards-inspire.html",
            sourcesNote = "Facts verified 2026-09-28 from cibc.com and RedFlagDeals.",
        ),
    )
}
