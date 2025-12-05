package com.example.aurora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReflectionActivity : AppCompatActivity() {

    private lateinit var reflectionMessage: TextView
    private lateinit var shareButton: Button
    private lateinit var viewJourneyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reflection)

        reflectionMessage = findViewById(R.id.reflectionMessage)
        shareButton = findViewById(R.id.shareButton)
        viewJourneyButton = findViewById(R.id.viewJourneyButton)

        // Get mood text from previous screen
        val mood = intent.getStringExtra("mood_text") ?: "Unknown mood"
        reflectionMessage.text = "You feel: $mood\nKeep shining! 🌟"

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, reflectionMessage.text)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        viewJourneyButton.setOnClickListener {
            startActivity(Intent(this, JourneyActivity::class.java))
        }
    }
}
