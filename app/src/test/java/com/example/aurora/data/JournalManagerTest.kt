package com.example.aurora.data

import com.example.aurora.data.db.JournalDao
import com.example.aurora.data.db.JournalEntry
import com.example.aurora.data.db.MoodDao
import com.example.aurora.data.db.MoodEntry
import com.example.aurora.data.model.Mood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class JournalManagerTest {

    private lateinit var journalManager: JournalManager
    private lateinit var fakeJournalDao: FakeJournalDao
    private lateinit var fakeMoodDao: FakeMoodDao

    @Before
    fun setup() {
        fakeJournalDao = FakeJournalDao()
        fakeMoodDao = FakeMoodDao()
        journalManager = JournalManager(fakeJournalDao, fakeMoodDao)
    }

    @Test
    fun `createEntry with empty moods returns error`() = runTest {
        val result = journalManager.createEntry("Title", "Content", emptyList())

        assertTrue(result is JournalResult.Error)
        assertEquals("Please select at least one mood", (result as JournalResult.Error).message)
    }

    @Test
    fun `createEntry with blank title returns error`() = runTest {
        val result = journalManager.createEntry("   ", "Content", listOf(Mood.HAPPY))

        assertTrue(result is JournalResult.Error)
        assertEquals("Title is required", (result as JournalResult.Error).message)
    }

    @Test
    fun `createEntry with title exceeding max length returns error`() = runTest {
        val longTitle = "a".repeat(JournalManager.MAX_TITLE_LENGTH + 1)
        val result = journalManager.createEntry(longTitle, "Content", listOf(Mood.HAPPY))

        assertTrue(result is JournalResult.Error)
        assertTrue((result as JournalResult.Error).message.contains("200 characters"))
    }

    @Test
    fun `createEntry with valid input returns success`() = runTest {
        val result = journalManager.createEntry("My Journal", "# Today\nContent", listOf(Mood.HAPPY))

        assertTrue(result is JournalResult.Success)
        assertTrue((result as JournalResult.Success).data > 0)
    }

    @Test
    fun `createEntry trims title and content`() = runTest {
        journalManager.createEntry("  My Title  ", "  Content  ", listOf(Mood.HAPPY))

        val savedEntry = fakeJournalDao.lastSavedEntry
        assertEquals("My Title", savedEntry?.title)
        assertEquals("Content", savedEntry?.content)
    }

    @Test
    fun `createEntry removes duplicate moods`() = runTest {
        journalManager.createEntry("Title", "Content", listOf(Mood.HAPPY, Mood.HAPPY, Mood.SAD))

        val savedMoods = fakeMoodDao.lastSavedMoods
        assertEquals(2, savedMoods?.size)
    }

    @Test
    fun `createEntry associates moods with journal entry`() = runTest {
        val result = journalManager.createEntry("Title", "Content", listOf(Mood.HAPPY, Mood.CALM))

        assertTrue(result is JournalResult.Success)
        val entryId = (result as JournalResult.Success).data
        val savedMoods = fakeMoodDao.lastSavedMoods
        assertTrue(savedMoods?.all { it.journalEntryId == entryId } == true)
    }

    @Test
    fun `updateEntry for non-existent entry returns error`() = runTest {
        val result = journalManager.updateEntry(999, "Title", "Content", listOf(Mood.HAPPY))

        assertTrue(result is JournalResult.Error)
        assertEquals("Journal entry not found", (result as JournalResult.Error).message)
    }

    @Test
    fun `updateEntry with empty moods returns error`() = runTest {
        fakeJournalDao.entries[1] = JournalEntry(id = 1, title = "Title", content = "Content")

        val result = journalManager.updateEntry(1, "New Title", "New Content", emptyList())

        assertTrue(result is JournalResult.Error)
        assertEquals("Please select at least one mood", (result as JournalResult.Error).message)
    }

    @Test
    fun `updateEntry with valid input returns success`() = runTest {
        fakeJournalDao.entries[1] = JournalEntry(id = 1, title = "Title", content = "Content")

        val result = journalManager.updateEntry(1, "Updated", "New Content", listOf(Mood.CALM))

        assertTrue(result is JournalResult.Success)
    }

    @Test
    fun `deleteEntry for non-existent entry returns error`() = runTest {
        val result = journalManager.deleteEntry(999)

        assertTrue(result is JournalResult.Error)
        assertEquals("Journal entry not found", (result as JournalResult.Error).message)
    }

    @Test
    fun `deleteEntry for existing entry returns success`() = runTest {
        fakeJournalDao.entries[1] = JournalEntry(id = 1, title = "Title", content = "Content")

        val result = journalManager.deleteEntry(1)

        assertTrue(result is JournalResult.Success)
        assertTrue(fakeJournalDao.entries.isEmpty())
    }

    @Test
    fun `getEntryById for non-existent entry returns error`() = runTest {
        val result = journalManager.getEntryById(999)

        assertTrue(result is JournalResult.Error)
    }

    @Test
    fun `getEntryById for existing entry returns success`() = runTest {
        val entry = JournalEntry(id = 1, title = "Title", content = "Content")
        fakeJournalDao.entries[1] = entry

        val result = journalManager.getEntryById(1)

        assertTrue(result is JournalResult.Success)
        assertEquals("Title", (result as JournalResult.Success).data.title)
    }

    @Test
    fun `getEntryWithMoods returns entry and associated moods`() = runTest {
        val entry = JournalEntry(id = 1, title = "Title", content = "Content")
        fakeJournalDao.entries[1] = entry
        fakeMoodDao.moodsByJournal[1] = listOf(
            MoodEntry(id = 1, mood = Mood.HAPPY, date = LocalDate.now(), journalEntryId = 1)
        )

        val result = journalManager.getEntryWithMoods(1)

        assertTrue(result is JournalResult.Success)
        val data = (result as JournalResult.Success).data
        assertEquals("Title", data.entry.title)
        assertEquals(1, data.moods.size)
        assertEquals(Mood.HAPPY, data.moods.first().mood)
    }

    @Test
    fun `createEntry with content exceeding max length returns error`() = runTest {
        val longContent = "a".repeat(JournalManager.MAX_CONTENT_LENGTH + 1)
        val result = journalManager.createEntry("Title", longContent, listOf(Mood.HAPPY))

        assertTrue(result is JournalResult.Error)
        assertTrue((result as JournalResult.Error).message.contains("maximum length"))
    }
}

private class FakeJournalDao : JournalDao {
    val entries = mutableMapOf<Long, JournalEntry>()
    var lastSavedEntry: JournalEntry? = null
    private var nextId = 1L

    override fun getAllEntries(): Flow<List<JournalEntry>> = flowOf(entries.values.toList())

    override suspend fun getEntryById(id: Long): JournalEntry? = entries[id]

    override suspend fun getEntriesForDate(date: LocalDate): List<JournalEntry> =
        entries.values.filter { it.date == date }

    override fun observeEntriesForDate(date: LocalDate): Flow<List<JournalEntry>> =
        flowOf(entries.values.filter { it.date == date })

    override suspend fun getEntriesInRange(startDate: LocalDate, endDate: LocalDate): List<JournalEntry> =
        entries.values.filter { it.date in startDate..endDate }

    override fun observeEntriesInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<JournalEntry>> =
        flowOf(entries.values.filter { it.date in startDate..endDate })

    override suspend fun insertEntry(entry: JournalEntry): Long {
        val id = nextId++
        entries[id] = entry.copy(id = id)
        lastSavedEntry = entries[id]
        return id
    }

    override suspend fun updateEntry(entry: JournalEntry) {
        entries[entry.id] = entry
    }

    override suspend fun deleteEntry(entry: JournalEntry) {
        entries.remove(entry.id)
    }

    override suspend fun deleteEntryById(id: Long) {
        entries.remove(id)
    }
}

private class FakeMoodDao : MoodDao {
    val moodsByDate = mutableMapOf<LocalDate, MutableList<MoodEntry>>()
    val moodsByJournal = mutableMapOf<Long, List<MoodEntry>>()
    var lastSavedMoods: List<MoodEntry>? = null
    private var nextId = 1L

    override suspend fun insert(moodEntry: MoodEntry): Long {
        val id = nextId++
        val entry = moodEntry.copy(id = id)
        moodsByDate.getOrPut(entry.date) { mutableListOf() }.add(entry)
        lastSavedMoods = listOf(entry)
        return id
    }

    override suspend fun insertAll(moodEntries: List<MoodEntry>) {
        val savedEntries = moodEntries.map { entry ->
            val id = nextId++
            entry.copy(id = id)
        }
        savedEntries.forEach { entry ->
            moodsByDate.getOrPut(entry.date) { mutableListOf() }.add(entry)
        }
        lastSavedMoods = savedEntries
    }

    override suspend fun delete(moodEntry: MoodEntry) {
        moodsByDate[moodEntry.date]?.removeIf { it.id == moodEntry.id }
    }

    override suspend fun deleteById(id: Long) {}

    override suspend fun deleteByJournalEntryId(journalEntryId: Long) {
        moodsByJournal.remove(journalEntryId)
        moodsByDate.values.forEach { list ->
            list.removeIf { it.journalEntryId == journalEntryId }
        }
    }

    override suspend fun getById(id: Long): MoodEntry? = null

    override suspend fun getMoodsForDate(date: LocalDate): List<MoodEntry> =
        moodsByDate[date] ?: emptyList()

    override fun observeMoodsForDate(date: LocalDate): Flow<List<MoodEntry>> =
        flowOf(moodsByDate[date] ?: emptyList())

    override suspend fun getMoodsForJournal(journalEntryId: Long): List<MoodEntry> =
        moodsByJournal[journalEntryId] ?: emptyList()

    override suspend fun getMoodsInRange(startDate: LocalDate, endDate: LocalDate): List<MoodEntry> =
        moodsByDate.filterKeys { it in startDate..endDate }.values.flatten()

    override fun observeMoodsInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<MoodEntry>> =
        flowOf(moodsByDate.filterKeys { it in startDate..endDate }.values.flatten())

    override suspend fun getDatesWithMoodsInRange(startDate: LocalDate, endDate: LocalDate): List<LocalDate> =
        moodsByDate.keys.filter { it in startDate..endDate }
}
