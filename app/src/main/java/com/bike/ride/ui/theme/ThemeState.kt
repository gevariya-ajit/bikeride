package com.bike.ride.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Stable

@Stable
class ThemeState {
    var isDarkMode by mutableStateOf(false)
}

val LocalThemeState = staticCompositionLocalOf { ThemeState() } 