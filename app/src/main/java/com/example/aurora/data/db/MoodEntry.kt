package com.example.aurora.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.aurora.data.model.Mood
import java.time.LocalDate

@Entity(
    tableName = "mood_entries",
    foreignKeys = [
        ForeignKey(
            entity = JournalEntry::class,
            parentColumns = ["id"],
            childColumns = ["journalEntryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("date"),
        Index("journalEntryId")
    ]
)
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mood: Mood,
    val date: LocalDate,
    val journalEntryId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
