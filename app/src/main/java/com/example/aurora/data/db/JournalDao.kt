package com.example.aurora.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): JournalEntry?

    @Query("SELECT * FROM journal_entries WHERE date = :date ORDER BY createdAt DESC")
    suspend fun getEntriesForDate(date: LocalDate): List<JournalEntry>

    @Query("SELECT * FROM journal_entries WHERE date = :date ORDER BY createdAt DESC")
    fun observeEntriesForDate(date: LocalDate): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, createdAt DESC")
    suspend fun getEntriesInRange(startDate: LocalDate, endDate: LocalDate): List<JournalEntry>

    @Query("SELECT * FROM journal_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, createdAt DESC")
    fun observeEntriesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<JournalEntry>>

    @Insert
    suspend fun insertEntry(entry: JournalEntry): Long

    @Update
    suspend fun updateEntry(entry: JournalEntry)

    @Delete
    suspend fun deleteEntry(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)
}
