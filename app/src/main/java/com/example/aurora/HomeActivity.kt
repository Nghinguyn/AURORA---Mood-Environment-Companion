package com.example.aurora

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.aurora.data.CalendarManager
import com.example.aurora.data.JournalManager
import com.example.aurora.data.db.AppDatabase
import com.example.aurora.data.db.LocationDao
import com.example.aurora.location.LocationTrackingService
import com.example.aurora.location.LocationTrackingService.Companion.ACTION_OPEN_JOURNAL
import com.example.aurora.data.db.MoodDao
import com.example.aurora.data.InsightsRepository
import com.example.aurora.data.SettingsRepository
import com.example.aurora.screens.CalendarScreen
import com.example.aurora.screens.InsightsScreen
import com.example.aurora.screens.DashboardScreen
import com.example.aurora.screens.JournalDetailScreen
import com.example.aurora.screens.JournalEntryScreen
import com.example.aurora.screens.SettingsScreen
import com.example.aurora.ui.theme.AuroraTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import javax.inject.Inject

sealed class Screen {
    object Dashboard : Screen()
    object JournalEntry : Screen()
    object Calendar : Screen()
    object Settings : Screen()
    object Insights : Screen()
    data class JournalDetail(val journalId: Long) : Screen()
}

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var journalManager: JournalManager

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var locationDao: LocationDao

    @Inject
    lateinit var moodDao: MoodDao

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var insightsRepository: InsightsRepository

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            requestBackgroundLocationPermission()
        }
    }

    private val backgroundLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startLocationTracking()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestLocationPermissions()

        val openJournal = intent?.action == ACTION_OPEN_JOURNAL

        setContent {
            AuroraTheme(darkTheme = true) {
                var currentScreen by remember { 
                    mutableStateOf<Screen>(if (openJournal) Screen.JournalEntry else Screen.Dashboard) 
                }
                var previousScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

                val today = LocalDate.now()
                val weekAgo = today.minusDays(7)

                val todayMoods by database.moodDao().observeMoodsForDate(today)
                    .collectAsState(initial = emptyList())

                val recentMoods by database.moodDao().observeMoodsInRange(weekAgo, today)
                    .collectAsState(initial = emptyList())

                val todayJournals by database.journalDao().observeEntriesForDate(today)
                    .collectAsState(initial = emptyList())

                val recentJournals by database.journalDao().observeEntriesInRange(weekAgo, today)
                    .collectAsState(initial = emptyList())

                val streak = calculateStreak(recentJournals.map { it.date }.distinct().sorted())
                val currentMood = todayMoods.maxByOrNull { it.createdAt }?.mood

                val hasApiKey by settingsRepository.googleAiApiKey
                    .collectAsState(initial = null)

                when (val screen = currentScreen) {
                    is Screen.Dashboard -> DashboardScreen(
                        recentMoods = recentMoods,
                        totalEntries = recentJournals.size,
                        currentStreak = streak,
                        currentMood = currentMood,
                        todayEntryCount = todayJournals.size,
                        hasApiKey = !hasApiKey.isNullOrBlank(),
                        insightsRepository = insightsRepository,
                        onAddJournalClick = { currentScreen = Screen.JournalEntry },
                        onCalendarClick = { currentScreen = Screen.Calendar },
                        onSettingsClick = { currentScreen = Screen.Settings },
                        onInsightsClick = { currentScreen = Screen.Insights }
                    )
                    is Screen.JournalEntry -> JournalEntryScreen(
                        journalManager = journalManager,
                        locationDao = locationDao,
                        onBackClick = { currentScreen = Screen.Dashboard },
                        onSaveSuccess = { currentScreen = Screen.Dashboard }
                    )
                    is Screen.Calendar -> CalendarScreen(
                        calendarManager = calendarManager,
                        onBackClick = { currentScreen = Screen.Dashboard },
                        onJournalClick = { journalId ->
                            previousScreen = Screen.Calendar
                            currentScreen = Screen.JournalDetail(journalId)
                        }
                    )
                    is Screen.JournalDetail -> JournalDetailScreen(
                        journalId = screen.journalId,
                        journalManager = journalManager,
                        moodDao = moodDao,
                        locationDao = locationDao,
                        onBackClick = { currentScreen = previousScreen },
                        onDeleted = { currentScreen = previousScreen }
                    )
                    is Screen.Settings -> SettingsScreen(
                        settingsRepository = settingsRepository,
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                    is Screen.Insights -> InsightsScreen(
                        insightsRepository = insightsRepository,
                        onBackClick = { currentScreen = Screen.Dashboard }
                    )
                }
            }
        }
    }

    private fun checkAndRequestLocationPermissions() {
        when {
            hasLocationPermissions() -> {
                if (hasBackgroundLocationPermission()) {
                    startLocationTracking()
                } else {
                    requestBackgroundLocationPermission()
                }
            }
            else -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            startLocationTracking()
        }
    }

    private fun startLocationTracking() {
        LocationTrackingService.start(this)
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
