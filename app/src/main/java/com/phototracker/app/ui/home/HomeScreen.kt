package com.phototracker.app.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.phototracker.app.data.GOAL_DAY_OPTIONS
import com.phototracker.app.data.PhotoRepository
import com.phototracker.app.ui.calendar.CalendarContent
import com.phototracker.app.ui.grid.DaysGridContent
import com.phototracker.app.ui.theme.ChipBorder
import com.phototracker.app.ui.theme.IBMPlexMono
import com.phototracker.app.ui.theme.IBMPlexSans
import com.phototracker.app.ui.theme.InkFaded
import com.phototracker.app.ui.theme.InkText
import com.phototracker.app.ui.theme.Parchment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class HomeTab { Calendar, Days }

@Composable
fun HomeScreen(onDayClick: (Int) -> Unit) {
    val context = LocalContext.current
    val repository = remember { PhotoRepository(context) }
    var refreshTick by remember { mutableIntStateOf(0) }
    var selectedTab by remember { mutableStateOf(HomeTab.Days) }
    var showInfo by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showStartOver by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        refreshTick++
        onPauseOrDispose { }
    }

    val goalDays = remember(refreshTick) { repository.goalDays() }

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
                    text = "$goalDays day transformation",
                    color = InkText,
                    fontFamily = IBMPlexSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = InkText)
                }
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
        InfoSheet(
            goalDays = goalDays,
            onDismiss = { showInfo = false },
        )
    }

    if (showSettings) {
        SettingsSheet(
            startDate = repository.startDate(),
            goalDays = goalDays,
            onStartOverClick = {
                showSettings = false
                showStartOver = true
            },
            onDismiss = { showSettings = false },
        )
    }

    if (showStartOver) {
        StartOverSheet(
            currentGoal = goalDays,
            onConfirm = { newGoal ->
                repository.startOver(newGoal)
                refreshTick++
                showStartOver = false
            },
            onDismiss = { showStartOver = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoSheet(
    goalDays: Int,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "$goalDays day transformation",
                color = InkText,
                fontFamily = IBMPlexSans,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Tap any day to snap a photo. Once captured, it turns into a little sticker on the grid so you can watch your progress build up, day by day. Switch to Calendar to see your days laid out across real months. Use Settings to start over with a different goal length.",
                color = InkFaded,
                fontFamily = IBMPlexSans,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
            ) {
                Text(text = "Got it", fontFamily = IBMPlexSans, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsSheet(
    startDate: LocalDate,
    goalDays: Int,
    onStartOverClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "Settings",
                color = InkText,
                fontFamily = IBMPlexSans,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
            )
            Spacer(Modifier.height(24.dp))
            SettingsRow(
                label = "Start date",
                value = startDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())),
            )
            Spacer(Modifier.height(20.dp))
            SettingsRow(label = "Transformation goal", value = "$goalDays days")
            Spacer(Modifier.height(24.dp))
            OutlinedButton(
                onClick = onStartOverClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, InkText),
            ) {
                Text(text = "Start over", color = InkText, fontFamily = IBMPlexSans, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Column {
        Text(text = label, color = InkFaded, fontFamily = IBMPlexSans, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        Text(text = value, color = InkText, fontFamily = IBMPlexSans, fontWeight = FontWeight.Medium, fontSize = 16.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartOverSheet(
    currentGoal: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedGoal by remember(currentGoal) { mutableStateOf(currentGoal) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "Start over transformation",
                color = InkText,
                fontFamily = IBMPlexSans,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "This resets your transformation journey and resets all progress and starts from zero",
                color = InkFaded,
                fontFamily = IBMPlexSans,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(24.dp))
            Text(text = "Pick transformation goal", color = InkFaded, fontFamily = IBMPlexSans, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            GOAL_DAY_OPTIONS.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedGoal = option }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedGoal == option,
                        onClick = { selectedGoal = option },
                        colors = RadioButtonDefaults.colors(selectedColor = Color.Black, unselectedColor = ChipBorder),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(text = "$option days", color = InkText, fontFamily = IBMPlexSans, fontSize = 15.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, InkText),
                ) {
                    Text(text = "Cancel", color = InkText, fontFamily = IBMPlexSans, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
                Button(
                    onClick = { onConfirm(selectedGoal) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                ) {
                    Text(text = "Start over", fontFamily = IBMPlexSans, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
            }
        }
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
