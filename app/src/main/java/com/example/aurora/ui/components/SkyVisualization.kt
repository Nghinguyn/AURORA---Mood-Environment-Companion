package com.example.aurora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurora.data.model.Mood
import com.example.aurora.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SkyVisualization(
    currentMood: Mood? = null,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now()
            delay(1000)
        }
    }

    val hour = currentTime.hour
    val skyColors = getSkyGradient(hour)
    val subtitle = getSubtitle(hour, currentMood)
    val isDaytime = hour in 8..16

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(skyColors),
        contentAlignment = Alignment.Center
    ) {
        if (isDaytime) {
            Cloud(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 30.dp, y = 40.dp)
                    .size(width = 120.dp, height = 50.dp)
            )
            Cloud(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-40).dp, y = 60.dp)
                    .size(width = 100.dp, height = 40.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("h:mm")),
                fontSize = 64.sp,
                fontWeight = FontWeight.Thin,
                color = SoftWhite
            )

            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("a")).uppercase(),
                fontSize = 14.sp,
                color = SoftWhite.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentTime.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                fontSize = 16.sp,
                color = SoftWhite.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = subtitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = SoftWhite.copy(alpha = 0.6f)
            )

            currentMood?.let { mood ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${mood.emoji} ${mood.label}",
                    fontSize = 14.sp,
                    color = SoftWhite.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun Cloud(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(SoftWhite.copy(alpha = 0.25f))
    )
}

private fun getSkyGradient(hour: Int): Brush {
    val colors = when (hour) {
        in 0..5 -> listOf(Color(0xFF0a0a15), Color(0xFF151535))
        in 6..7 -> listOf(Color(0xFF2d2040), Color(0xFFc76b4a))
        in 8..16 -> listOf(Color(0xFF4a90c2), Color(0xFF87ceeb))
        in 17..19 -> listOf(Color(0xFF3d4060), Color(0xFFe87040))
        else -> listOf(Color(0xFF0d1020), Color(0xFF1f2045))
    }
    return Brush.verticalGradient(colors)
}

private fun getSubtitle(hour: Int, mood: Mood?): String {
    if (mood != null) {
        return when (mood) {
            Mood.HAPPY -> "Joy fills the air"
            Mood.SAD -> "Take it slow today"
            Mood.ANXIOUS -> "Breathe deeply"
            Mood.CALM -> "Peace flows through you"
            Mood.ANGRY -> "Let the storm pass"
            Mood.EXCITED -> "Energy surrounds you"
            Mood.TIRED -> "Rest is calling"
            Mood.GRATEFUL -> "Blessings abound"
            Mood.CONFUSED -> "Clarity will come"
            Mood.HOPEFUL -> "Possibilities await"
        }
    }
    
    return when (hour) {
        in 0..5 -> "The world sleeps softly"
        in 6..7 -> "The sky awakens"
        in 8..11 -> "A fresh day unfolds"
        in 12..14 -> "Midday warmth"
        in 15..17 -> "Afternoon light"
        in 18..19 -> "Sunset paints the sky"
        else -> "Stars appear above"
    }
}
