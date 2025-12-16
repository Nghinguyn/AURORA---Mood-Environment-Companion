package com.example.aurora.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
class JournalDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var journalDao: JournalDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        journalDao = database.journalDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertEntry_returnsId() = runTest {
        val entry = JournalEntry(title = "Test", content = "Content")
        val id = journalDao.insertEntry(entry)
        assertTrue(id > 0)
    }

    @Test
    fun getEntryById_returnsEntry() = runTest {
        val entry = JournalEntry(title = "Test", content = "# My Journal\n\nFeeling good today!")
        val id = journalDao.insertEntry(entry)

        val retrieved = journalDao.getEntryById(id)

        assertNotNull(retrieved)
        assertEquals("Test", retrieved!!.title)
    }

    @Test
    fun getAllEntries_returnsAllEntries() = runTest {
        journalDao.insertEntry(JournalEntry(title = "Entry 1", content = "Content 1"))
        journalDao.insertEntry(JournalEntry(title = "Entry 2", content = "Content 2"))

        val entries = journalDao.getAllEntries().first()

        assertEquals(2, entries.size)
    }

    @Test
    fun updateEntry_updatesFields() = runTest {
        val entry = JournalEntry(title = "Test", content = "Content")
        val id = journalDao.insertEntry(entry)

        val updatedEntry = entry.copy(id = id, title = "Updated Title")
        journalDao.updateEntry(updatedEntry)

        val retrieved = journalDao.getEntryById(id)

        assertNotNull(retrieved)
        assertEquals("Updated Title", retrieved!!.title)
    }

    @Test
    fun deleteEntryById_removesEntry() = runTest {
        val entry = JournalEntry(title = "Test", content = "Content")
        val id = journalDao.insertEntry(entry)

        journalDao.deleteEntryById(id)

        val retrieved = journalDao.getEntryById(id)
        assertNull(retrieved)
    }

    @Test
    fun getEntriesForDate_returnsOnlyMatchingDate() = runTest {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        journalDao.insertEntry(JournalEntry(title = "Today", content = "Content", date = today))
        journalDao.insertEntry(JournalEntry(title = "Yesterday", content = "Content", date = yesterday))

        val todayEntries = journalDao.getEntriesForDate(today)

        assertEquals(1, todayEntries.size)
        assertEquals("Today", todayEntries.first().title)
    }

    @Test
    fun getEntriesInRange_returnsEntriesInDateRange() = runTest {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

        journalDao.insertEntry(JournalEntry(title = "In Range", content = "Content", date = today))
        journalDao.insertEntry(JournalEntry(title = "Out of Range", content = "Content", date = startOfMonth.minusDays(1)))

        val entries = journalDao.getEntriesInRange(startOfMonth, endOfMonth)

        assertEquals(1, entries.size)
        assertEquals("In Range", entries.first().title)
    }
}
