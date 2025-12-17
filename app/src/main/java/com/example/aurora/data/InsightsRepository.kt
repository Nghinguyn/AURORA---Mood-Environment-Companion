package com.example.aurora.data

import android.content.Context
import com.example.aurora.R
import com.example.aurora.data.db.InsightDao
import com.example.aurora.data.db.InsightEntry
import com.example.aurora.data.db.JournalDao
import com.example.aurora.data.db.JournalEntry
import com.example.aurora.data.db.LocationDao
import com.example.aurora.data.db.LocationEntry
import com.example.aurora.data.db.MoodDao
import com.example.aurora.data.db.MoodEntry
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.security.MessageDigest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

data class InsightResult(
    val summary: String,
    val fullReport: String,
    val isLoading: Boolean = false,
    val error: String? = null
)

@Singleton
class InsightsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val journalDao: JournalDao,
    private val moodDao: MoodDao,
    private val locationDao: LocationDao,
    private val insightDao: InsightDao,
    private val settingsRepository: SettingsRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun observeInsight(): Flow<InsightEntry?> = insightDao.observeInsight()

    suspend fun getCachedInsight(): InsightEntry? = insightDao.getInsight()

    suspend fun generateInsights(forceRefresh: Boolean = false): InsightResult {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = settingsRepository.getGoogleAiApiKey()
                if (apiKey.isNullOrBlank()) {
                    return@withContext InsightResult(
                        summary = "",
                        fullReport = "",
                        error = context.getString(R.string.error_api_key_not_configured)
                    )
                }

                val today = LocalDate.now()
                val thirtyDaysAgo = today.minusDays(30)

                val moods = moodDao.getMoodsInRange(thirtyDaysAgo, today)
                val journals = journalDao.getEntriesInRange(thirtyDaysAgo, today)
                val locations = locationDao.getLocationsInRange(thirtyDaysAgo, today)

                if (moods.isEmpty() && journals.isEmpty()) {
                    return@withContext InsightResult(
                        summary = context.getString(R.string.insights_getting_started_summary),
                        fullReport = context.getString(R.string.insights_getting_started_body),
                        error = null
                    )
                }

                val currentLanguage = settingsRepository.getAppLanguage()
                val currentDataHash = computeDataHash(moods, journals, locations, currentLanguage)
                val cachedHash = insightDao.getDataHash()
                val cachedInsight = insightDao.getInsight()

                if (!forceRefresh && cachedHash == currentDataHash && cachedInsight != null) {
                    return@withContext InsightResult(
                        summary = cachedInsight.summary,
                        fullReport = cachedInsight.fullReport
                    )
                }

                val systemPrompt = loadSystemPrompt()
                val languageInstruction = getLanguageInstruction(currentLanguage)
                val userPrompt = buildUserPrompt(moods, journals, locations, thirtyDaysAgo, today, languageInstruction)

                val model = GenerativeModel(
                    modelName = "gemini-3-pro-preview",
                    apiKey = apiKey,
                    systemInstruction = com.google.ai.client.generativeai.type.content {
                        text(systemPrompt)
                    }
                )

                val response = model.generateContent(userPrompt)
                val responseText = response.text ?: throw Exception("Empty response from AI")

                val jsonResponse = parseJsonResponse(responseText)
                val summary = jsonResponse.optString("summary", "Check your insights for personalized recommendations.")
                val fullReport = jsonResponse.optString("fullReport", responseText)

                val insightEntry = InsightEntry(
                    id = 1,
                    summary = summary,
                    fullReport = fullReport,
                    dataHash = currentDataHash,
                    generatedAt = System.currentTimeMillis()
                )
                insightDao.insertOrUpdate(insightEntry)

                InsightResult(summary = summary, fullReport = fullReport)
            } catch (e: Exception) {
                val cached = insightDao.getInsight()
                if (cached != null) {
                    InsightResult(
                        summary = cached.summary,
                        fullReport = cached.fullReport,
                        error = context.getString(R.string.error_using_cached, e.message ?: "")
                    )
                } else {
                    InsightResult(
                        summary = "",
                        fullReport = "",
                        error = e.message ?: context.getString(R.string.error_generate_insights)
                    )
                }
            }
        }
    }

    private fun loadSystemPrompt(): String {
        return context.resources.openRawResource(R.raw.insights_system_prompt)
            .bufferedReader()
            .use { it.readText() }
    }

    private fun getLanguageInstruction(language: AppLanguage): String {
        return when (language) {
            AppLanguage.VIETNAMESE -> "IMPORTANT: You MUST respond entirely in Vietnamese (Tiếng Việt). All text in your response including the summary and fullReport must be in Vietnamese."
            AppLanguage.ENGLISH -> "Respond in English."
            AppLanguage.SYSTEM_DEFAULT -> {
                val systemLocale = java.util.Locale.getDefault().language
                if (systemLocale == "vi") {
                    "IMPORTANT: You MUST respond entirely in Vietnamese (Tiếng Việt). All text in your response including the summary and fullReport must be in Vietnamese."
                } else {
                    "Respond in English."
                }
            }
        }
    }

    private fun buildUserPrompt(
        moods: List<MoodEntry>,
        journals: List<JournalEntry>,
        locations: List<LocationEntry>,
        startDate: LocalDate,
        endDate: LocalDate,
        languageInstruction: String
    ): String {
        val sb = StringBuilder()
        sb.appendLine(languageInstruction)
        sb.appendLine()
        sb.appendLine("Please analyze the following mood tracking data from ${startDate.format(dateFormatter)} to ${endDate.format(dateFormatter)}:")
        sb.appendLine()

        sb.appendLine("## MOOD ENTRIES (${moods.size} total)")
        if (moods.isEmpty()) {
            sb.appendLine("No mood entries recorded.")
        } else {
            val moodsByDate = moods.groupBy { it.date }
            moodsByDate.forEach { (date, dayMoods) ->
                sb.appendLine("- ${date.format(dateFormatter)}: ${dayMoods.joinToString(", ") { it.mood.label }}")
            }
        }
        sb.appendLine()

        sb.appendLine("## JOURNAL ENTRIES (${journals.size} total)")
        if (journals.isEmpty()) {
            sb.appendLine("No journal entries recorded.")
        } else {
            journals.forEach { journal ->
                sb.appendLine("### ${journal.date.format(dateFormatter)} - ${journal.title}")
                sb.appendLine(journal.content.take(500))
                if (journal.content.length > 500) sb.appendLine("...")
                sb.appendLine()
            }
        }

        sb.appendLine("## LOCATION DATA (${locations.size} entries)")
        if (locations.isEmpty()) {
            sb.appendLine("No location data recorded.")
        } else {
            val namedLocations = locations.filter { !it.placeName.isNullOrBlank() }
                .groupBy { it.placeName }
                .mapValues { it.value.size }
                .toList()
                .sortedByDescending { it.second }
                .take(10)
            
            if (namedLocations.isNotEmpty()) {
                sb.appendLine("Frequently visited places:")
                namedLocations.forEach { (place, count) ->
                    sb.appendLine("- $place: $count visits")
                }
            } else {
                sb.appendLine("Location names not available.")
            }
        }

        sb.appendLine()
        sb.appendLine("## MOOD DISTRIBUTION")
        val moodCounts = moods.groupBy { it.mood }.mapValues { it.value.size }
        moodCounts.entries.sortedByDescending { it.value }.forEach { (mood, count) ->
            sb.appendLine("- ${mood.emoji} ${mood.label}: $count times")
        }

        return sb.toString()
    }

    private fun computeDataHash(
        moods: List<MoodEntry>,
        journals: List<JournalEntry>,
        locations: List<LocationEntry>,
        language: AppLanguage
    ): String {
        val data = buildString {
            append("lang:${language.code ?: "system"}")
            append("moods:")
            moods.forEach { append("${it.id}${it.mood}${it.date}${it.createdAt}") }
            append("journals:")
            journals.forEach { append("${it.id}${it.updatedAt}") }
            append("locations:")
            locations.forEach { append("${it.id}${it.timestamp}") }
        }
        
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(data.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun parseJsonResponse(response: String): JSONObject {
        val jsonPattern = Regex("\\{[\\s\\S]*\\}")
        val match = jsonPattern.find(response)
        return if (match != null) {
            try {
                JSONObject(match.value)
            } catch (e: Exception) {
                JSONObject().apply {
                    put("summary", "Insights generated successfully.")
                    put("fullReport", response)
                }
            }
        } else {
            JSONObject().apply {
                put("summary", "Insights generated successfully.")
                put("fullReport", response)
            }
        }
    }
}
