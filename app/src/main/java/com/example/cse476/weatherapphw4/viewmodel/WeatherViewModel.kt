package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cse476.weatherapphw4.models.service.LocationService
import com.example.cse476.weatherapphw4.models.service.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    application: Application,
    private val weatherService: WeatherService,
    private val locationService: LocationService
) : AndroidViewModel(application) {
    companion object {
        const val TAG = "WeatherViewModel"
    }

    private val _todayTemp = MutableLiveData<Double?>(null)
    val todayTemp : LiveData<Double?> = this._todayTemp

    private val _todayWeatherStatus = MutableLiveData<String?>()
    val todayWeatherStatus: LiveData<String?> = this._todayWeatherStatus

    private val _todayWeatherBitmapImage = MutableLiveData<Bitmap?>(null)
    val todayWeatherBitmapImage: LiveData<Bitmap?> = this._todayWeatherBitmapImage

    init {
        this.transformWeatherData()
    }

    fun transformWeatherData() {
        val weatherData = this.weatherService.weatherMapByDate
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_WEEK)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.get(Calendar.DAY_OF_WEEK)
        var usedTomorrow = false

        var currentWeather = weatherData[today]
        if (currentWeather == null) {
            usedTomorrow = true
            currentWeather = weatherData[tomorrow] ?: throw Exception("Illegal state")
        }
        val currentTimeWeather = if (usedTomorrow) {
            currentWeather.first()
        } else {
            currentWeather.firstOrNull {
                val listCalendar = Calendar.getInstance()
                listCalendar.timeInMillis = it.dt * 1000L
                currentHour >= listCalendar.get(Calendar.HOUR_OF_DAY)
            } ?: currentWeather.first()
        }

        this._todayTemp.value = currentTimeWeather.main.temp
        this._todayWeatherStatus.value = currentTimeWeather.weather.firstOrNull()?.description
    }
}