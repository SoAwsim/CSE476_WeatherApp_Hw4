package com.example.cse476.weatherapphw4.models.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class LocationService @Inject constructor() {
    var location: Location? = null

    suspend fun getCurrentLocation(context: Context, provider: String): Location? = suspendCancellableCoroutine { continuation ->
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            continuation.resumeWithException(Exception("Location permission missing"))
            return@suspendCancellableCoroutine
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(provider)) {
            continuation.resumeWithException(Exception("Provider not enabled"))
            return@suspendCancellableCoroutine
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            locationManager.getCurrentLocation(
                provider,
                null,
                context.mainExecutor
            ) { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    continuation.resumeWithException(Exception("Failed to get location"))
                }
            }
        } else {
            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    continuation.resume(location)
                    locationManager.removeUpdates(this)
                }

                override fun onProviderDisabled(provider: String) {
                    super.onProviderDisabled(provider)
                    if (!locationManager.isProviderEnabled(provider)) {
                        continuation.resumeWithException(Exception("Provider got disabled"))
                        locationManager.removeUpdates(this)
                    }
                }
            }

            locationManager.requestSingleUpdate(
                provider,
                locationListener,
                null
            )
        }
    }
}