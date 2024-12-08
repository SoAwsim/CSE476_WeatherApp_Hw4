package com.example.cse476.weatherapphw4.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cse476.weatherapphw4.BuildConfig
import com.example.cse476.weatherapphw4.models.network.NetworkMonitor
import com.example.cse476.weatherapphw4.models.response.WeatherApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class LoadingViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "LoadingViewModel"
        private const val WEATHER_API =
            "https://api.openweathermap.org/data/2.5/forecast?appid=" + BuildConfig.API_KEY
    }

    private var lastLocation: Location? = null

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = this._isLoading

    private val _networkMonitor =
        NetworkMonitor(this.getApplication<Application>().applicationContext)
    val networkState: LiveData<Boolean> = this._networkMonitor.networkState

    init {
        this._networkMonitor.startMonitoringNetwork()
    }

    fun initializeData() {
        this.startLocationService()
        if (this.lastLocation == null) {
            Log.e(TAG, "Location service failed!")
            return
        }

        viewModelScope.launch {
            val response = this@LoadingViewModel.fetchApiResponse()
            if (response == null)
                return@launch
            this@LoadingViewModel._isLoading.value = false
        }
    }

    private suspend fun fetchApiResponse(): WeatherApiResponse? = withContext(Dispatchers.IO) {
        var reader: BufferedReader? = null
        try {
            val connection = URL(
                WEATHER_API + "&lat=" + this@LoadingViewModel.lastLocation?.latitude +
                        "&lon=" + this@LoadingViewModel.lastLocation?.longitude).openConnection()
            connection.connect()
            reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            val gson = Gson()
            return@withContext gson.fromJson(response.toString(), WeatherApiResponse::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get response from API", e)
        } finally {
            reader?.close()
        }
        return@withContext null
    }

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