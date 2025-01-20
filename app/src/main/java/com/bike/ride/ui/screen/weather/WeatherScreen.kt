package com.bike.ride.ui.screen.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bike.ride.ui.screen.settings.SettingsViewModel
import com.bike.ride.ui.screen.weather.components.WeatherList
import com.bike.ride.ui.theme.ScorePoorColor

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel,
    settingsViewModel: SettingsViewModel,
    location: String? = null
) {
    LaunchedEffect(location) {
        weatherViewModel.fetchWeatherData(location = location)
    }
    
    val weatherState by weatherViewModel.weatherState.collectAsState()
    val settingsState by settingsViewModel.settingsState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Location Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = weatherState.location,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            // Weather List
            when {
                weatherState.error != null -> {
                    ErrorMessage(weatherState.error!!)
                }
                else -> {
                    WeatherList(
                        weatherData = weatherState.weatherData,
                        location = weatherState.location,
                        isLoading = weatherState.isLoading,
                        onRefresh = { weatherViewModel.refreshWeatherData() },
                        onLocationClick = { /* Location is now managed by Dashboard */ },
                        useCelsius = settingsState.useCelsius,
                        useKmh = settingsState.useKmh,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineMedium,
            color = ScorePoorColor
        )
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
} 