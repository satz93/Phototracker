package com.phototracker.app.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phototracker.app.data.PhotoRepository
import com.phototracker.app.ui.components.BlankCell
import com.phototracker.app.ui.components.DayCell
import com.phototracker.app.ui.theme.IBMPlexMono
import com.phototracker.app.ui.theme.InkText
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private data class CalendarDay(val trackerDay: Int, val date: LocalDate)

private sealed interface CalendarRowItem {
    data class MonthLabel(val yearMonth: YearMonth) : CalendarRowItem
    data class WeekRow(val cells: List<CalendarDay?>) : CalendarRowItem
}

/** The "Calendar" tab: the tracked days laid out across real weekday-aligned months. */
@Composable
fun CalendarContent(
    repository: PhotoRepository,
    refreshTick: Int,
    onDayClick: (Int) -> Unit,
) {
    val rowItems = remember(refreshTick) { buildCalendarRows(repository) }
    val today = remember(refreshTick) { LocalDate.now() }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(rowItems) { item ->
            when (item) {
                is CalendarRowItem.MonthLabel -> {
                    Text(
                        text = item.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        color = InkText,
                        fontFamily = IBMPlexMono,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                    )
                }

                is CalendarRowItem.WeekRow -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        item.cells.forEach { cell ->
                            if (cell != null) {
                                DayCell(
                                    day = cell.trackerDay,
                                    label = cell.date.dayOfMonth,
                                    hasPhoto = repository.hasPhoto(cell.trackerDay),
                                    photoFile = repository.photoFile(cell.trackerDay),
                                    isToday = cell.date == today,
                                    onClick = { onDayClick(cell.trackerDay) },
                                )
                            } else {
                                BlankCell()
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Sunday-first weekday-aligned month grids spanning the tracker's start date through its last day. */
private fun buildCalendarRows(repository: PhotoRepository): List<CalendarRowItem> {
    val startMonth = YearMonth.from(repository.startDate())
    val endMonth = YearMonth.from(repository.dateForDay(repository.goalDays()))

    val items = mutableListOf<CalendarRowItem>()
    var month = startMonth
    while (!month.isAfter(endMonth)) {
        items += CalendarRowItem.MonthLabel(month)

        val firstOfMonth = month.atDay(1)
        val leadingBlanks = firstOfMonth.dayOfWeek.value % 7 // Sunday-first week

        val cells = mutableListOf<CalendarDay?>()
        repeat(leadingBlanks) { cells += null }
        for (dayOfMonth in 1..month.lengthOfMonth()) {
            val date = month.atDay(dayOfMonth)
            val trackerDay = repository.dayForDate(date)
            cells += if (trackerDay != null) CalendarDay(trackerDay, date) else null
        }
        while (cells.size % 7 != 0) cells += null

        cells.chunked(7).forEach { week -> items += CalendarRowItem.WeekRow(week) }

        month = month.plusMonths(1)
    }
    return items
}
