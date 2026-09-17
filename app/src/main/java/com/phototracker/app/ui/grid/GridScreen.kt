package com.phototracker.app.ui.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.phototracker.app.R
import com.phototracker.app.data.PhotoRepository
import com.phototracker.app.data.TOTAL_DAYS
import com.phototracker.app.ui.theme.ChipBorder
import com.phototracker.app.ui.theme.InkFaded
import com.phototracker.app.ui.theme.InkText

@Composable
fun GridScreen(onDayClick: (Int) -> Unit) {
    val context = LocalContext.current
    val repository = remember { PhotoRepository(context) }
    var refreshTick by remember { mutableIntStateOf(0) }
    var showInfo by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        refreshTick++
        onPauseOrDispose { }
    }

    val completed = remember(refreshTick) { repository.completedCount() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = stringResource(R.string.grid_title), style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "$completed / $TOTAL_DAYS days captured",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkFaded,
                )
            }
            Row {
                IconButton(onClick = { showResetConfirm = true }) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Reset progress", tint = InkText)
                }
                IconButton(onClick = { showInfo = true }) {
                    Icon(Icons.Outlined.Info, contentDescription = "About", tint = InkText)
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(TOTAL_DAYS) { index ->
                val day = index + 1
                DaySlot(
                    day = day,
                    hasPhoto = remember(refreshTick, day) { repository.hasPhoto(day) },
                    photoFile = repository.photoFile(day),
                    onClick = { onDayClick(day) },
                )
            }
        }
    }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            confirmButton = { TextButton(onClick = { showInfo = false }) { Text("Got it") } },
            title = { Text("90 day transformation") },
            text = { Text("Tap any day to snap a photo. Once captured, it turns into a little sticker on the grid so you can watch your progress build up, day by day.") },
        )
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset all photos?") },
            text = { Text("This deletes every photo you've captured for all $TOTAL_DAYS days. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    repository.deleteAll()
                    refreshTick++
                    showResetConfirm = false
                }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { showResetConfirm = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun DaySlot(
    day: Int,
    hasPhoto: Boolean,
    photoFile: java.io.File,
    onClick: () -> Unit,
) {
    val rotation = remember(day) { (day * 37 % 11 - 5).toFloat() }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, ChipBorder, RoundedCornerShape(10.dp))
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .rotate(rotation)
                    .clip(RoundedCornerShape(6.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(6.dp)),
            )
        } else {
            Text(text = day.toString(), style = MaterialTheme.typography.bodyMedium, color = InkText)
        }
    }
}
