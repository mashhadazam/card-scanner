package com.lemon.cardscanner.banks.amex

import com.lemon.cardscanner.core.BonusInfo
import com.lemon.cardscanner.core.CardConfig

/**
 * American Express cards. Earn rates and 1:1 transfers verified 2026-09-28
 * from americanexpress.com. Fee and welcome bonus change often — left to the
 * daily backend scan. binPrefixes stay empty until verified from a real card scan.
 */
object AmexCatalog {
    const val ISSUER = "American Express"

    val cards: List<CardConfig> = listOf(
        CardConfig(
            id = "amex-cobalt",
            name = "American Express Cobalt Card",
            issuer = ISSUER,
            network = "Amex",
            annualFeeCad = null,
            annualFeeNote = "Monthly fee — confirm the current amount on americanexpress.com",
            welcomeBonus = BonusInfo(
                headline = "Membership Rewards — see the current public offer",
                url = "https://www.americanexpress.com/ca/en/credit-cards/cobalt-card/",
            ),
            earnRates = mapOf(
                "Eligible eats, drinks, groceries and food delivery in Canada" to "5 points per \$1",
                "Eligible streaming subscriptions in Canada" to "3 points per \$1",
                "Eligible gas, transit and rideshare in Canada" to "2 points per \$1",
                "All other purchases" to "1 point per \$1",
            ),
            perks = listOf(
                "Front Of The Line presale and reserved tickets",
                "Additional cards at no additional cost",
            ),
            transferPartners = listOf(
                "Frequent flyer and loyalty programs — 1:1 (see americanexpress.com for the current list)",
            ),
            offerPageUrl = "https://www.americanexpress.com/ca/en/credit-cards/cobalt-card/",
            sourcesNote = "Earn rates and 1:1 transfers verified 2026-09-28 from americanexpress.com.",
        ),
    )
}
