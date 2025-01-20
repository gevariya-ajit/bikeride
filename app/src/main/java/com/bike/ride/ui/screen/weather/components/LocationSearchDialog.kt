package com.bike.ride.ui.screen.weather.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bike.ride.data.model.SearchLocation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (String) -> Unit,
    searchLocations: (String) -> Unit,
    locations: List<SearchLocation>,
    isSearching: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = { Text("Search Location") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        if (it.length >= 3) {
                            searchLocations(it)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter city name") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, "Clear")
                            }
                        }
                    },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(locations) { location ->
                            LocationItem(
                                location = location,
                                onLocationSelected = {
                                    onLocationSelected("${location.lat},${location.lon}")
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun LocationItem(
    location: SearchLocation,
    onLocationSelected: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLocationSelected)
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${location.region}, ${location.country}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
} 