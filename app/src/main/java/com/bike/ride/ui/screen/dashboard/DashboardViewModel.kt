package com.bike.ride.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bike.ride.data.repository.WeatherRepository
import com.bike.ride.data.repository.LocationStateHolder
import com.bike.ride.data.preferences.LocationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationStateHolder: LocationStateHolder,
    private val locationPreferences: LocationPreferences
) : ViewModel() {

    private val _dashboardState = MutableStateFlow(DashboardState())
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    init {
        // Load saved locations
        viewModelScope.launch {
            locationPreferences.savedLocations
                .catch { fetchWeatherData() }  // Handle any errors by fetching current location
                .collect { savedLocations ->
                    if (savedLocations.isNotEmpty()) {
                        loadSavedLocations(savedLocations)
                    } else {
                        fetchWeatherData()
                    }
                }
        }
    }

    private suspend fun loadSavedLocations(locationIds: List<String>) {
        try {
            _dashboardState.value = _dashboardState.value.copy(isLoading = true)
            
            // First, get current location
            val (currentName, currentWeather, _) = weatherRepository.getCurrentWeather()
            val currentLocation = LocationData(
                id = "current_location",
                name = currentName,
                currentWeather = currentWeather
            )

            // Then load saved locations
            val additionalLocations = locationIds.mapNotNull { locationId ->
                try {
                    val (locationName, weather, _) = weatherRepository.getCurrentWeather(locationId)
                    LocationData(
                        id = locationId,
                        name = locationName,
                        currentWeather = weather
                    )
                } catch (e: Exception) {
                    null  // Skip failed locations
                }
            }

            // Combine current location with saved locations
            val allLocations = listOf(currentLocation) + additionalLocations

            _dashboardState.value = _dashboardState.value.copy(
                locations = allLocations,
                isLoading = false
            )

            // Set current location as selected if none selected
            if (locationStateHolder.selectedLocation.value == null) {
                locationStateHolder.updateSelectedLocation("current_location")
            }
        } catch (e: Exception) {
            fetchWeatherData()
        }
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

    fun addLocation(locationQuery: String) {
        viewModelScope.launch {
            try {
                _dashboardState.value = _dashboardState.value.copy(isLoading = true)
                val (locationName, currentWeather, hourlyForecast) = 
                    weatherRepository.getCurrentWeather(locationQuery)
                
                // Create new location
                val newLocation = LocationData(
                    id = locationQuery,  // Keep the search query as ID for additional locations
                    name = locationName,
                    currentWeather = currentWeather
                )

                // Get current location (first item) and existing additional locations
                val currentLocation = _dashboardState.value.locations.firstOrNull()
                val existingLocations = _dashboardState.value.locations.drop(1)

                // Combine: current location + new location + existing additional locations
                val updatedLocations = listOfNotNull(currentLocation) + newLocation + existingLocations
                
                _dashboardState.value = _dashboardState.value.copy(
                    locations = updatedLocations,
                    selectedLocationDetails = LocationDetails(
                        name = locationName,
                        currentWeather = currentWeather,
                        hourlyForecast = hourlyForecast
                    ),
                    isLoading = false
                )

                // Save all additional locations to preferences (excluding current location)
                locationPreferences.saveLocations(updatedLocations.drop(1).map { it.id })
                
                // Update selected location to the newly added location
                locationStateHolder.updateSelectedLocation(locationQuery)
            } catch (e: Exception) {
                _dashboardState.value = _dashboardState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun deleteLocation(locationId: String) {
        // Prevent deletion of current location
        if (locationId == "current_location") return

        val currentLocations = _dashboardState.value.locations
        val updatedLocations = currentLocations.filter { it.id != locationId }
        
        _dashboardState.value = _dashboardState.value.copy(
            locations = updatedLocations
        )

        // Save only additional locations to preferences
        viewModelScope.launch {
            locationPreferences.saveLocations(updatedLocations.drop(1).map { it.id })
        }

        if (locationStateHolder.selectedLocation.value == locationId) {
            locationStateHolder.updateSelectedLocation("current_location")
        }
    }

    fun refreshLocations() {
        viewModelScope.launch {
            try {
                _dashboardState.value = _dashboardState.value.copy(isLoading = true)
                
                val updatedLocations = _dashboardState.value.locations.map { location ->
                    val (locationName, currentWeather, _) = weatherRepository.getCurrentWeather(location.id)
                    location.copy(
                        name = locationName,
                        currentWeather = currentWeather
                    )
                }

                _dashboardState.value = _dashboardState.value.copy(
                    locations = updatedLocations,
                    isLoading = false
                )
            } catch (e: Exception) {
                _dashboardState.value = _dashboardState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun updateSelectedLocation(locationId: String) {
        viewModelScope.launch {
            try {
                _dashboardState.value = _dashboardState.value.copy(isLoading = true)
                val (locationName, currentWeather, hourlyForecast) = 
                    weatherRepository.getCurrentWeather(locationId)

                _dashboardState.value = _dashboardState.value.copy(
                    selectedLocationDetails = LocationDetails(
                        name = locationName,
                        currentWeather = currentWeather,
                        hourlyForecast = hourlyForecast
                    ),
                    isLoading = false
                )
                locationStateHolder.updateSelectedLocation(locationId)
            } catch (e: Exception) {
                _dashboardState.value = _dashboardState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun shareLocation(location: String) {
        locationStateHolder.updateSelectedLocation(location)
    }

    private fun fetchWeatherData() {
        viewModelScope.launch {
            try {
                _dashboardState.value = _dashboardState.value.copy(isLoading = true)
                
                // Get current location weather
                val (locationName, currentWeather, hourlyForecast) = 
                    weatherRepository.getCurrentWeather()
                
                // Create initial location
                val initialLocation = LocationData(
                    id = "current_location",
                    name = locationName,
                    currentWeather = currentWeather
                )

                // Create location details
                val locationDetails = LocationDetails(
                    name = locationName,
                    currentWeather = currentWeather,
                    hourlyForecast = hourlyForecast
                )

                // Update state
                _dashboardState.value = _dashboardState.value.copy(
                    locations = listOf(initialLocation),
                    selectedLocationDetails = locationDetails,
                    isLoading = false
                )

                // Save current location to preferences
                locationPreferences.saveLocations(listOf("current_location"))

                // Set as selected location
                locationStateHolder.updateSelectedLocation("current_location")
                
            } catch (e: Exception) {
                _dashboardState.value = _dashboardState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
} 