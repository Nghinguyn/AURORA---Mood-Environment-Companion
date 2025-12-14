package com.example.aurora.network

data class JournalRequest(
    val mood: String,        // User's mood (e.g., "Happy", "Sad")
    val description: String  // The user's journal description
)
