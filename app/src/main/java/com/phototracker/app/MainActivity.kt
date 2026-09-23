package com.phototracker.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.phototracker.app.ui.theme.PhotoTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The app always uses a light palette (no dark theme), so force dark system bar
        // icons/transparent scrims regardless of the device's system dark/light setting —
        // otherwise a phone in dark mode gets light icons and a scrim meant for a dark
        // background, which shows up as a mismatched white bar over our light background.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            PhotoTrackerTheme {
                PhotoTrackerApp()
            }
        }
    }
}
