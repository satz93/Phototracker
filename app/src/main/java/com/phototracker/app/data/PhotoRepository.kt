package com.phototracker.app.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

const val DEFAULT_GOAL_DAYS = 90
val GOAL_DAY_OPTIONS = listOf(90, 75, 60)

private const val PREFS_NAME = "phototracker_prefs"
private const val KEY_START_DATE_EPOCH_DAY = "start_date_epoch_day"
private const val KEY_GOAL_DAYS = "goal_days"
private const val KEY_USER_NAME = "user_name"

class PhotoRepository(context: Context) {
    private val appContext = context.applicationContext
    private val photosDir = File(appContext.filesDir, "photos").apply { mkdirs() }
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun photoFile(day: Int): File = File(photosDir, "day_$day.jpg")

    fun hasPhoto(day: Int): Boolean = photoFile(day).exists()

    fun capturedAtMillis(day: Int): Long? =
        photoFile(day).takeIf { it.exists() }?.lastModified()

    fun uriForCamera(day: Int): Uri {
        val file = photoFile(day)
        return FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            file,
        )
    }

    /** Length of the current transformation, in days (90/75/60). */
    fun goalDays(): Int = prefs.getInt(KEY_GOAL_DAYS, DEFAULT_GOAL_DAYS)

    fun userName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    /** Deletes every captured photo and restarts the tracker from today with a new goal length. */
    fun startOver(goalDays: Int) {
        photosDir.listFiles()?.forEach { it.delete() }
        val today = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate()
        prefs.edit()
            .putLong(KEY_START_DATE_EPOCH_DAY, today.toEpochDay())
            .putInt(KEY_GOAL_DAYS, goalDays)
            .apply()
    }

    fun completedCount(): Int = (1..goalDays()).count { hasPhoto(it) }

    /** Day 1 of the tracker. Defaults to today, fixed the first time it's read. */
    fun startDate(): LocalDate {
        val stored = prefs.getLong(KEY_START_DATE_EPOCH_DAY, -1L)
        if (stored != -1L) return LocalDate.ofEpochDay(stored)
        val today = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.systemDefault()).toLocalDate()
        prefs.edit().putLong(KEY_START_DATE_EPOCH_DAY, today.toEpochDay()).apply()
        return today
    }

    fun dateForDay(day: Int): LocalDate = startDate().plusDays((day - 1).toLong())

    /** Which tracker day (1..goalDays()) a calendar date is, or null if outside the tracked window. */
    fun dayForDate(date: LocalDate): Int? {
        val diff = ChronoUnit.DAYS.between(startDate(), date).toInt() + 1
        return diff.takeIf { it in 1..goalDays() }
    }
}
