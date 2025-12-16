package com.example.aurora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurora.data.db.MoodEntry
import com.example.aurora.data.model.Mood
import com.example.aurora.ui.theme.AuroraGreen
import com.example.aurora.ui.theme.LemonYellow
import com.example.aurora.ui.theme.SoftWhite
import com.example.aurora.ui.theme.VioletGlow
import kotlin.random.Random

@Composable
fun InsightsCard(
    recentMoods: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    val tip = remember(recentMoods) { getTip(recentMoods) }
    val quote = remember { getQuote() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2d1b4e))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "✨ Insights",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SoftWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(VioletGlow.copy(alpha = 0.15f))
                    .padding(12.dp)
            ) {
                Text(text = tip.first, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = tip.second,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SoftWhite
                    )
                    Text(
                        text = tip.third,
                        fontSize = 12.sp,
                        color = SoftWhite.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "\"${quote.first}\"",
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = SoftWhite.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "— ${quote.second}",
                        fontSize = 11.sp,
                        color = VioletGlow.copy(alpha = 0.7f),
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (recentMoods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                MoodBalance(recentMoods)
            }
        }
    }
}

@Composable
private fun MoodBalance(moods: List<MoodEntry>) {
    val positive = moods.count { it.mood in listOf(Mood.HAPPY, Mood.CALM, Mood.EXCITED, Mood.GRATEFUL, Mood.HOPEFUL) }
    val ratio = positive.toFloat() / moods.size

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Mood Balance", fontSize = 12.sp, color = SoftWhite.copy(alpha = 0.5f))
            Text(
                "${(ratio * 100).toInt()}% positive",
                fontSize = 12.sp,
                color = if (ratio >= 0.5f) AuroraGreen else LemonYellow
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(ratio)
                    .background(if (ratio >= 0.5f) AuroraGreen else LemonYellow)
            )
        }
    }
}

private fun getTip(moods: List<MoodEntry>): Triple<String, String, String> {
    if (moods.isEmpty()) {
        return Triple("💧", "Stay Hydrated", "Water affects mood more than you think!")
    }
    
    val top = moods.groupBy { it.mood }.maxByOrNull { it.value.size }?.key
    return when (top) {
        Mood.HAPPY -> Triple("🌟", "Riding High", "Keep doing what works!")
        Mood.SAD -> Triple("💙", "Be Gentle", "It's okay to feel down.")
        Mood.ANXIOUS -> Triple("🧘", "Ground Yourself", "Try deep breathing.")
        Mood.CALM -> Triple("🌊", "Inner Peace", "Your calm is your strength.")
        Mood.ANGRY -> Triple("🔥", "Channel It", "Use that energy wisely.")
        Mood.EXCITED -> Triple("⚡", "Harness It", "Great time for creativity!")
        Mood.TIRED -> Triple("😴", "Rest Up", "Recovery time is needed.")
        Mood.GRATEFUL -> Triple("🙏", "Beautiful", "Gratitude attracts more.")
        Mood.CONFUSED -> Triple("🧭", "Seek Clarity", "Write it out.")
        Mood.HOPEFUL -> Triple("🌈", "Keep Going", "Trust the process.")
        else -> Triple("💡", "Tip", "Track your mood daily!")
    }
}

private fun getQuote(): Pair<String, String> {
    val quotes = listOf(
        "The only way out is through" to "Robert Frost",
        "You are the sky. Everything else is just weather" to "Pema Chödrön",
        "What we think, we become" to "Buddha",
        "Be yourself; everyone else is taken" to "Oscar Wilde"
    )
    return quotes[Random(System.currentTimeMillis() / 86400000).nextInt(quotes.size)]
}
