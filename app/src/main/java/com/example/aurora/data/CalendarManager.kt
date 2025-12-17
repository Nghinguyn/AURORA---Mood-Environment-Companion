package com.example.aurora.data

import com.example.aurora.data.db.JournalDao
import com.example.aurora.data.db.LocationDao
import com.example.aurora.data.db.MoodDao
import com.example.aurora.data.db.MoodEntry
import com.example.aurora.data.model.DailySummary
import com.example.aurora.data.model.MonthSummary
import com.example.aurora.data.model.Mood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.YearMonth

class CalendarManager(
    private val journalDao: JournalDao,
    private val moodDao: MoodDao,
    private val locationDao: LocationDao
) {
    suspend fun getMonthSummary(year: Int, month: Int): MonthSummary {
        val yearMonth = YearMonth.of(year, month)
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()

        val journals = journalDao.getEntriesInRange(startDate, endDate)
        val moods = moodDao.getMoodsInRange(startDate, endDate)
        val locations = locationDao.getLocationsInRange(startDate, endDate)

        val journalsByDate = journals.groupBy { it.date }
        val moodsByDate = moods.groupBy { it.date }
        val locationsByDate = locations.groupBy { it.date }

        val allDates = (journalsByDate.keys + moodsByDate.keys + locationsByDate.keys).distinct()

        val dailySummaries = allDates.associateWith { date ->
            DailySummary(
                date = date,
                moods = moodsByDate[date] ?: emptyList(),
                journalEntries = journalsByDate[date] ?: emptyList(),
                locations = locationsByDate[date] ?: emptyList()
            )
        }

        return MonthSummary(year, month, dailySummaries)
    }

    fun observeMonthSummary(year: Int, month: Int): Flow<MonthSummary> {
        val yearMonth = YearMonth.of(year, month)
        val startDate = yearMonth.atDay(1)
        val endDate = yearMonth.atEndOfMonth()

        return combine(
            journalDao.observeEntriesInRange(startDate, endDate),
            moodDao.observeMoodsInRange(startDate, endDate),
            locationDao.observeLocationsInRange(startDate, endDate)
        ) { journals, moods, locations ->
            val journalsByDate = journals.groupBy { it.date }
            val moodsByDate = moods.groupBy { it.date }
            val locationsByDate = locations.groupBy { it.date }
            val allDates = (journalsByDate.keys + moodsByDate.keys + locationsByDate.keys).distinct()

            val dailySummaries = allDates.associateWith { date ->
                DailySummary(
                    date = date,
                    moods = moodsByDate[date] ?: emptyList(),
                    journalEntries = journalsByDate[date] ?: emptyList(),
                    locations = locationsByDate[date] ?: emptyList()
                )
            }

            MonthSummary(year, month, dailySummaries)
        }
    }

    suspend fun getDailySummary(date: LocalDate): DailySummary {
        val journals = journalDao.getEntriesForDate(date)
        val moods = moodDao.getMoodsForDate(date)

        return DailySummary(
            date = date,
            moods = moods,
            journalEntries = journals
        )
    }

    fun observeDailySummary(date: LocalDate): Flow<DailySummary> {
        return combine(
            journalDao.observeEntriesForDate(date),
            moodDao.observeMoodsForDate(date)
        ) { journals, moods ->
            DailySummary(
                date = date,
                moods = moods,
                journalEntries = journals
            )
        }
    }

    suspend fun logMood(
        mood: Mood,
        date: LocalDate = LocalDate.now(),
        journalEntryId: Long? = null
    ): Long {
        val moodEntry = MoodEntry(
            mood = mood,
            date = date,
            journalEntryId = journalEntryId
        )
        return moodDao.insert(moodEntry)
    }

    suspend fun logMoods(
        moods: List<Mood>,
        date: LocalDate = LocalDate.now(),
        journalEntryId: Long? = null
    ) {
        val moodEntries = moods.distinct().map { mood ->
            MoodEntry(
                mood = mood,
                date = date,
                journalEntryId = journalEntryId
            )
        }
        moodDao.insertAll(moodEntries)
    }

    suspend fun deleteMood(moodEntryId: Long) {
        moodDao.deleteById(moodEntryId)
    }

    suspend fun getMoodsForJournal(journalEntryId: Long): List<MoodEntry> {
        return moodDao.getMoodsForJournal(journalEntryId)
    }
}
