package com.bike.ride.domain.model

data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val conditions: String,
    val windSpeed: Double,
    val humidity: Int,
    val precipitation: Double,
    val bikeRideScore: Int
) 