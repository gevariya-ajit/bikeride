package com.bike.ride.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.bike.ride.ui.screen.settings.SettingsState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val USE_CELSIUS = booleanPreferencesKey("use_celsius")
        val USE_KMH = booleanPreferencesKey("use_kmh")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val settings: Flow<SettingsState> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            SettingsState(
                useCelsius = preferences[PreferencesKeys.USE_CELSIUS] ?: true,
                useKmh = preferences[PreferencesKeys.USE_KMH] ?: true,
                isDarkMode = preferences[PreferencesKeys.DARK_MODE] ?: false
            )
        }

    suspend fun saveSettings(settings: SettingsState) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_CELSIUS] = settings.useCelsius
            preferences[PreferencesKeys.USE_KMH] = settings.useKmh
            preferences[PreferencesKeys.DARK_MODE] = settings.isDarkMode
        }
    }

    suspend fun getSettings(): Flow<SettingsState> = settings
} 