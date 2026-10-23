package com.nowsilence.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_settings"
)

class UserSettingsDataStore(private val context: Context) {

    private object Keys {
        val IS_FILTER_ENABLED = booleanPreferencesKey("is_filter_enabled")
        val FILTER_PROFILE = stringPreferencesKey("filter_profile")
        val USAGE_PLAN = stringPreferencesKey("usage_plan")
        val EXTERNAL_API_KEY = stringPreferencesKey("external_api_key")
    }

    val isFilterEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences -> preferences[Keys.IS_FILTER_ENABLED] ?: false }

    val filterProfile: Flow<String> = context.settingsDataStore.data
        .map { preferences -> preferences[Keys.FILTER_PROFILE] ?: "PERSONAL" }

    val usagePlan: Flow<String> = context.settingsDataStore.data
        .map { preferences -> preferences[Keys.USAGE_PLAN] ?: "PERSONAL" }

    val externalApiKey: Flow<String> = context.settingsDataStore.data
        .map { preferences -> preferences[Keys.EXTERNAL_API_KEY] ?: "" }

    suspend fun setFilterEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.IS_FILTER_ENABLED] = enabled
        }
    }

    suspend fun setFilterProfile(profile: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.FILTER_PROFILE] = profile
        }
    }

    suspend fun setUsagePlan(plan: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.USAGE_PLAN] = plan
        }
    }

    suspend fun setExternalApiKey(apiKey: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.EXTERNAL_API_KEY] = apiKey
        }
    }
}
