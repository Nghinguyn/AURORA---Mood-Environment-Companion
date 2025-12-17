package com.example.aurora.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "insight_entries")
data class InsightEntry(
    @PrimaryKey
    val id: Long = 1,
    val summary: String,
    val fullReport: String,
    val dataHash: String,
    val generatedAt: Long = System.currentTimeMillis()
)
