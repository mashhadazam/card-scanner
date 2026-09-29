package com.lemon.cardscanner.banks.bmo

import com.lemon.cardscanner.core.BonusInfo
import com.lemon.cardscanner.core.CardConfig

/**
 * BMO cards. Facts verified 2026-09-28 from bmo.com. Regular annual fee not
 * confirmed — left blank on purpose. binPrefixes stay empty until verified
 * from a real card scan.
 */
object BmoCatalog {
    const val ISSUER = "BMO"

    val cards: List<CardConfig> = listOf(
        CardConfig(
            id = "bmo-ascend-world-elite",
            name = "BMO Ascend World Elite Mastercard",
            issuer = ISSUER,
            network = "Mastercard",
            annualFeeCad = null,
            annualFeeNote = "Annual fee waived in the first year under the current offer — confirm the regular fee on bmo.com",
            welcomeBonus = BonusInfo(
                headline = "Up to 115,000 BMO Rewards points + \$200 NEXUS credit + first year free (up to \$1,900 value)",
                details = listOf(
                    "Up to 115,000 BMO Rewards welcome bonus points",
                    "Up to \$200 NEXUS statement credit in the first year",
                    "Mastercard Travel Pass membership + 4 complimentary lounge passes",
                    "No annual fee in the first year for primary and authorized users",
                ),
                url = "https://www.bmo.com/en-ca/main/personal/credit-cards/bmo-ascend-world-elite-mastercard/?icid=US70652",
            ),
            earnRates = mapOf(
                "Eligible travel purchases" to "5 points per \$1",
                "Eligible dining, entertainment and recurring bill payments" to "3 points per \$1",
                "All other purchases" to "1 point per \$1",
            ),
            perks = listOf(
                "Mastercard Travel Pass (DragonPass) + 4 lounge passes per year",
                "Up to \$200 NEXUS statement credit in the first year",
                "6 months of Instacart+ and a \$10 monthly Instacart credit when enrolled",
            ),
            offerPageUrl = "https://www.bmo.com/en-ca/main/personal/credit-cards/bmo-ascend-world-elite-mastercard/?icid=US70652",
            sourcesNote = "Facts verified 2026-09-28 from bmo.com.",
        ),
    )
}
