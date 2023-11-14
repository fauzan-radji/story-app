package com.fauzan.storytelling.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_preference")

class SettingsPreference private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun setIncludeLocation(isIncluded: Boolean) {
        dataStore.edit { preferences ->
            preferences[INCLUDE_LOCATION] = isIncluded
        }
    }

    fun getIncludeLocation(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[INCLUDE_LOCATION] ?: false
        }
    }

    companion object {
        private val INCLUDE_LOCATION = booleanPreferencesKey("save_location")

        @Volatile
        private var instance: SettingsPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingsPreference {
            return instance ?: synchronized(this) {
                instance ?: SettingsPreference(dataStore).also { instance = it }
            }
        }
    }
}