package com.bike.ride.data.api

import com.bike.ride.data.model.WeatherResponse
import com.bike.ride.data.model.SearchLocation
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String = "422c5045641a477092e162207251801",
        @Query("q") location: String,
        @Query("days") days: Int = 3,
        @Query("aqi") aqi: String = "no",
        @Query("hour") hour: Int = 24
    ): WeatherResponse

    @GET("search.json")
    suspend fun searchLocation(
        @Query("key") apiKey: String = "422c5045641a477092e162207251801",
        @Query("q") query: String
    ): List<SearchLocation>
} 