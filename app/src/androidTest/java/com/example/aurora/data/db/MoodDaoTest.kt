package com.example.aurora.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.aurora.data.model.Mood
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class MoodDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var moodDao: MoodDao
    private lateinit var journalDao: JournalDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        moodDao = database.moodDao()
        journalDao = database.journalDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertMood_returnsId() = runTest {
        val moodEntry = MoodEntry(mood = Mood.HAPPY, date = LocalDate.now())
        val id = moodDao.insert(moodEntry)
        assertTrue(id > 0)
    }

    @Test
    fun getMoodsForDate_returnsOnlyMatchingDate() = runTest {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        moodDao.insert(MoodEntry(mood = Mood.HAPPY, date = today))
        moodDao.insert(MoodEntry(mood = Mood.SAD, date = yesterday))

        val todayMoods = moodDao.getMoodsForDate(today)

        assertEquals(1, todayMoods.size)
        assertEquals(Mood.HAPPY, todayMoods.first().mood)
    }

    @Test
    fun insertMultipleMoods_allPersisted() = runTest {
        val today = LocalDate.now()
        val moods = listOf(
            MoodEntry(mood = Mood.HAPPY, date = today),
            MoodEntry(mood = Mood.EXCITED, date = today),
            MoodEntry(mood = Mood.CALM, date = today)
        )

        moodDao.insertAll(moods)

        val retrieved = moodDao.getMoodsForDate(today)
        assertEquals(3, retrieved.size)
    }

    @Test
    fun getMoodsForJournal_returnsAssociatedMoods() = runTest {
        val journalEntry = JournalEntry(title = "Test", content = "Content")
        val journalId = journalDao.insertEntry(journalEntry)

        moodDao.insert(MoodEntry(mood = Mood.HAPPY, date = LocalDate.now(), journalEntryId = journalId))
        moodDao.insert(MoodEntry(mood = Mood.SAD, date = LocalDate.now(), journalEntryId = journalId))
        moodDao.insert(MoodEntry(mood = Mood.CALM, date = LocalDate.now(), journalEntryId = null))

        val journalMoods = moodDao.getMoodsForJournal(journalId)

        assertEquals(2, journalMoods.size)
        assertTrue(journalMoods.all { it.journalEntryId == journalId })
    }

    @Test
    fun deleteByJournalEntryId_removesOnlyAssociatedMoods() = runTest {
        val journalEntry = JournalEntry(title = "Test", content = "Content")
        val journalId = journalDao.insertEntry(journalEntry)
        val today = LocalDate.now()

        moodDao.insert(MoodEntry(mood = Mood.HAPPY, date = today, journalEntryId = journalId))
        moodDao.insert(MoodEntry(mood = Mood.CALM, date = today, journalEntryId = null))

        moodDao.deleteByJournalEntryId(journalId)

        val remaining = moodDao.getMoodsForDate(today)
        assertEquals(1, remaining.size)
        assertEquals(Mood.CALM, remaining.first().mood)
    }

    @Test
    fun getMoodsInRange_returnsEntriesInDateRange() = runTest {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

        moodDao.insert(MoodEntry(mood = Mood.HAPPY, date = today))
        moodDao.insert(MoodEntry(mood = Mood.SAD, date = startOfMonth.minusDays(1)))

        val moods = moodDao.getMoodsInRange(startOfMonth, endOfMonth)

        assertEquals(1, moods.size)
        assertEquals(Mood.HAPPY, moods.first().mood)
    }

    @Test
    fun cascadeDeleteRemovesMoodsWhenJournalDeleted() = runTest {
        val journalEntry = JournalEntry(title = "Test", content = "Content")
        val journalId = journalDao.insertEntry(journalEntry)
        val today = LocalDate.now()

        moodDao.insert(MoodEntry(mood = Mood.HAPPY, date = today, journalEntryId = journalId))
        moodDao.insert(MoodEntry(mood = Mood.SAD, date = today, journalEntryId = journalId))

        journalDao.deleteEntryById(journalId)

        val journalMoods = moodDao.getMoodsForJournal(journalId)
        assertEquals(0, journalMoods.size)
    }

    @Test
    fun observeMoodsForDate_emitsUpdates() = runTest {
        val today = LocalDate.now()

        moodDao.insert(MoodEntry(mood = Mood.HAPPY, date = today))

        val moods = moodDao.observeMoodsForDate(today).first()
        assertEquals(1, moods.size)
    }
}
