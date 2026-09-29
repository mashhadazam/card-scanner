package com.lemon.cardscanner.core

import kotlinx.serialization.Serializable

/**
 * One card's display config. These live in configs/cards/ and are refreshed
 * by the daily backend scan. The app never invents card facts — it displays
 * exactly what is here.
 */
@Serializable
data class CardConfig(
    val id: String,
    val name: String,
    val issuer: String,
    val network: String,
    val annualFeeCad: Int? = null,
    val annualFeeNote: String = "",
    val welcomeBonus: BonusInfo = BonusInfo(),
    val earnRates: Map<String, String> = emptyMap(),
    val perks: List<String> = emptyList(),
    val transferPartners: List<String> = emptyList(),
    /** Known BIN/IIN prefixes (first 6-8 digits). Empty until verified from a real card. */
    val binPrefixes: List<String> = emptyList(),
    val offerPageUrl: String = "",
    /** Where the facts above were verified, and when. */
    val sourcesNote: String = "",
    val lastScanned: String? = null
)

@Serializable
data class BonusInfo(
    val headline: String = "See current public offer",
    val details: List<String> = emptyList(),
    val url: String = ""
)

@Serializable
data class CardIndex(
    val cards: List<String>,
    val updatedAt: String = ""
)

/** One card's entry in the daily backend scan output. */
@Serializable
data class CardOffer(
    val cardId: String,
    val fetchedAt: String = "",
    val pageTitle: String = "",
    val snippets: List<String> = emptyList()
)

@Serializable
data class OffersSnapshot(
    val generatedAt: String? = null,
    val cards: Map<String, CardOffer> = emptyMap()
)

/**
 * Result of an on-device card scan. Only the BIN + last4 are kept —
 * the full PAN is never stored or transmitted.
 */
@Serializable
data class ScanResult(
    val bin: String,
    val last4: String,
    val expiry: String? = null,
    val holderName: String? = null,
    val matchedCardId: String? = null
)
