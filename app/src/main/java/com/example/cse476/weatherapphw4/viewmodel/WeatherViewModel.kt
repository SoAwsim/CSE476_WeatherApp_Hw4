package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import android.graphics.Bitmap
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
    weatherService: WeatherService,
    locationService: LocationService
) : AndroidViewModel(application) {
    companion object {
        const val TAG = "WeatherViewModel"
    }

    private val _todayTemp = MutableLiveData<Double?>(null)
    val todayTemp : LiveData<Double?> = this._todayTemp

    private val _todayWeatherStatus = MutableLiveData<String?>(null)
    val todayWeatherStatus: LiveData<String?> = this._todayWeatherStatus

    private val _todayWeatherBitmapImage = MutableLiveData<Bitmap?>(null)
    val todayWeatherBitmapImage: LiveData<Bitmap?> = this._todayWeatherBitmapImage
}