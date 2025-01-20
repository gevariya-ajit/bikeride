package com.bike.ride.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "locations")

@Singleton
class LocationPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    private object PreferencesKeys {
        val SAVED_LOCATIONS = stringPreferencesKey("saved_locations")
    }

    val savedLocations: Flow<List<String>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val locationsJson = preferences[PreferencesKeys.SAVED_LOCATIONS] ?: "[]"
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(locationsJson, type)
        }

    suspend fun saveLocations(locations: List<String>) {
        context.dataStore.edit { preferences ->
            val locationsJson = gson.toJson(locations)
            preferences[PreferencesKeys.SAVED_LOCATIONS] = locationsJson
        }
    }
} 