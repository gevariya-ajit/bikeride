package com.bike.ride.domain.model

data class WeatherDay(
    val time: String,
    val temperature: Double,
    val condition: String,
    val windSpeed: Double,
    val bikeRideScore: Int,
    val isGoodForBiking: Boolean
) 