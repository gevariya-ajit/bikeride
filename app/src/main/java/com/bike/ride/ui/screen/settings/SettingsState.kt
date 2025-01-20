package com.bike.ride.ui.screen.settings

data class SettingsState(
    val isDarkMode: Boolean = false,
    val useCelsius: Boolean = true,
    val useKmh: Boolean = true
) 