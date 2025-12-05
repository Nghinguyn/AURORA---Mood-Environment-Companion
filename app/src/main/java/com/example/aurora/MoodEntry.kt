package com.example.aurora

data class MoodEntry(
    val mood: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
