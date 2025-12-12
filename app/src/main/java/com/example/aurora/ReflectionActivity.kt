package com.example.aurora

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aurora.screens.ReflectionScreen

class ReflectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get mood text from previous screen
        val mood = intent.getStringExtra("mood_text") ?: "Unknown mood"
        val reflectionMessage = "You feel: $mood\nKeep shining! 🌟"

        // Set the content to use ReflectionScreen composable
        setContent {
            ReflectionScreen(
                reflectionMessage = reflectionMessage,
                onShare = { shareReflection(reflectionMessage) },
                onViewJourney = { navigateToJourney() }
            )
        }
    }

    private fun shareReflection(reflectionMessage: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, reflectionMessage)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun navigateToJourney() {
        startActivity(Intent(this, JourneyActivity::class.java))
    }
}
