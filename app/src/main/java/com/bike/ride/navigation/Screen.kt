package com.bike.ride.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import com.bike.ride.R
import androidx.annotation.StringRes
import androidx.compose.material.icons.rounded.*

sealed class Screen(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector
) {
    object Dashboard : Screen("home", R.string.menu_home, Icons.Rounded.Home)
    object Forecast : Screen("forecast?location={location}", R.string.menu_forecast, Icons.Rounded.WbSunny) {
        fun createRoute(location: String? = null): String {
            return if (location != null) "forecast?location=$location" else "forecast"
        }
    }
    object Settings : Screen("settings", R.string.menu_settings, Icons.Rounded.Settings)
} 