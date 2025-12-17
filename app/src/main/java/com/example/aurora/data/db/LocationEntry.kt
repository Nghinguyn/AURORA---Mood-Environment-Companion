package com.example.aurora.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "location_entries",
    indices = [Index("date"), Index("timestamp")]
)
data class LocationEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val placeName: String? = null,
    val address: String? = null,
    val date: LocalDate = LocalDate.now(),
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 0
)
