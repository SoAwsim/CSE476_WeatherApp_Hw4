package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cse476.weatherapphw4.enums.LocationType
import com.example.cse476.weatherapphw4.enums.TempUnit
import com.example.cse476.weatherapphw4.service.SettingsService
import com.example.cse476.weatherapphw4.service.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val settingsService: SettingsService,
    private val weatherService: WeatherService
) : AndroidViewModel(application) {
    val tempUnit = this.settingsService.tempUnit
    val locationStatus = this.settingsService.locationType
    val city = this.settingsService.cityInformation

    fun updateTempUnit(unit: TempUnit) {
        this.settingsService.changeTemp(unit)
    }

    fun changeLocationType(locationType: LocationType) {
        this.settingsService.changeLocationType(locationType)
    }

    fun getCityAndSaveInformation(city: String) {
        viewModelScope.launch {
            val result = this@SettingsViewModel.weatherService.getCity(city)?.firstOrNull()
            if (result == null)
                return@launch

            val location = Location("")
            location.latitude = result.lat
            location.longitude = result.lon
            val context = this@SettingsViewModel.getApplication<Application>().applicationContext
            this@SettingsViewModel.settingsService.saveCityInformation(result)
            val task = this@SettingsViewModel.weatherService.fetchCurrentWeatherDataFromApi(
                location,
                context,
                CoroutineScope(Dispatchers.IO)
            )
            this@SettingsViewModel.weatherService.fetchWeeklyWeatherDataFromApi(
                location,
                context,
                CoroutineScope(Dispatchers.IO)
            )
            task.await()
            this@SettingsViewModel.weatherService.awaitWeeklyTask()
        }
    }
}