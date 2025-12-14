package com.example.aurora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.aurora.network.ApiResponse
import com.example.aurora.network.JournalRequest
import com.example.aurora.network.RetrofitInstance
import com.example.aurora.screens.JournalWriteScreen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JournalWriteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var description by remember { mutableStateOf("") }
            var mood by remember { mutableStateOf("Happy") }
            // Composable UI for journal input
            JournalWriteScreen(
                mood = mood,
                onSubmit = { enteredMood, enteredDescription ->
                    // Call the function to submit the journal entry to the backend
                    submitJournal(enteredMood, enteredDescription)
                }
            )
        }
    }
}
