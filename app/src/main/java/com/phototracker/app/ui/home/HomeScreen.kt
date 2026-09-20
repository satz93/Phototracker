package com.phototracker.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.phototracker.app.data.PhotoRepository
import com.phototracker.app.data.TOTAL_DAYS
import com.phototracker.app.ui.calendar.CalendarContent
import com.phototracker.app.ui.grid.DaysGridContent
import com.phototracker.app.ui.theme.ChipBorder
import com.phototracker.app.ui.theme.IBMPlexMono
import com.phototracker.app.ui.theme.IBMPlexSans
import com.phototracker.app.ui.theme.InkText
import com.phototracker.app.ui.theme.Parchment

private enum class HomeTab { Calendar, Days }

@Composable
fun HomeScreen(onDayClick: (Int) -> Unit) {
    val context = LocalContext.current
    val repository = remember { PhotoRepository(context) }
    var refreshTick by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableStateOf(HomeTab.Days) }
    var showInfo by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        refreshTick++
        onPauseOrDispose { }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Parchment),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "90 day transformation",
                    color = InkText,
                    fontFamily = IBMPlexSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { showInfo = true }) {
                    Icon(Icons.Outlined.Info, contentDescription = "About", tint = InkText)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 24.dp),
            ) {
                when (selectedTab) {
                    HomeTab.Days -> DaysGridContent(
                        repository = repository,
                        refreshTick = refreshTick,
                        onDayClick = onDayClick,
                    )

                    HomeTab.Calendar -> CalendarContent(
                        repository = repository,
                        refreshTick = refreshTick,
                        onDayClick = onDayClick,
                    )
                }
            }
        }

        ViewToggle(
            selected = selectedTab,
            onSelect = { selectedTab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
        )
    }

    if (showInfo) {
        AlertDialog(
            onDismissRequest = { showInfo = false },
            confirmButton = { TextButton(onClick = { showInfo = false }) { Text("Got it") } },
            dismissButton = {
                TextButton(onClick = {
                    repository.deleteAll()
                    refreshTick++
                    showInfo = false
                }) { Text("Reset all photos") }
            },
            title = { Text("90 day transformation") },
            text = { Text("Tap any day to snap a photo. Once captured, it turns into a little sticker on the grid so you can watch your progress build up, day by day. Switch to Calendar to see your $TOTAL_DAYS days laid out across real months.") },
        )
    }
}

@Composable
private fun ViewToggle(
    selected: HomeTab,
    onSelect: (HomeTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(11.dp))
            .border(BorderStroke(1.dp, ChipBorder), RoundedCornerShape(11.dp))
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        ToggleSegment(
            icon = Icons.Outlined.CalendarMonth,
            label = "Calendar",
            active = selected == HomeTab.Calendar,
            onClick = { onSelect(HomeTab.Calendar) },
        )
        ToggleSegment(
            icon = Icons.Outlined.GridView,
            label = "Days",
            active = selected == HomeTab.Days,
            onClick = { onSelect(HomeTab.Days) },
        )
    }
}

@Composable
private fun ToggleSegment(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    if (active) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .clickable(onClick = onClick)
                .padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(icon, contentDescription = label, tint = Color.White, modifier = Modifier.size(18.dp))
            Text(text = label, color = Color.White, fontFamily = IBMPlexMono, fontSize = 13.sp)
        }
    } else {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = label, tint = InkText, modifier = Modifier.size(18.dp))
        }
    }
}
