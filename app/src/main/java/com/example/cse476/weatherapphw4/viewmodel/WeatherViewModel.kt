package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cse476.weatherapphw4.models.response.CurrentWeatherApiResponse
import com.example.cse476.weatherapphw4.service.SettingsService
import com.example.cse476.weatherapphw4.service.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    application: Application,
    private val weatherService: WeatherService,
    private val settingsService: SettingsService
) : AndroidViewModel(application) {
    private val _todayTemp = MutableLiveData<Double?>(null)
    val todayTemp : LiveData<Double?> = this._todayTemp

    private val _todayWeatherStatus = MutableLiveData<String?>()
    val todayWeatherStatus: LiveData<String?> = this._todayWeatherStatus

    private val _todayWeatherBitmapImage = MutableLiveData<Bitmap?>(null)
    val todayWeatherBitmapImage: LiveData<Bitmap?> = this._todayWeatherBitmapImage

    private val _minTemp = MutableLiveData<Double?>(null)
    val minTemp : LiveData<Double?> = this._minTemp

    private val _maxTemp = MutableLiveData<Double?>(null)
    val maxTemp : LiveData<Double?> = this._maxTemp

    private val _feelsLikeTemp = MutableLiveData<Double?>(null)
    val feelsLikeTemp : LiveData<Double?> = this._feelsLikeTemp

    private val _humidity = MutableLiveData<Double?>(null)
    val humidity : LiveData<Double?> = this._humidity

    private val _windSpeed = MutableLiveData<Double?>(null)
    val windSpeed : LiveData<Double?> = this._windSpeed

    private val _windDegree = MutableLiveData<Double?>(null)
    val windDegree : LiveData<Double?> = this._windDegree

    private val _windGust = MutableLiveData<Double?>(null)
    val windGust : LiveData<Double?> = this._windGust

    private val _pressure = MutableLiveData<Double?>(null)
    val pressure : LiveData<Double?> = this._pressure

    private val _city = MutableLiveData<String?>(null)
    val city : LiveData<String?> = this._city

    val tempUnit = this.settingsService.tempUnit
    val currentWeatherData = this.weatherService.currentWeather

    fun setCurrentWeatherData(weatherData: CurrentWeatherApiResponse?) {
        this._todayTemp.value = weatherData?.main?.temp
        this._todayWeatherStatus.value = weatherData?.weather?.first()?.description
        this._todayWeatherBitmapImage.value = this.weatherService
            .iconMap[weatherData?.weather?.first()?.icon]
        this._minTemp.value = weatherData?.main?.temp_min
        this._maxTemp.value = weatherData?.main?.temp_max
        this._feelsLikeTemp.value = weatherData?.main?.feels_like
        this._humidity.value = weatherData?.main?.humidity
        this._windSpeed.value = weatherData?.wind?.speed
        this._windDegree.value = weatherData?.wind?.deg
        this._windGust.value = weatherData?.wind?.gust
        this._pressure.value = weatherData?.main?.pressure
        this._city.value = weatherData?.name
    }

    fun triggerTempUpdate() {
        this._todayTemp.value = this._todayTemp.value
        this._minTemp.value = this._minTemp.value
        this._maxTemp.value = this._maxTemp.value
        this._feelsLikeTemp.value = this._feelsLikeTemp.value
    }
}