package com.phototracker.app.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

const val TOTAL_DAYS = 90

class PhotoRepository(context: Context) {
    private val appContext = context.applicationContext
    private val photosDir = File(appContext.filesDir, "photos").apply { mkdirs() }

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

    fun deleteAll() {
        photosDir.listFiles()?.forEach { it.delete() }
    }

    fun completedCount(): Int = (1..TOTAL_DAYS).count { hasPhoto(it) }
}
