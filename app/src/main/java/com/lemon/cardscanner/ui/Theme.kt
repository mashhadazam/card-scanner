package com.lemon.cardscanner.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF38BDF8),
    secondary = androidx.compose.ui.graphics.Color(0xFF818CF8),
    background = androidx.compose.ui.graphics.Color(0xFF0F172A),
    surface = androidx.compose.ui.graphics.Color(0xFF1E293B),
    onBackground = androidx.compose.ui.graphics.Color(0xFFF1F5F9),
    onSurface = androidx.compose.ui.graphics.Color(0xFFF1F5F9),
)

@Composable
fun CardScannerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkColors, content = content)
}
