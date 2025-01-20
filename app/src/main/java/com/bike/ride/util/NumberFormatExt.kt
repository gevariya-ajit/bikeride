package com.bike.ride.util

import kotlin.math.pow
import kotlin.math.round

fun Double.formatToDecimals(decimals: Int = 2): Double {
    val factor = 10.0.pow(decimals.toDouble())
    return round(this * factor) / factor
}

fun Double.formatTemperature(useCelsius: Boolean): String {
    val temp = if (useCelsius) this else (this * 9/5) + 32
    return "${temp.formatToDecimals()}${if (useCelsius) "°C" else "°F"}"
}

fun Double.formatWindSpeed(useKmh: Boolean): String {
    val speed = if (useKmh) this else this * 0.621371
    return "${speed.formatToDecimals()}${if (useKmh) " km/h" else " mph"}"
} 