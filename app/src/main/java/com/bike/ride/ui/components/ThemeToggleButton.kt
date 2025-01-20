package com.bike.ride.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.NightlightRound
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ThemeToggleButton(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.NightlightRound,
            contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode"
        )
    }
} 