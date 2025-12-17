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

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val GOOGLE_AI_API_KEY = stringPreferencesKey("google_ai_api_key")
    }

    val googleAiApiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[GOOGLE_AI_API_KEY]
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
}
