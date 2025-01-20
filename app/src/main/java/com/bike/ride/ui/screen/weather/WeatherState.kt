package com.bike.ride.ui.screen.weather

import com.bike.ride.domain.model.WeatherDay

data class WeatherState(
    val weatherData: List<WeatherDay> = emptyList(),
    val location: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) 