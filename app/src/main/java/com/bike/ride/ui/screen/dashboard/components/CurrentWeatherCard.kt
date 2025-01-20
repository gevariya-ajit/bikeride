package com.bike.ride.ui.screen.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bike.ride.R
import com.bike.ride.domain.model.CurrentWeather
import com.bike.ride.ui.theme.getScoreColor

@Composable
fun CurrentWeatherCard(
    currentWeather: CurrentWeather?,
    useCelsius: Boolean,
    useKmh: Boolean,
    modifier: Modifier = Modifier
) {
    if (currentWeather == null) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getScoreColor(currentWeather.bikeRideScore)
                .copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.current_weather),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Temperature and Conditions
                Column {
                    Text(
                        text = if (useCelsius) 
                            "${currentWeather.temperature}°C" 
                        else 
                            "${celsiusToFahrenheit(currentWeather.temperature)}°F",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentWeather.conditions,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Bike Ride Score
                Surface(
                    color = getScoreColor(currentWeather.bikeRideScore),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "${currentWeather.bikeRideScore}%",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.surface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weather Details
            WeatherDetailRow(
                label = stringResource(R.string.feels_like),
                value = if (useCelsius) 
                    "${currentWeather.feelsLike}°C" 
                else 
                    "${celsiusToFahrenheit(currentWeather.feelsLike)}°F"
            )
            WeatherDetailRow(
                label = stringResource(R.string.label_wind_speed),
                value = if (useKmh) 
                    "${currentWeather.windSpeed} km/h" 
                else 
                    "${kmhToMph(currentWeather.windSpeed)} mph"
            )
            WeatherDetailRow(
                label = stringResource(R.string.humidity),
                value = "${currentWeather.humidity}%"
            )
            WeatherDetailRow(
                label = stringResource(R.string.precipitation),
                value = "${currentWeather.precipitation}mm"
            )
        }
    }
}

@Composable
private fun WeatherDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun celsiusToFahrenheit(celsius: Double): Double {
    return (celsius * 9/5) + 32
}

private fun kmhToMph(kmh: Double): Double {
    return kmh * 0.621371
} 