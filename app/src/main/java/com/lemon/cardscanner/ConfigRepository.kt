package com.lemon.cardscanner

import android.content.Context
import com.lemon.cardscanner.core.CardConfig
import com.lemon.cardscanner.core.CardIndex
import com.lemon.cardscanner.core.OffersSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

/**
 * The app is a thin display client. Configs ship bundled (same files the
 * backend scans every day) and are refreshed from the backend feed when online.
 */
class ConfigRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    fun loadBundledCards(): List<CardConfig> {
        val index: CardIndex = context.assets.open("index.json").use { stream ->
            json.decodeFromString(CardIndex.serializer(), stream.readBytes().toString(Charsets.UTF_8))
        }
        return index.cards.mapNotNull { id ->
            runCatching {
                context.assets.open("cards/$id.json").use { stream ->
                    json.decodeFromString(
                        CardConfig.serializer(),
                        stream.readBytes().toString(Charsets.UTF_8)
                    )
                }
            }.getOrNull()
        }
    }

    fun loadBundledOffers(): OffersSnapshot = runCatching {
        context.assets.open("offers.json").use { stream ->
            json.decodeFromString(OffersSnapshot.serializer(), stream.readBytes().toString(Charsets.UTF_8))
        }
    }.getOrDefault(OffersSnapshot())

    /** Returns fresh (cards, offers) from the backend feed, or null when offline. */
    suspend fun refreshFromBackend(): Pair<List<CardConfig>, OffersSnapshot>? =
        withContext(Dispatchers.IO) {
            runCatching {
                val base = BuildConfig.CONFIG_BASE_URL
                val index: CardIndex =
                    json.decodeFromString(CardIndex.serializer(), httpGet("$base/index.json"))
                val cards = index.cards.map { id ->
                    json.decodeFromString(CardConfig.serializer(), httpGet("$base/cards/$id.json"))
                }
                val offers =
                    json.decodeFromString(OffersSnapshot.serializer(), httpGet("$base/offers.json"))
                cards to offers
            }.getOrNull()
        }

    private fun httpGet(url: String): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 8_000
            readTimeout = 8_000
        }
        return conn.inputStream.use { it.readBytes().toString(Charsets.UTF_8) }
            .also { conn.disconnect() }
    }
}
