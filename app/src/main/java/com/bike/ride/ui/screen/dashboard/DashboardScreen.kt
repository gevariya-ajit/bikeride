package com.bike.ride.ui.screen.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bike.ride.R
import com.bike.ride.ui.screen.settings.SettingsViewModel
import com.bike.ride.ui.screen.weather.components.LocationSearchDialog
import com.bike.ride.ui.screen.dashboard.components.LocationCard
import androidx.compose.material.icons.filled.Add

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel,
    settingsViewModel: SettingsViewModel,
    onLocationClick: (String) -> Unit
) {
    val dashboardState by viewModel.dashboardState.collectAsState()
    val settingsState by settingsViewModel.settingsState.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    var showLocationSearch by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showLocationSearch = true }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add location")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // App Title
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Location Cards
            dashboardState.locations.forEach { locationData ->
                LocationCard(
                    location = locationData.name,
                    currentWeather = locationData.currentWeather,
                    useCelsius = settingsState.useCelsius,
                    useKmh = settingsState.useKmh,
                    onDelete = { viewModel.deleteLocation(locationData.id) },
                    onClick = { onLocationClick(locationData.id) },
                    isCurrentLocation = locationData.id == "current_location",
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }

    if (showLocationSearch) {
        LocationSearchDialog(
            onDismiss = { showLocationSearch = false },
            onLocationSelected = { 
                viewModel.addLocation(it)
                showLocationSearch = false
            },
            searchLocations = { viewModel.searchLocations(it) },
            locations = searchState.locations,
            isSearching = searchState.isSearching
        )
    }
} 