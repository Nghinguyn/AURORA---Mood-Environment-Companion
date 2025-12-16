package com.example.aurora.data.db

import androidx.room.TypeConverter
import com.example.aurora.data.model.Mood
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromMood(mood: Mood): String = mood.name

    @TypeConverter
    fun toMood(value: String): Mood = Mood.valueOf(value)

    @TypeConverter
    fun fromLocalDate(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun toLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)
}
