package com.phototracker.app.ui.detail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.phototracker.app.data.PhotoRepository
import com.phototracker.app.ui.theme.HandwriteBlue
import com.phototracker.app.ui.theme.HighlightYellow
import com.phototracker.app.ui.theme.InkFaded
import com.phototracker.app.ui.theme.InkText
import com.phototracker.app.ui.theme.PlaceholderMoss
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DayDetailScreen(day: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { PhotoRepository(context) }
    var refreshTick by remember { mutableIntStateOf(0) }

    val hasPhoto = remember(refreshTick, day) { repository.hasPhoto(day) }
    val capturedAt = remember(refreshTick, day) { repository.capturedAtMillis(day) }
    val photoUri = remember(day) { repository.uriForCamera(day) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) refreshTick++
    }

    val photoPainter = if (hasPhoto) {
        rememberAsyncImagePainter(
            model = remember(refreshTick, day) {
                ImageRequest.Builder(context)
                    .data(repository.photoFile(day))
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .build()
            },
        )
    } else {
        null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopStart),
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = InkText)
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 40.dp),
        ) {
            StampCard(
                day = day,
                hasPhoto = hasPhoto,
                capturedAt = capturedAt,
                photoPainter = photoPainter,
                onTap = { cameraLauncher.launch(photoUri) },
            )
        }
    }
}

@Composable
private fun StampCard(
    day: Int,
    hasPhoto: Boolean,
    capturedAt: Long?,
    photoPainter: Painter?,
    onTap: () -> Unit,
) {
    val rotation = remember(day) { (day * 17 % 7 - 3).toFloat() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .rotate(rotation)
            .clickable(onClick = onTap),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.85f)
                .shadow(10.dp, RoundedCornerShape(6.dp))
                .background(Color.White, RoundedCornerShape(6.dp))
                .padding(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(2.dp)),
            ) {
                if (hasPhoto && photoPainter != null) {
                    Image(
                        painter = photoPainter,
                        contentDescription = "Day $day photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(listOf(PlaceholderMoss, Color(0xFFEFE9DD))),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Tap to add your day $day photo",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 24.dp),
                        )
                    }
                }

                Text(
                    text = "Day $day",
                    color = InkFaded,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp),
                )

                if (hasPhoto && capturedAt != null) {
                    Text(
                        text = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(capturedAt)),
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp),
                    )
                }
            }
        }

        Box(modifier = Modifier.padding(start = 16.dp, top = 10.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 4.dp)
                    .size(width = 62.dp, height = 8.dp)
                    .background(HighlightYellow.copy(alpha = 0.7f)),
            )
            Text(
                text = "Day $day",
                color = HandwriteBlue,
                fontSize = 26.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
