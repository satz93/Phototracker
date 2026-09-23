package com.phototracker.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.phototracker.app.ui.theme.ChipBorder
import com.phototracker.app.ui.theme.IBMPlexMono
import com.phototracker.app.ui.theme.InkFaded
import com.phototracker.app.ui.theme.InkText
import java.io.File

private val CellShape = RoundedCornerShape(8.dp)

/**
 * A single 42x42dp tracker-day cell, matching the Figma home-page spec.
 *
 * [day] is the tracker day (1..goalDays), used to look up/save its photo. [label] is what
 * number is actually shown — usually the same as [day], but the calendar view shows the
 * real day-of-month instead. [isToday] draws a black outline instead of the default grey one.
 */
@Composable
fun DayCell(
    day: Int,
    hasPhoto: Boolean,
    photoFile: File,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: Int = day,
    isToday: Boolean = false,
) {
    val context = LocalContext.current
    val borderStroke = if (isToday) BorderStroke(2.dp, Color.Black) else BorderStroke(1.dp, ChipBorder)

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CellShape)
            .background(Color.White)
            .border(borderStroke, CellShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (hasPhoto) {
            AsyncImage(
                model = remember(photoFile.lastModified()) {
                    ImageRequest.Builder(context)
                        .data(photoFile)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .diskCachePolicy(CachePolicy.DISABLED)
                        .build()
                },
                contentDescription = "Day $day photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Text(
                text = label.toString(),
                color = InkText,
                fontFamily = IBMPlexMono,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
            )
        }
    }
}

/** An empty, invisible grid slot used to pad a week row for calendar alignment. */
@Composable
fun BlankCell(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(42.dp))
}

/** A real calendar date outside the tracked window (e.g. before the start date) — shown, not clickable. */
@Composable
fun DisabledDayCell(label: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CellShape)
            .background(Color.White)
            .border(BorderStroke(1.dp, ChipBorder), CellShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.toString(),
            color = InkFaded,
            fontFamily = IBMPlexMono,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
        )
    }
}
