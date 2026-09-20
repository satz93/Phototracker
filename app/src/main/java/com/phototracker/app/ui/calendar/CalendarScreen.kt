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
import com.phototracker.app.data.TOTAL_DAYS
import com.phototracker.app.ui.components.BlankCell
import com.phototracker.app.ui.components.DayCell
import com.phototracker.app.ui.theme.IBMPlexMono
import com.phototracker.app.ui.theme.InkText
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private sealed interface CalendarRowItem {
    data class MonthLabel(val yearMonth: YearMonth) : CalendarRowItem
    data class WeekRow(val cells: List<Int?>) : CalendarRowItem
}

/** The "Calendar" tab: the 90 tracker days laid out across real weekday-aligned months. */
@Composable
fun CalendarContent(
    repository: PhotoRepository,
    refreshTick: Int,
    onDayClick: (Int) -> Unit,
) {
    val rowItems = remember(refreshTick) { buildCalendarRows(repository) }

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
                        item.cells.forEach { day ->
                            if (day != null) {
                                DayCell(
                                    day = day,
                                    hasPhoto = repository.hasPhoto(day),
                                    photoFile = repository.photoFile(day),
                                    onClick = { onDayClick(day) },
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

/** Sunday-first weekday-aligned month grids spanning the tracker's start date through day 90. */
private fun buildCalendarRows(repository: PhotoRepository): List<CalendarRowItem> {
    val startMonth = YearMonth.from(repository.startDate())
    val endMonth = YearMonth.from(repository.dateForDay(TOTAL_DAYS))

    val items = mutableListOf<CalendarRowItem>()
    var month = startMonth
    while (!month.isAfter(endMonth)) {
        items += CalendarRowItem.MonthLabel(month)

        val firstOfMonth = month.atDay(1)
        val leadingBlanks = firstOfMonth.dayOfWeek.value % 7 // Sunday-first week

        val cells = mutableListOf<Int?>()
        repeat(leadingBlanks) { cells += null }
        for (dayOfMonth in 1..month.lengthOfMonth()) {
            cells += repository.dayForDate(month.atDay(dayOfMonth))
        }
        while (cells.size % 7 != 0) cells += null

        cells.chunked(7).forEach { week -> items += CalendarRowItem.WeekRow(week) }

        month = month.plusMonths(1)
    }
    return items
}
