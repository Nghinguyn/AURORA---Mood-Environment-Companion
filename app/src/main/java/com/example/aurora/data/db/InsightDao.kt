package com.example.aurora.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InsightDao {
    @Query("SELECT * FROM insight_entries WHERE id = 1")
    suspend fun getInsight(): InsightEntry?

    @Query("SELECT * FROM insight_entries WHERE id = 1")
    fun observeInsight(): Flow<InsightEntry?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(insight: InsightEntry)

    @Query("DELETE FROM insight_entries")
    suspend fun clearInsights()

    @Query("SELECT dataHash FROM insight_entries WHERE id = 1")
    suspend fun getDataHash(): String?
}
