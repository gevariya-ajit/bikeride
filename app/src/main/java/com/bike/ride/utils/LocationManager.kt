package com.bike.ride.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(forceRefresh: Boolean = false): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        return suspendCancellableCoroutine { continuation ->
            try {
                val priority = if (forceRefresh) {
                    Priority.PRIORITY_HIGH_ACCURACY
                } else {
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY
                }
                
                fusedLocationClient.getCurrentLocation(
                    priority,
                    object : CancellationToken() {
                        override fun onCanceledRequested(listener: OnTokenCanceledListener) =
                            CancellationTokenSource().token

                        override fun isCancellationRequested() = false
                    }
                ).addOnSuccessListener { location ->
                    continuation.resume(location)
                }.addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
            } catch (e: SecurityException) {
                continuation.resume(null)
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
} 