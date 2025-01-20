package com.bike.ride.ui.screen.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bike.ride.domain.model.HourlyWeather
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HourlyWeatherItem(
    hourlyWeather: HourlyWeather,
    useCelsius: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(hourlyWeather.time),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (useCelsius) 
                    "${hourlyWeather.temperature}°C" 
                else 
                    "${celsiusToFahrenheit(hourlyWeather.temperature)}°F",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = hourlyWeather.conditions,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            if (hourlyWeather.precipitation > 0) {
                Text(
                    text = "${hourlyWeather.precipitation}mm",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun formatTime(time: String): String {
    return LocalTime.parse(time).format(DateTimeFormatter.ofPattern("HH:mm"))
}

private fun celsiusToFahrenheit(celsius: Double): Double {
    return (celsius * 9/5) + 32
} 