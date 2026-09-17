package com.phototracker.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = InkText,
    onPrimary = Parchment,
    background = Parchment,
    onBackground = InkText,
    surface = CardCream,
    onSurface = InkText,
    secondary = HandwriteBlue,
)

@Composable
fun PhotoTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content,
    )
}
