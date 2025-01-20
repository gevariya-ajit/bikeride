package com.bike.ride.domain.model

data class HourlyWeather(
    val time: String,
    val temperature: Double,
    val conditions: String,
    val precipitation: Double
) 