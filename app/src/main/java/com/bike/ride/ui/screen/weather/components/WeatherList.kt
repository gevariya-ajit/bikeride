package com.bike.ride.ui.screen.weather.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bike.ride.domain.model.WeatherDay
import com.bike.ride.util.formatTemperature
import com.bike.ride.util.formatWindSpeed
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeatherList(
    weatherData: List<WeatherDay>,
    location: String,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onLocationClick: () -> Unit,
    useCelsius: Boolean,
    useKmh: Boolean,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = onRefresh
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(weatherData) { day ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Date/Time
                        Text(
                            text = formatDate(day.time),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Weather Details
                        WeatherDetails(
                            weatherDay = day,
                            useCelsius = useCelsius,
                            useKmh = useKmh
                        )
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun WeatherDetails(
    weatherDay: WeatherDay,
    useCelsius: Boolean,
    useKmh: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Bike Score Indicator
        BikeScoreIndicator(
            score = weatherDay.bikeRideScore,
            isGood = weatherDay.isGoodForBiking
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Temperature with icon
        WeatherDetailRow(
            icon = "🌡️",
            value = weatherDay.temperature.formatTemperature(useCelsius),
            style = MaterialTheme.typography.headlineMedium
        )

        // Condition with icon
        WeatherDetailRow(
            icon = "☁️",
            value = weatherDay.condition,
            style = MaterialTheme.typography.bodyLarge
        )

        // Wind speed with icon
        WeatherDetailRow(
            icon = "💨",
            value = weatherDay.windSpeed.formatWindSpeed(useKmh),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun BikeScoreIndicator(
    score: Int,
    isGood: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🚲",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (isGood) "Good day for biking!" else "Not ideal for biking",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isGood) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error
            )
        }
        
        Text(
            text = "$score%",
            style = MaterialTheme.typography.titleMedium,
            color = when {
                score >= 70 -> MaterialTheme.colorScheme.primary
                score >= 40 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.error
            }
        )
    }
}

@Composable
private fun WeatherDetailRow(
    icon: String,
    value: String,
    style: TextStyle
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = icon)
        Text(
            text = value,
            style = style
        )
    }
}

private fun formatDate(dateString: String): String {
    val date = LocalDate.parse(dateString)
    return date.dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
} 