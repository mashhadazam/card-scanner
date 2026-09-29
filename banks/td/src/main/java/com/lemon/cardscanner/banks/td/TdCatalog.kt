package com.lemon.cardscanner.banks.td

import com.lemon.cardscanner.core.BonusInfo
import com.lemon.cardscanner.core.CardConfig

/**
 * TD cards. Facts verified 2026-09-28 from td.com, milesopedia.com and
 * frugalflyer.ca. binPrefixes stay empty until verified from a real card scan.
 */
object TdCatalog {
    const val ISSUER = "TD"

    val cards: List<CardConfig> = listOf(
        CardConfig(
            id = "td-first-class-travel-visa-infinite",
            name = "TD First Class Travel Visa Infinite",
            issuer = ISSUER,
            network = "Visa",
            annualFeeCad = 139,
            annualFeeNote = "First-year annual fee rebate for new cardholders",
            welcomeBonus = BonusInfo(
                headline = "Up to 160,000 TD Rewards points + first-year fee rebate",
                details = listOf(
                    "20,000 TD Rewards points on first purchase",
                    "140,000 TD Rewards points on \$7,500 spend within 180 days of account opening",
                    "Birthday bonus of up to 10,000 TD Rewards points per year",
                    "\$100 annual TD Travel Credit when booking through Expedia For TD",
                    "4 complimentary airport lounge visits per year (Visa Airport Companion)",
                ),
                url = "https://td.com/ca/en/personal-banking/products/credit-cards/travel-rewards/first-class-travel-visa-infinite-card",
            ),
            earnRates = mapOf(
                "Travel booked through Expedia For TD" to "8 points per \$1",
                "Groceries, dining and public transit" to "6 points per \$1",
                "Recurring bill payments, streaming, digital gaming and media" to "4 points per \$1",
                "All other purchases" to "2 points per \$1",
            ),
            perks = listOf(
                "\$100 annual TD Travel Credit on Expedia For TD bookings",
                "4 complimentary lounge visits per year via Visa Airport Companion",
                "Annual fee rebated every year with a TD All-Inclusive Banking Plan",
            ),
            offerPageUrl = "https://td.com/ca/en/personal-banking/products/credit-cards/travel-rewards/first-class-travel-visa-infinite-card",
            sourcesNote = "Facts verified 2026-09-28 from td.com, milesopedia.com and frugalflyer.ca.",
        ),
    )
}
