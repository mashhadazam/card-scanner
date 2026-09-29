package com.lemon.cardscanner.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lemon.cardscanner.core.CardConfig
import com.lemon.cardscanner.core.CardOffer
import com.lemon.cardscanner.core.ScanResult

/** Pure display: everything on this screen comes from the backend configs. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    card: CardConfig,
    offer: CardOffer?,
    generatedAt: String?,
    scan: ScanResult?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(card.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            scan?.let {
                Text(
                    "Scanned card •••• ${it.last4}" +
                        (it.expiry?.let { e -> " · exp $e" } ?: "") +
                        (it.holderName?.let { n -> " · $n" } ?: ""),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
            }

            DetailRow("Issuer", card.issuer)
            DetailRow("Network", card.network)
            card.annualFeeCad?.let { DetailRow("Annual fee", "$$it CAD") }
            if (card.annualFeeNote.isNotBlank()) DetailRow("Fee note", card.annualFeeNote)

            Section("Welcome bonus")
            Text(card.welcomeBonus.headline, style = MaterialTheme.typography.bodyMedium)
            card.welcomeBonus.details.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }

            if (card.earnRates.isNotEmpty()) {
                Section("Earn rates")
                card.earnRates.forEach { (k, v) -> DetailRow(k, v) }
            }
            if (card.transferPartners.isNotEmpty()) {
                Section("Transfer partners")
                card.transferPartners.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }
            if (card.perks.isNotEmpty()) {
                Section("Perks")
                card.perks.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
            }

            offer?.let {
                Section("Latest backend scan")
                if (it.snippets.isEmpty()) {
                    Text(
                        "Scanned ${it.fetchedAt} — no offer changes detected.",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    it.snippets.forEach { s ->
                        Text("• $s", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider()
            Text(
                "Catalog data as of ${generatedAt ?: "bundled build"}. " +
                    "Refreshed daily by the backend scan — the app only displays it.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (card.sourcesNote.isNotBlank()) {
                Text(
                    card.sourcesNote,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun Section(title: String) {
    Spacer(Modifier.height(12.dp))
    Text(title, style = MaterialTheme.typography.titleSmall)
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun DetailRow(label: String, value: String) {
    Text(
        "$label: $value",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
