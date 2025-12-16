package com.example.aurora.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.aurora.data.model.Mood
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MoodDao {
    @Insert
    suspend fun insert(moodEntry: MoodEntry): Long

    @Insert
    suspend fun insertAll(moodEntries: List<MoodEntry>)

    @Delete
    suspend fun delete(moodEntry: MoodEntry)

    @Query("DELETE FROM mood_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM mood_entries WHERE journalEntryId = :journalEntryId")
    suspend fun deleteByJournalEntryId(journalEntryId: Long)

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getById(id: Long): MoodEntry?

    @Query("SELECT * FROM mood_entries WHERE date = :date ORDER BY createdAt DESC")
    suspend fun getMoodsForDate(date: LocalDate): List<MoodEntry>

    @Query("SELECT * FROM mood_entries WHERE date = :date ORDER BY createdAt DESC")
    fun observeMoodsForDate(date: LocalDate): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE journalEntryId = :journalEntryId")
    suspend fun getMoodsForJournal(journalEntryId: Long): List<MoodEntry>

    @Query("SELECT * FROM mood_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, createdAt DESC")
    suspend fun getMoodsInRange(startDate: LocalDate, endDate: LocalDate): List<MoodEntry>

    @Query("SELECT * FROM mood_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, createdAt DESC")
    fun observeMoodsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<MoodEntry>>

    @Query("SELECT DISTINCT date FROM mood_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getDatesWithMoodsInRange(startDate: LocalDate, endDate: LocalDate): List<LocalDate>
}
