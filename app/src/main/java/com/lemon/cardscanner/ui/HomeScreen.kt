package com.lemon.cardscanner.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lemon.cardscanner.core.CardConfig
import com.lemon.cardscanner.core.OffersSnapshot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    cards: List<CardConfig>,
    offers: OffersSnapshot,
    onScan: () -> Unit,
    onCard: (CardConfig) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Card Scanner") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onScan,
                icon = { Icon(Icons.Filled.PhotoCamera, contentDescription = null) },
                text = { Text("Scan a card") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Catalog" + (offers.generatedAt?.let { " · backend scanned $it" } ?: ""),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cards) { card ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCard(card) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(card.name, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    card.issuer,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (card.annualFeeCad != null) {
                                    Text(
                                        "$${card.annualFeeCad}/yr",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (card.welcomeBonus.headline.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    card.welcomeBonus.headline,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
