package com.bike.ride.data.model

data class WeatherResponse(
    val location: Location,
    val current: Current,
    val forecast: Forecast
)

data class Location(
    val name: String,
    val region: String,
    val country: String
)

data class Current(
    val temp_c: Double,
    val feelslike_c: Double,
    val condition: Condition,
    val wind_kph: Double,
    val humidity: Int,
    val precip_mm: Double
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<Hour>
)

data class Day(
    val avgtemp_c: Double,
    val maxwind_kph: Double,
    val condition: Condition,
    val daily_chance_of_rain: Int
)

data class Hour(
    val time: String,
    val temp_c: Double,
    val condition: Condition,
    val precip_mm: Double
)

data class Condition(
    val text: String
) 