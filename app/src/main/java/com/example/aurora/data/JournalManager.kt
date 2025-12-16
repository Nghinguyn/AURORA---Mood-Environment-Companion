package com.example.aurora.data

import com.example.aurora.data.db.JournalDao
import com.example.aurora.data.db.JournalEntry
import com.example.aurora.data.db.MoodDao
import com.example.aurora.data.db.MoodEntry
import com.example.aurora.data.model.Mood
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

sealed class JournalResult<out T> {
    data class Success<T>(val data: T) : JournalResult<T>()
    data class Error(val message: String) : JournalResult<Nothing>()
}

data class JournalWithMoods(
    val entry: JournalEntry,
    val moods: List<MoodEntry>
)

class JournalManager(
    private val journalDao: JournalDao,
    private val moodDao: MoodDao
) {
    companion object {
        const val MIN_TITLE_LENGTH = 1
        const val MAX_TITLE_LENGTH = 200
        const val MAX_CONTENT_LENGTH = 50_000
    }

    fun getAllEntries(): Flow<List<JournalEntry>> {
        return journalDao.getAllEntries()
    }

    suspend fun getEntryById(id: Long): JournalResult<JournalEntry> {
        return when (val entry = journalDao.getEntryById(id)) {
            null -> JournalResult.Error("Journal entry not found")
            else -> JournalResult.Success(entry)
        }
    }

    suspend fun getEntryWithMoods(id: Long): JournalResult<JournalWithMoods> {
        val entry = journalDao.getEntryById(id)
            ?: return JournalResult.Error("Journal entry not found")
        val moods = moodDao.getMoodsForJournal(id)
        return JournalResult.Success(JournalWithMoods(entry, moods))
    }

    suspend fun createEntry(
        title: String,
        content: String,
        moods: List<Mood>,
        date: LocalDate = LocalDate.now()
    ): JournalResult<Long> {
        val validationError = validateEntry(title, content, moods)
        if (validationError != null) {
            return JournalResult.Error(validationError)
        }

        val entry = JournalEntry(
            title = title.trim(),
            content = content.trim(),
            date = date
        )
        val entryId = journalDao.insertEntry(entry)

        val moodEntries = moods.distinct().map { mood ->
            MoodEntry(
                mood = mood,
                date = date,
                journalEntryId = entryId
            )
        }
        moodDao.insertAll(moodEntries)

        return JournalResult.Success(entryId)
    }

    suspend fun updateEntry(
        id: Long,
        title: String,
        content: String,
        moods: List<Mood>
    ): JournalResult<Unit> {
        val existingEntry = journalDao.getEntryById(id)
            ?: return JournalResult.Error("Journal entry not found")

        val validationError = validateEntry(title, content, moods)
        if (validationError != null) {
            return JournalResult.Error(validationError)
        }

        val updatedEntry = existingEntry.copy(
            title = title.trim(),
            content = content.trim(),
            updatedAt = System.currentTimeMillis()
        )
        journalDao.updateEntry(updatedEntry)

        moodDao.deleteByJournalEntryId(id)
        val moodEntries = moods.distinct().map { mood ->
            MoodEntry(
                mood = mood,
                date = existingEntry.date,
                journalEntryId = id
            )
        }
        moodDao.insertAll(moodEntries)

        return JournalResult.Success(Unit)
    }

    suspend fun deleteEntry(id: Long): JournalResult<Unit> {
        val existingEntry = journalDao.getEntryById(id)
            ?: return JournalResult.Error("Journal entry not found")

        journalDao.deleteEntry(existingEntry)
        return JournalResult.Success(Unit)
    }

    private fun validateEntry(
        title: String,
        content: String,
        moods: List<Mood>
    ): String? {
        val trimmedTitle = title.trim()
        val trimmedContent = content.trim()

        return when {
            moods.isEmpty() -> "Please select at least one mood"
            trimmedTitle.length < MIN_TITLE_LENGTH -> "Title is required"
            trimmedTitle.length > MAX_TITLE_LENGTH -> "Title must be $MAX_TITLE_LENGTH characters or less"
            trimmedContent.length > MAX_CONTENT_LENGTH -> "Content exceeds maximum length"
            else -> null
        }
    }
}
