package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cse476.weatherapphw4.enums.LocationType
import com.example.cse476.weatherapphw4.network.NetworkMonitor
import com.example.cse476.weatherapphw4.service.LocationService
import com.example.cse476.weatherapphw4.service.SettingsService
import com.example.cse476.weatherapphw4.service.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    application: Application,
    private val weatherService: WeatherService,
    private val locationService: LocationService,
    private val settingsService: SettingsService
) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "LoadingViewModel"
    }

    private val networkMonitor: NetworkMonitor

    init {
        val context = this.getApplication<Application>().applicationContext
        this.networkMonitor = NetworkMonitor(context)
        this.networkMonitor.startMonitoringNetwork()
        this.settingsService.initializeSettings(context)
    }

    val networkState = this.networkMonitor.networkState

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = this._isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = this._errorMessage

    private val initializeSemaphore = Semaphore(1)

    fun initializeDataFromLocation() {
        viewModelScope.launch { initializeSemaphore.withPermit {
            if (this@LoadingViewModel._isLoading.value == false || this@LoadingViewModel._errorMessage.value != null)
                return@withPermit

            val context = this@LoadingViewModel.getApplication<Application>().applicationContext
            this@LoadingViewModel.getBestLocation(context)
            if (this@LoadingViewModel.locationService.location == null) {
                Log.e(TAG, "Location service failed!")
                this@LoadingViewModel._errorMessage.value = "Location fetching failed!"
                return@withPermit
            }

            this@LoadingViewModel.weatherService.fetchWeeklyWeatherDataFromApi(
                this@LoadingViewModel.locationService.location,
                context,
                CoroutineScope(Dispatchers.IO)
            )
            val currentWeatherTask = this@LoadingViewModel.weatherService.fetchCurrentWeatherDataFromApi(
                this@LoadingViewModel.locationService.location,
                context,
                CoroutineScope(Dispatchers.IO)
            )
            currentWeatherTask.await()
            if (this@LoadingViewModel.weatherService.currentWeather.value == null) {
                this@LoadingViewModel._errorMessage.value = "Failed to fetch data from api!"
                return@withPermit
            }
            this@LoadingViewModel._isLoading.value = false
        } }
    }

    suspend fun getBestLocation(context: Context) = withContext(Dispatchers.Main) {
        if (this@LoadingViewModel.settingsService.locationType.value == LocationType.City) {
            this@LoadingViewModel.locationService.location = this@LoadingViewModel.settingsService.cityLocation.value
            return@withContext
        }

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

    fun clearErrorMessage() {
        this._errorMessage.value = null
    }
}