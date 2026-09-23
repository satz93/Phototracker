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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.phototracker.app.data.PhotoRepository
import com.phototracker.app.ui.theme.CheckerBase
import com.phototracker.app.ui.theme.CheckerLine
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
            .checkerBackground(),
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(12.dp),
        ) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = InkText)
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 42.dp),
        ) {
            PolaroidCard(
                day = day,
                hasPhoto = hasPhoto,
                capturedAt = capturedAt,
                photoPainter = photoPainter,
                onTap = { cameraLauncher.launch(photoUri) },
            )
        }
    }
}

private fun Modifier.checkerBackground(
    cellSize: Dp = 25.dp,
    lineColor: Color = CheckerLine,
    baseColor: Color = CheckerBase,
): Modifier = this
    .background(baseColor)
    .drawBehind {
        val cellPx = cellSize.toPx()
        var x = 0f
        while (x <= size.width) {
            drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
            x += cellPx
        }
        var y = 0f
        while (y <= size.height) {
            drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            y += cellPx
        }
    }

@Composable
private fun PolaroidCard(
    day: Int,
    hasPhoto: Boolean,
    capturedAt: Long?,
    photoPainter: Painter?,
    onTap: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.785f)
            .shadow(8.dp, RoundedCornerShape(3.dp))
            .background(Color.White, RoundedCornerShape(3.dp))
            .clickable(onClick = onTap)
            .padding(10.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.78f)
                    .clip(RoundedCornerShape(1.dp)),
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
                            .background(PlaceholderMoss),
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
                        .padding(8.dp),
                )

                if (hasPhoto && capturedAt != null) {
                    Text(
                        text = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(capturedAt)),
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.22f)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .size(width = 60.dp, height = 8.dp)
                            .align(Alignment.BottomStart)
                            .background(HighlightYellow.copy(alpha = 0.7f)),
                    )
                    Text(
                        text = "Day $day",
                        color = HandwriteBlue,
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
