package com.bike.ride

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.bike.ride.navigation.Screen
import com.bike.ride.ui.screen.dashboard.DashboardScreen
import com.bike.ride.ui.screen.dashboard.DashboardViewModel
import com.bike.ride.ui.screen.settings.SettingsScreen
import com.bike.ride.ui.screen.settings.SettingsViewModel
import com.bike.ride.ui.screen.weather.WeatherScreen
import com.bike.ride.ui.screen.weather.WeatherViewModel
import com.bike.ride.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavType
import androidx.navigation.navArgument

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Permission granted, fetch weather
                weatherViewModel.fetchWeatherData()
            }
        }
    }

    private val weatherViewModel: WeatherViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()

        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDarkTheme) }

            WeatherAppTheme(darkTheme = isDarkTheme) {
                MainScreen(
                    modifier = Modifier,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = it }
                )
            }
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                weatherViewModel.fetchWeatherData()
            }

            else -> {
                // Request permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    @Composable
    fun MainScreen(
        modifier: Modifier = Modifier,
        isDarkTheme: Boolean,
        onThemeChange: (Boolean) -> Unit,
        navController: NavHostController = rememberNavController()
    ) {
        val settingsViewModel: SettingsViewModel = hiltViewModel()
        val dashboardViewModel: DashboardViewModel = hiltViewModel()
        val weatherViewModel: WeatherViewModel = hiltViewModel()

        Scaffold(
            bottomBar = {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    listOf(
                        Screen.Dashboard,
                        Screen.Forecast,
                        Screen.Settings
                    ).forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(stringResource(screen.labelResId)) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = modifier.padding(paddingValues)
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        settingsViewModel = settingsViewModel,
                        onLocationClick = { location ->
                            navController.navigate(Screen.Forecast.createRoute(location))
                        }
                    )
                }
                composable(
                    route = Screen.Forecast.route,
                    arguments = listOf(
                        navArgument("location") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val location = backStackEntry.arguments?.getString("location")
                    WeatherScreen(
                        weatherViewModel = weatherViewModel,
                        settingsViewModel = settingsViewModel,
                        location = location
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onThemeChange = onThemeChange,
                        isDarkTheme = isDarkTheme,
                        viewModel = settingsViewModel
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewMainScreen() {
        MainScreen(isDarkTheme = false, onThemeChange = {})
    }
}