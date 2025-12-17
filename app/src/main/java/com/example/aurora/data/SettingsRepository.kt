package com.example.aurora.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aurora_settings")

enum class AppLanguage(val code: String?, val displayName: String) {
    SYSTEM_DEFAULT(null, "System Default"),
    ENGLISH("en", "English"),
    VIETNAMESE("vi", "Tiếng Việt")
}

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val GOOGLE_AI_API_KEY = stringPreferencesKey("google_ai_api_key")
        private val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    val googleAiApiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[GOOGLE_AI_API_KEY]
    }

    val appLanguage: Flow<AppLanguage> = context.dataStore.data.map { preferences ->
        val code = preferences[APP_LANGUAGE]
        AppLanguage.entries.find { it.code == code } ?: AppLanguage.SYSTEM_DEFAULT
    }

    suspend fun getGoogleAiApiKey(): String? {
        return context.dataStore.data.first()[GOOGLE_AI_API_KEY]
    }

    suspend fun setGoogleAiApiKey(apiKey: String?) {
        context.dataStore.edit { preferences ->
            if (apiKey.isNullOrBlank()) {
                preferences.remove(GOOGLE_AI_API_KEY)
            } else {
                preferences[GOOGLE_AI_API_KEY] = apiKey
            }
        }
    }

    suspend fun hasGoogleAiApiKey(): Boolean {
        return !getGoogleAiApiKey().isNullOrBlank()
    }

    suspend fun getAppLanguage(): AppLanguage {
        val code = context.dataStore.data.first()[APP_LANGUAGE]
        return AppLanguage.entries.find { it.code == code } ?: AppLanguage.SYSTEM_DEFAULT
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            if (language == AppLanguage.SYSTEM_DEFAULT) {
                preferences.remove(APP_LANGUAGE)
            } else {
                preferences[APP_LANGUAGE] = language.code ?: return@edit
            }
        }
    }
}
