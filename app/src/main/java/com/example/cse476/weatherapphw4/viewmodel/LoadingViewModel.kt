package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cse476.weatherapphw4.BuildConfig
import com.example.cse476.weatherapphw4.models.network.NetworkMonitor
import com.example.cse476.weatherapphw4.models.repository.WeatherRepository
import com.example.cse476.weatherapphw4.models.response.WeatherApiResponse
import com.example.cse476.weatherapphw4.models.service.LocationService
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    application: Application,
    private val repository: WeatherRepository,
    private val locationService: LocationService
) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "LoadingViewModel"
        private const val WEATHER_API =
            "https://api.openweathermap.org/data/2.5/forecast?appid=" + BuildConfig.API_KEY
    }

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = this._isLoading

    private val _networkMonitor =
        NetworkMonitor(this.getApplication<Application>().applicationContext)
    val networkState: LiveData<Boolean> = this._networkMonitor.networkState

    init {
        this._networkMonitor.startMonitoringNetwork()
    }

    fun initializeDataFromLocation() {
        viewModelScope.launch {
            this@LoadingViewModel.getBestLocation()
            if (this@LoadingViewModel.locationService.location == null) {
                Log.e(TAG, "Location service failed!")
                return@launch
            }

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
                WEATHER_API + "&lat=" + this@LoadingViewModel.locationService.location?.latitude +
                        "&lon=" + this@LoadingViewModel.locationService.location?.longitude).openConnection()
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

    suspend fun getBestLocation() = withContext(Dispatchers.Main) {
        val context = this@LoadingViewModel.getApplication<Application>().applicationContext
        val gpsLocation = try {
            this@LoadingViewModel.locationService.getCurrentLocation(context, LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            Log.w(TAG, "Gps location failed", e)
            null
        }

        val networkLocation = try {
            this@LoadingViewModel.locationService.getCurrentLocation(context, LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            Log.w(TAG, "Network location failed", e)
            null
        }

        if (gpsLocation == null && networkLocation != null) {
            Log.d(TAG, "Gps location not found, using network location " +
                    "lat: ${networkLocation.latitude} lon: ${networkLocation.longitude}")
            this@LoadingViewModel.locationService.location = networkLocation
        } else if (gpsLocation != null && networkLocation == null) {
            Log.d(TAG, "Network location not found, using GPS location " +
                    "lat: ${gpsLocation.latitude} lon: ${gpsLocation.longitude}")
            this@LoadingViewModel.locationService.location = gpsLocation
        } else if (gpsLocation != null && networkLocation != null) {
            Log.d(TAG, "Both network and GPS provider returned values")
            if(networkLocation.accuracy > gpsLocation.accuracy) {
                Log.d(TAG, "Network location has higher accuracy " +
                        "lat: ${networkLocation.latitude} lon: ${networkLocation.longitude}")
                this@LoadingViewModel.locationService.location = networkLocation
            } else {
                Log.d(TAG, "GPS location has higher accuracy " +
                        "lat: ${gpsLocation.latitude} lon: ${gpsLocation.longitude}")
                this@LoadingViewModel.locationService.location = gpsLocation
            }
        } else {
            Log.e(TAG, "Location was not found!")
        }
    }
}