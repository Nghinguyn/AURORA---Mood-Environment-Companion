package com.example.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

class JourneyActivity : ComponentActivity() {

    private val moodEntries = listOf(
        MoodEntry("Happy 🌞", "Had a great day at work, feeling positive and energetic!"),
        MoodEntry("Calm", "Relaxed and peaceful day, spent time outdoors."),
        MoodEntry("Excited ✨", "Excited about the upcoming weekend plans!")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    JourneyScreen(moodEntries = moodEntries)
                }
            }
        }
    }
}
