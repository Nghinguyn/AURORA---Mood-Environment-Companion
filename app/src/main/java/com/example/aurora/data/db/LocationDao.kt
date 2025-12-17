package com.example.aurora.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface LocationDao {
    @Query("SELECT * FROM location_entries WHERE date = :date ORDER BY timestamp DESC")
    fun observeLocationsForDate(date: LocalDate): Flow<List<LocationEntry>>

    @Query("SELECT * FROM location_entries WHERE date = :date ORDER BY timestamp DESC")
    suspend fun getLocationsForDate(date: LocalDate): List<LocationEntry>

    @Query("SELECT * FROM location_entries WHERE timestamp >= :since ORDER BY timestamp DESC")
    suspend fun getLocationsSince(since: Long): List<LocationEntry>

    @Query("SELECT * FROM location_entries WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun observeLocationsSince(since: Long): Flow<List<LocationEntry>>

    @Query("SELECT * FROM location_entries ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLocations(limit: Int): List<LocationEntry>

    @Query("SELECT * FROM location_entries ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentLocations(limit: Int): Flow<List<LocationEntry>>

    @Query("SELECT * FROM location_entries WHERE placeName IS NOT NULL AND date = :date ORDER BY timestamp DESC")
    suspend fun getNamedLocationsForDate(date: LocalDate): List<LocationEntry>

    @Query("SELECT * FROM location_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    suspend fun getLocationsInRange(startDate: LocalDate, endDate: LocalDate): List<LocationEntry>

    @Query("SELECT * FROM location_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun observeLocationsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<LocationEntry>>

    @Insert
    suspend fun insert(entry: LocationEntry): Long

    @Update
    suspend fun update(entry: LocationEntry)

    @Query("DELETE FROM location_entries WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)

    @Query("SELECT * FROM location_entries WHERE id = (SELECT MAX(id) FROM location_entries)")
    suspend fun getLastLocation(): LocationEntry?
}
