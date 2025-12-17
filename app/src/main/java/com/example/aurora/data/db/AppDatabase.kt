package com.example.aurora.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [JournalEntry::class, MoodEntry::class, LocationEntry::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
    abstract fun moodDao(): MoodDao
    abstract fun locationDao(): LocationDao

    companion object {
        const val DATABASE_NAME = "aurora_db"
    }
}
