package com.example.aurora

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.aurora.screens.HomeScreen

class HomeActivity : ComponentActivity() {

    private val prefs by lazy { getSharedPreferences("AURA_PREFS", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var mood by remember { mutableStateOf("") }
            var showSettings by remember { mutableStateOf(false) }

            // HomeScreen UI
            HomeScreen(
                mood = mood,
                onMoodChange = { mood = it },
                onNextClick = { startActivity(Intent(this, JournalWriteActivity::class.java)) },
            )
        }
    }
}
