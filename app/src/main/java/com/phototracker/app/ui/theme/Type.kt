package com.phototracker.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The app's type scale, built on Material 3's Typography system. IBM Plex Sans covers
 * headings/body/labels; IBM Plex Mono is reserved for tabular/numeric UI (day numbers, month
 * labels, the Days/Calendar toggle) to match the Figma spec. Swap the font families here if the
 * app's typeface changes later — call sites should reference these named styles, not raw sizes.
 */
val AppTypography = Typography(
    titleLarge = TextStyle(fontFamily = IBMPlexSans, fontWeight = FontWeight.Medium, fontSize = 20.sp),
    titleMedium = TextStyle(fontFamily = IBMPlexSans, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = IBMPlexSans, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = IBMPlexSans, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = IBMPlexSans, fontWeight = FontWeight.Normal, fontSize = 13.sp),
    labelLarge = TextStyle(fontFamily = IBMPlexSans, fontWeight = FontWeight.Medium, fontSize = 15.sp),
    labelMedium = TextStyle(fontFamily = IBMPlexMono, fontWeight = FontWeight.Normal, fontSize = 13.sp),
)
