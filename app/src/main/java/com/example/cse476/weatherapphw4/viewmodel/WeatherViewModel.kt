package com.example.cse476.weatherapphw4.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "WeatherViewModel"
    }

    private var lastLocation: Location? = null

    private val _todayTemp = MutableLiveData<Double?>(null)
    val todayTemp : LiveData<Double?> = this._todayTemp

    private val _todayWeatherStatus = MutableLiveData<String?>(null)
    val todayWeatherStatus: LiveData<String?> = this._todayWeatherStatus

    private val _todayWeatherBitmapImage = MutableLiveData<Bitmap?>(null)
    val todayWeatherBitmapImage: LiveData<Bitmap?> = this._todayWeatherBitmapImage

    fun startLocationService() {
        val context = this.getApplication<Application>().applicationContext
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (gpsLocation == null && networkLocation != null) {
            Log.d(TAG, "Gps location not found, using network location " +
                    "lat: ${networkLocation.latitude} lon: ${networkLocation.longitude}")
            lastLocation = networkLocation
        } else if (gpsLocation != null && networkLocation == null) {
            Log.d(TAG, "Network location not found, using GPS location " +
                    "lat: ${gpsLocation.latitude} lon: ${gpsLocation.longitude}")
            lastLocation = gpsLocation
        } else if (gpsLocation != null && networkLocation != null) {
            Log.d(TAG, "Both network and GPS provider returned values")
            if(networkLocation.accuracy > gpsLocation.accuracy) {
                Log.d(TAG, "Network location has higher accuracy " +
                        "lat: ${networkLocation.latitude} lon: ${networkLocation.longitude}")
                lastLocation = networkLocation
            } else {
                Log.d(TAG, "GPS location has higher accuracy " +
                        "lat: ${gpsLocation.latitude} lon: ${gpsLocation.longitude}")
                lastLocation = gpsLocation
            }
        } else {
            Log.e(TAG, "Location was not found!")
        }
    }
}