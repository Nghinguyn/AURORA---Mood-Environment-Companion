package com.example.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.aurora.data.db.AppDatabase
import com.example.aurora.screens.CalendarScreen
import com.example.aurora.screens.DashboardScreen
import com.example.aurora.screens.JournalEntryScreen
import com.example.aurora.ui.theme.AuroraTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import javax.inject.Inject

enum class Screen {
    Dashboard, JournalEntry, Calendar
}

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AuroraTheme(darkTheme = true) {
                var currentScreen by remember { mutableStateOf(Screen.Dashboard) }

                val today = LocalDate.now()
                val weekAgo = today.minusDays(7)

                val todayMoods by database.moodDao().observeMoodsForDate(today)
                    .collectAsState(initial = emptyList())

                val recentMoods by database.moodDao().observeMoodsInRange(weekAgo, today)
                    .collectAsState(initial = emptyList())

                val streak = calculateStreak(recentMoods.map { it.date }.distinct().sorted())
                val currentMood = todayMoods.maxByOrNull { it.createdAt }?.mood

                when (currentScreen) {
                    Screen.Dashboard -> DashboardScreen(
                        recentMoods = recentMoods,
                        totalEntries = recentMoods.size,
                        currentStreak = streak,
                        currentMood = currentMood,
                        todayEntryCount = todayMoods.size,
                        onAddJournalClick = { currentScreen = Screen.JournalEntry },
                        onCalendarClick = { currentScreen = Screen.Calendar }
                    )
                    Screen.JournalEntry -> JournalEntryScreen(
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                    Screen.Calendar -> CalendarScreen(
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                }
            }
        }
    }

    private fun calculateStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        val today = LocalDate.now()
        var streak = 0
        var check = today
        
        while (dates.contains(check) || (streak == 0 && dates.contains(check.minusDays(1)))) {
            if (dates.contains(check)) streak++
            check = check.minusDays(1)
            if (check.isBefore(today.minusDays(30))) break
        }
        return streak
    }
}
