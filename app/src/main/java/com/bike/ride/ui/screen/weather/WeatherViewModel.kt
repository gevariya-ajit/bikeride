package com.bike.ride.ui.screen.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bike.ride.data.repository.WeatherRepository
import com.bike.ride.data.model.SearchLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bike.ride.data.repository.LocationStateHolder

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationStateHolder: LocationStateHolder
) : ViewModel() {
    private val _weatherState = MutableStateFlow(WeatherState())
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    init {
        fetchWeatherData()  // Load current location data immediately
    }

    fun searchLocations(query: String) {
        viewModelScope.launch {
            try {
                _searchState.value = _searchState.value.copy(isSearching = true)
                val locations = weatherRepository.searchLocations(query)
                _searchState.value = SearchState(locations = locations)
            } catch (e: Exception) {
                _searchState.value = SearchState(error = e.message)
            }
        }
    }

    fun updateLocation(location: String) {
        viewModelScope.launch {
            try {
                _weatherState.value = _weatherState.value.copy(isLoading = true)
                val (locationName, weatherData) = weatherRepository.getWeatherForecast(
                    locationQuery = location
                )
                _weatherState.value = WeatherState(
                    weatherData = weatherData,
                    location = locationName,
                    isLoading = false
                )
            } catch (e: Exception) {
                _weatherState.value = WeatherState(error = e.message)
            }
        }
    }

    fun refreshWeatherData() {
        fetchWeatherData(forceRefresh = true)
    }

    fun fetchWeatherData(forceRefresh: Boolean = false, location: String? = null) {
        viewModelScope.launch {
            try {
                if (forceRefresh) {
                    _weatherState.value = _weatherState.value.copy(isLoading = true)
                }
                // Pass location to repository
                val (locationName, weatherData) = weatherRepository.getWeatherForecast(
                    forceRefresh = forceRefresh,
                    locationQuery = location
                )
                _weatherState.value = WeatherState(
                    weatherData = weatherData,
                    location = locationName,
                    isLoading = false
                )
            } catch (e: Exception) {
                _weatherState.value = WeatherState(error = e.message)
            }
        }
    }
}

data class SearchState(
    val locations: List<SearchLocation> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null
) 