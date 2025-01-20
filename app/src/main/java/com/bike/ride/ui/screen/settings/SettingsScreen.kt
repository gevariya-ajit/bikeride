package com.bike.ride.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bike.ride.R

@Composable
fun SettingsScreen(
    onThemeChange: (Boolean) -> Unit,
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState by viewModel.settingsState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Theme Setting
        SettingItem(
            title = stringResource(R.string.settings_dark_theme),
            subtitle = stringResource(R.string.settings_dark_theme_desc),
            control = {
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onThemeChange
                )
            }
        )

        // Temperature Unit Setting
        SettingItem(
            title = stringResource(R.string.settings_temp_unit),
            subtitle = stringResource(
                if (settingsState.useCelsius) 
                    R.string.settings_using_celsius 
                else 
                    R.string.settings_using_fahrenheit
            ),
            control = {
                Switch(
                    checked = settingsState.useCelsius,
                    onCheckedChange = { viewModel.toggleTemperatureUnit() }
                )
            }
        )

        // Wind Speed Unit Setting
        SettingItem(
            title = stringResource(R.string.settings_wind_unit),
            subtitle = stringResource(
                if (settingsState.useKmh) 
                    R.string.settings_using_kmh 
                else 
                    R.string.settings_using_mph
            ),
            control = {
                Switch(
                    checked = settingsState.useKmh,
                    onCheckedChange = { viewModel.toggleSpeedUnit() }
                )
            }
        )
    }
}

@Composable
private fun SettingItem(
    title: String,
    subtitle: String,
    control: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            control()
        }
    }
} 