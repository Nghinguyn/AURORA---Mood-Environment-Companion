package com.example.aurora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurora.R
import com.example.aurora.data.db.MoodEntry
import com.example.aurora.ui.theme.AuroraGreen
import com.example.aurora.ui.theme.LemonYellow
import com.example.aurora.ui.theme.SoftWhite
import com.example.aurora.ui.theme.VioletGlow
import java.time.LocalDate

@Composable
fun JourneyCard(
    recentMoods: List<MoodEntry>,
    totalEntries: Int,
    currentStreak: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1a1a2e))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.your_journey),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftWhite
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.open),
                    tint = SoftWhite.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            WeekRow(recentMoods = recentMoods)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = totalEntries.toString(),
                    label = stringResource(R.string.entries),
                    color = AuroraGreen
                )
                StatItem(
                    value = "🔥 $currentStreak",
                    label = stringResource(R.string.streak),
                    color = LemonYellow
                )
                StatItem(
                    value = recentMoods.groupBy { it.mood }
                        .maxByOrNull { it.value.size }?.key?.emoji ?: "—",
                    label = stringResource(R.string.top_mood),
                    color = VioletGlow
                )
            }
        }
    }
}

@Composable
private fun WeekRow(recentMoods: List<MoodEntry>) {
    val today = LocalDate.now()
    val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
    val moodsByDate = recentMoods.groupBy { it.date }
    
    val dayAbbreviations = listOf(
        stringResource(R.string.day_mon),
        stringResource(R.string.day_tue),
        stringResource(R.string.day_wed),
        stringResource(R.string.day_thu),
        stringResource(R.string.day_fri),
        stringResource(R.string.day_sat),
        stringResource(R.string.day_sun)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (0..6).forEach { offset ->
            val date = weekStart.plusDays(offset.toLong())
            val mood = moodsByDate[date]?.firstOrNull()?.mood
            val isToday = date == today
            val dayIndex = (date.dayOfWeek.value - 1) % 7

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = dayAbbreviations[dayIndex],
                    fontSize = 10.sp,
                    color = if (isToday) AuroraGreen else SoftWhite.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isToday) AuroraGreen.copy(alpha = 0.2f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mood?.emoji ?: date.dayOfMonth.toString(),
                        fontSize = if (mood != null) 14.sp else 11.sp,
                        color = if (isToday) AuroraGreen else SoftWhite.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = SoftWhite.copy(alpha = 0.5f)
        )
    }
}
