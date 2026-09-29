package com.lemon.cardscanner

import android.content.Context
import com.lemon.cardscanner.core.OffersSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

/**
 * The app is a thin display client. The card catalog is compiled in from the
 * bank modules ([BankCatalogs]); this repo only fetches the daily offer
 * snippets the backend scan writes to configs/offers.json.
 */
class ConfigRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    fun loadBundledOffers(): OffersSnapshot = runCatching {
        context.assets.open("offers.json").use { stream ->
            json.decodeFromString(OffersSnapshot.serializer(), stream.readBytes().toString(Charsets.UTF_8))
        }
    }.getOrDefault(OffersSnapshot())

    /** Returns fresh offers from the backend feed, or null when offline. */
    suspend fun refreshOffers(): OffersSnapshot? =
        withContext(Dispatchers.IO) {
            runCatching {
                json.decodeFromString(
                    OffersSnapshot.serializer(),
                    httpGet("${BuildConfig.CONFIG_BASE_URL}/offers.json")
                )
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
