package com.bike.ride.ui.screen.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bike.ride.R
import com.bike.ride.domain.model.CurrentWeather
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import com.bike.ride.util.formatTemperature
import com.bike.ride.util.formatWindSpeed

@Composable
fun LocationCard(
    location: String,
    currentWeather: CurrentWeather?,
    useCelsius: Boolean,
    useKmh: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    isCurrentLocation: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = location,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (!isCurrentLocation) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete location")
                    }
                }
            }

            if (currentWeather != null) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = currentWeather.temperature.formatTemperature(useCelsius),
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Text(
                    text = currentWeather.conditions,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherDetail(
                        label = stringResource(R.string.feels_like),
                        value = currentWeather.feelsLike.formatTemperature(useCelsius)
                    )
                    WeatherDetail(
                        label = stringResource(R.string.label_wind_speed),
                        value = currentWeather.windSpeed.formatWindSpeed(useKmh)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherDetail(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
} 