package com.example.aurora.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aurora.R
import com.example.aurora.data.InsightResult
import com.example.aurora.data.InsightsRepository
import com.example.aurora.data.db.MoodEntry
import com.example.aurora.data.model.Mood
import com.example.aurora.ui.theme.AuroraGreen
import com.example.aurora.ui.theme.LemonYellow
import com.example.aurora.ui.theme.SoftWhite
import com.example.aurora.ui.theme.VioletGlow
import kotlin.random.Random

data class Quote(val textResId: Int, val authorResId: Int)

private val quotes = listOf(
    Quote(R.string.quote_1, R.string.quote_1_author),
    Quote(R.string.quote_2, R.string.quote_2_author),
    Quote(R.string.quote_3, R.string.quote_3_author),
    Quote(R.string.quote_4, R.string.quote_4_author)
)

data class MoodTip(val emoji: String, val titleResId: Int, val descResId: Int)

@Composable
fun InsightsCard(
    insightsRepository: InsightsRepository,
    recentMoods: List<MoodEntry>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var insightResult by remember { mutableStateOf<InsightResult?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        insightResult = insightsRepository.generateInsights(forceRefresh = false)
        isLoading = false
    }

    val quoteIndex = remember { Random(System.currentTimeMillis() / 86400000).nextInt(quotes.size) }
    val quote = quotes[quoteIndex]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2d1b4e))
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
                    text = stringResource(R.string.insights_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SoftWhite
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = stringResource(R.string.open),
                    tint = SoftWhite.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = VioletGlow,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            } else {
                insightResult?.let { result ->
                    if (result.summary.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(VioletGlow.copy(alpha = 0.15f))
                                .padding(12.dp)
                        ) {
                            Text(text = "💡", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.ai_insight),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SoftWhite
                                )
                                Text(
                                    text = result.summary,
                                    fontSize = 12.sp,
                                    color = SoftWhite.copy(alpha = 0.8f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    } else if (result.error != null) {
                        TipRow(recentMoods)
                    }
                } ?: TipRow(recentMoods)
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
                        text = "\"${stringResource(quote.textResId)}\"",
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = SoftWhite.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "— ${stringResource(quote.authorResId)}",
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.tap_for_full_report),
                fontSize = 11.sp,
                color = VioletGlow.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun TipRow(moods: List<MoodEntry>) {
    val tip = getTip(moods)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VioletGlow.copy(alpha = 0.15f))
            .padding(12.dp)
    ) {
        Text(text = tip.emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = stringResource(tip.titleResId),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftWhite
            )
            Text(
                text = stringResource(tip.descResId),
                fontSize = 12.sp,
                color = SoftWhite.copy(alpha = 0.6f)
            )
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
            Text(
                stringResource(R.string.mood_balance),
                fontSize = 12.sp,
                color = SoftWhite.copy(alpha = 0.5f)
            )
            Text(
                stringResource(R.string.positive_percent, (ratio * 100).toInt()),
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

private fun getTip(moods: List<MoodEntry>): MoodTip {
    if (moods.isEmpty()) {
        return MoodTip("💧", R.string.tip_hydration_title, R.string.tip_hydration_desc)
    }
    
    val top = moods.groupBy { it.mood }.maxByOrNull { it.value.size }?.key
    return when (top) {
        Mood.HAPPY -> MoodTip("🌟", R.string.tip_happy_title, R.string.tip_happy_desc)
        Mood.SAD -> MoodTip("💙", R.string.tip_sad_title, R.string.tip_sad_desc)
        Mood.ANXIOUS -> MoodTip("🧘", R.string.tip_anxious_title, R.string.tip_anxious_desc)
        Mood.CALM -> MoodTip("🌊", R.string.tip_calm_title, R.string.tip_calm_desc)
        Mood.ANGRY -> MoodTip("🔥", R.string.tip_angry_title, R.string.tip_angry_desc)
        Mood.EXCITED -> MoodTip("⚡", R.string.tip_excited_title, R.string.tip_excited_desc)
        Mood.TIRED -> MoodTip("😴", R.string.tip_tired_title, R.string.tip_tired_desc)
        Mood.GRATEFUL -> MoodTip("🙏", R.string.tip_grateful_title, R.string.tip_grateful_desc)
        Mood.CONFUSED -> MoodTip("🧭", R.string.tip_confused_title, R.string.tip_confused_desc)
        Mood.HOPEFUL -> MoodTip("🌈", R.string.tip_hopeful_title, R.string.tip_hopeful_desc)
        else -> MoodTip("💡", R.string.tip_default_title, R.string.tip_default_desc)
    }
}
