package com.example.aurora.data.model

import com.example.aurora.data.db.JournalEntry
import com.example.aurora.data.db.MoodEntry
import java.time.LocalDate

data class DailySummary(
    val date: LocalDate,
    val moods: List<MoodEntry>,
    val journalEntries: List<JournalEntry>
) {
    val hasMoods: Boolean get() = moods.isNotEmpty()
    val hasJournals: Boolean get() = journalEntries.isNotEmpty()
    val isEmpty: Boolean get() = !hasMoods && !hasJournals
    
    val moodCounts: Map<Mood, Int> by lazy {
        moods.groupingBy { it.mood }.eachCount()
    }
    
    val dominantMood: Mood? by lazy {
        moodCounts.maxByOrNull { it.value }?.key
    }
}

data class MonthSummary(
    val year: Int,
    val month: Int,
    val dailySummaries: Map<LocalDate, DailySummary>
) {
    fun getSummaryForDay(day: Int): DailySummary? {
        val date = LocalDate.of(year, month, day)
        return dailySummaries[date]
    }
    
    val daysWithEntries: Set<Int> by lazy {
        dailySummaries.filter { !it.value.isEmpty }
            .keys
            .map { it.dayOfMonth }
            .toSet()
    }
}
