package com.example.aurora
import java.time.LocalDate
data class MoodEntry(
    val mood: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val date: LocalDate = LocalDate.now())
