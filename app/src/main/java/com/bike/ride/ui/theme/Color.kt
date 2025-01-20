package com.bike.ride.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Weather score colors
val ScorePoorColor = Color(0xFFFF6B6B)      // Red
val ScoreMediumColor = Color(0xFFFFD93D)     // Yellow
val ScoreGoodColor = Color(0xFF4CAF50)       // Green

fun getScoreColor(score: Int): Color {
    return when (score) {
        in 0..49 -> ScorePoorColor
        in 50..79 -> ScoreMediumColor
        else -> ScoreGoodColor
    }
}

val LightBackground = Color(0xFFF5F5F5)
val LightSurface = Color(0xFFFFFFFF)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF242424)