package com.bike.ride.ui.screen.dashboard

import com.bike.ride.data.model.SearchLocation
import com.bike.ride.domain.model.CurrentWeather
import com.bike.ride.domain.model.HourlyWeather

data class DashboardState(
    val locations: List<LocationData> = emptyList(),
    val selectedLocationDetails: LocationDetails? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class LocationData(
    val id: String,
    val name: String,
    val currentWeather: CurrentWeather?
)

data class LocationDetails(
    val name: String,
    val currentWeather: CurrentWeather,
    val hourlyForecast: List<HourlyWeather>
)

data class SearchState(
    val locations: List<SearchLocation> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null
) 