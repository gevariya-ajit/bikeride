package com.bike.ride.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationStateHolder @Inject constructor() {
    private val _selectedLocation = MutableStateFlow<String?>(null)
    val selectedLocation: StateFlow<String?> = _selectedLocation.asStateFlow()

    fun updateSelectedLocation(location: String?) {
        _selectedLocation.value = location
    }
} 