package com.bike.ride.ui.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bike.ride.data.preferences.SettingsPreferences
import androidx.lifecycle.viewModelScope

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {
    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsPreferences.getSettings().collect { settings ->
                _settingsState.value = settings
            }
        }
    }

    fun updateTheme(isDark: Boolean) {
        _settingsState.value = _settingsState.value.copy(isDarkMode = isDark)
    }

    fun toggleTemperatureUnit() {
        viewModelScope.launch {
            val newState = _settingsState.value.copy(
                useCelsius = !_settingsState.value.useCelsius
            )
            _settingsState.value = newState
            settingsPreferences.saveSettings(newState)
        }
    }

    fun toggleSpeedUnit() {
        viewModelScope.launch {
            val newState = _settingsState.value.copy(
                useKmh = !_settingsState.value.useKmh
            )
            _settingsState.value = newState
            settingsPreferences.saveSettings(newState)
        }
    }
} 