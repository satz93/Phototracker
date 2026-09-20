@file:OptIn(ExperimentalTextApi::class)

package com.phototracker.app.ui.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.phototracker.app.R

val IBMPlexMono = FontFamily(
    Font(R.font.ibm_plex_mono_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium),
)

val IBMPlexSans = FontFamily(
    Font(
        R.font.ibm_plex_sans_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400),
            FontVariation.width(100f),
        ),
    ),
    Font(
        R.font.ibm_plex_sans_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500),
            FontVariation.width(100f),
        ),
    ),
)
