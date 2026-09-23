package com.phototracker.app.ui.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.phototracker.app.data.PhotoRepository
import com.phototracker.app.ui.components.DayCell
import java.time.LocalDate

/** The "Days" tab: every tracker day in a flat 7-column grid. */
@Composable
fun DaysGridContent(
    repository: PhotoRepository,
    refreshTick: Int,
    onDayClick: (Int) -> Unit,
) {
    val rows = remember(refreshTick) { (1..repository.goalDays()).chunked(7) }
    val today = remember(refreshTick) { LocalDate.now() }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(rows) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                row.forEach { day ->
                    DayCell(
                        day = day,
                        hasPhoto = repository.hasPhoto(day),
                        photoFile = repository.photoFile(day),
                        isToday = repository.dateForDay(day) == today,
                        onClick = { onDayClick(day) },
                    )
                }
            }
        }
    }
}
