package com.stadiolinks.kitabu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KitabuColors = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9F2D8),
    onPrimaryContainer = Color(0xFF0B3D12),
    secondary = Color(0xFFF9A825),
    tertiary = Color(0xFF1565C0),
    background = Color(0xFFF8FAF8),
    surface = Color.White
)

@Composable
fun KitabuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KitabuColors,
        content = content
    )
}
