package com.bike.ride.data.repository

import com.bike.ride.data.api.WeatherApi
import com.bike.ride.data.model.*
import com.bike.ride.domain.model.WeatherDay
import com.bike.ride.domain.model.CurrentWeather
import com.bike.ride.domain.model.HourlyWeather
import com.bike.ride.utils.LocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    private val locationManager: LocationManager
) {
    suspend fun searchLocations(query: String): List<SearchLocation> = withContext(Dispatchers.IO) {
        weatherApi.searchLocation(query = query)
    }

    suspend fun getWeatherForecast(
        forceRefresh: Boolean = false,
        locationQuery: String? = null
    ): Pair<String, List<WeatherDay>> = withContext(Dispatchers.IO) {
        val query = when {
            locationQuery != null -> locationQuery
            forceRefresh -> {
                val location = locationManager.getCurrentLocation(true)
                if (location != null) "${location.latitude},${location.longitude}" else "auto:ip"
            }
            else -> {
                val location = locationManager.getCurrentLocation()
                if (location != null) "${location.latitude},${location.longitude}" else "auto:ip"
            }
        }
        
        val response = weatherApi.getForecast(location = query)
        val locationName = "${response.location.name}, ${response.location.country}"
        val weatherData = response.forecast.forecastday.map { day ->
            val bikeRideScore = calculateDailyBikeRideScore(day)
            WeatherDay(
                time = day.date,
                temperature = day.day.avgtemp_c,
                condition = day.day.condition.text,
                windSpeed = day.day.maxwind_kph,
                bikeRideScore = bikeRideScore,
                isGoodForBiking = bikeRideScore >= 70
            )
        }
        return@withContext Pair(locationName, weatherData)
    }

    suspend fun getCurrentWeather(
        locationQuery: String? = null
    ): Triple<String, CurrentWeather, List<HourlyWeather>> = withContext(Dispatchers.IO) {
        val query = locationQuery ?: run {
            val location = locationManager.getCurrentLocation()
            if (location != null) "${location.latitude},${location.longitude}" else "auto:ip"
        }
        
        val response = weatherApi.getForecast(location = query)
        val locationName = "${response.location.name}, ${response.location.country}"
        
        val current = response.current.let { current ->
            CurrentWeather(
                temperature = current.temp_c,
                feelsLike = current.feelslike_c,
                conditions = current.condition.text,
                windSpeed = current.wind_kph,
                humidity = current.humidity,
                precipitation = current.precip_mm,
                bikeRideScore = calculateCurrentBikeRideScore(current)
            )
        }
        
        val hourly = response.forecast.forecastday.first().hour.map { hour ->
            HourlyWeather(
                time = hour.time,
                temperature = hour.temp_c,
                conditions = hour.condition.text,
                precipitation = hour.precip_mm
            )
        }
        
        Triple(locationName, current, hourly)
    }

    private fun calculateDailyBikeRideScore(day: ForecastDay): Int {
        // Temperature score (0-100)
        val tempScore = when (day.day.avgtemp_c) {
            in Double.NEGATIVE_INFINITY..0.0 -> 0    // Too cold
            in 0.0..10.0 -> 30                       // Very cold
            in 10.0..15.0 -> 60                      // Cool
            in 15.0..25.0 -> 100                     // Perfect
            in 25.0..30.0 -> 70                      // Warm
            else -> 30                               // Too hot
        }

        // Wind speed score (0-100)
        val windScore = when (day.day.maxwind_kph) {
            in 0.0..5.0 -> 100                       // Perfect
            in 5.0..10.0 -> 90                       // Very good
            in 10.0..15.0 -> 80                      // Good
            in 15.0..20.0 -> 60                      // Moderate
            in 20.0..25.0 -> 40                      // Windy
            in 25.0..30.0 -> 20                      // Very windy
            else -> 0                                // Too windy
        }

        // Rain chance score (0-100)
        val rainScore = max(0, 100 - day.day.daily_chance_of_rain)

        // Calculate final score (weighted average)
        val finalScore = (tempScore * 0.4 + windScore * 0.3 + rainScore * 0.3).toInt()
        
        // Ensure score is between 0 and 100
        return min(100, max(0, finalScore))
    }

    private fun calculateCurrentBikeRideScore(current: Current): Int {
        // Temperature score (0-100)
        val tempScore = when (current.temp_c) {
            in Double.NEGATIVE_INFINITY..0.0 -> 0    // Too cold
            in 0.0..10.0 -> 30                       // Very cold
            in 10.0..15.0 -> 60                      // Cool
            in 15.0..25.0 -> 100                     // Perfect
            in 25.0..30.0 -> 70                      // Warm
            else -> 30                               // Too hot
        }

        // Wind speed score (0-100)
        val windScore = when (current.wind_kph) {
            in 0.0..5.0 -> 100                       // Perfect
            in 5.0..10.0 -> 90                       // Very good
            in 10.0..15.0 -> 80                      // Good
            in 15.0..20.0 -> 60                      // Moderate
            in 20.0..25.0 -> 40                      // Windy
            in 25.0..30.0 -> 20                      // Very windy
            else -> 0                                // Too windy
        }

        // Rain score (0-100)
        val rainScore = if (current.precip_mm > 0) {
            when (current.precip_mm) {
                in 0.0..0.5 -> 80
                in 0.5..2.0 -> 60
                in 2.0..5.0 -> 30
                else -> 0
            }
        } else {
            100
        }

        // Calculate final score (weighted average)
        val finalScore = (tempScore * 0.4 + windScore * 0.3 + rainScore * 0.3).toInt()
        
        // Ensure score is between 0 and 100
        return min(100, max(0, finalScore))
    }
} 