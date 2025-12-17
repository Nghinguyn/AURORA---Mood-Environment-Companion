package com.example.aurora.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aurora.data.db.MoodEntry
import com.example.aurora.data.model.Mood
import com.example.aurora.ui.components.InsightsCard
import com.example.aurora.ui.components.JournalEntryCard
import com.example.aurora.ui.components.JourneyCard
import com.example.aurora.ui.components.SkyVisualization
import com.example.aurora.ui.theme.DeepNightBlue
import com.example.aurora.ui.theme.VioletGlow

@Composable
fun DashboardScreen(
    recentMoods: List<MoodEntry> = emptyList(),
    totalEntries: Int = 0,
    currentStreak: Int = 0,
    currentMood: Mood? = null,
    todayEntryCount: Int = 0,
    hasApiKey: Boolean = false,
    onAddJournalClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNightBlue)
            .verticalScroll(rememberScrollState())
    ) {
        SkyVisualization(
            currentMood = currentMood,
            onSettingsClick = onSettingsClick
        )

        Divider(color = VioletGlow.copy(alpha = 0.3f), thickness = 1.dp)

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            JournalEntryCard(
                todayEntryCount = todayEntryCount,
                onClick = onAddJournalClick
            )

            JourneyCard(
                recentMoods = recentMoods,
                totalEntries = totalEntries,
                currentStreak = currentStreak,
                onClick = onCalendarClick
            )

            if (hasApiKey) {
                InsightsCard(recentMoods = recentMoods)
            }
        }
    }
}
