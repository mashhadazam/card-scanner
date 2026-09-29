package com.lemon.cardscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lemon.cardscanner.core.CardConfig
import com.lemon.cardscanner.core.OffersSnapshot
import com.lemon.cardscanner.core.ScanResult
import com.lemon.cardscanner.ui.CardDetailScreen
import com.lemon.cardscanner.ui.HomeScreen
import com.lemon.cardscanner.ui.ScanScreen
import com.lemon.cardscanner.ui.theme.CardScannerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = ConfigRepository(applicationContext)
        setContent {
            CardScannerTheme {
                val navController = rememberNavController()
                var cards by remember { mutableStateOf(repo.loadBundledCards()) }
                var offers by remember { mutableStateOf(repo.loadBundledOffers()) }
                var lastScan by remember { mutableStateOf<ScanResult?>(null) }

                // Bundled configs show instantly; the backend feed refreshes silently.
                LaunchedEffect(Unit) {
                    repo.refreshFromBackend()?.let { (freshCards, freshOffers) ->
                        cards = freshCards
                        offers = freshOffers
                    }
                }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            cards = cards,
                            offers = offers,
                            onScan = { navController.navigate("scan") },
                            onCard = { card -> navController.navigate("detail/${card.id}") }
                        )
                    }
                    composable("scan") {
                        ScanScreen(
                            cards = cards,
                            onIdentified = { result ->
                                lastScan = result
                                val dest = result.matchedCardId?.let { "detail/$it" } ?: "home"
                                navController.navigate(dest) {
                                    popUpTo("home")
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("detail/{cardId}") { backStackEntry ->
                        val cardId = backStackEntry.arguments?.getString("cardId")
                        val card: CardConfig? = cards.firstOrNull { it.id == cardId }
                        if (card != null) {
                            CardDetailScreen(
                                card = card,
                                offer = offers.cards[card.id],
                                generatedAt = offers.generatedAt,
                                scan = lastScan?.takeIf { it.matchedCardId == card.id },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
