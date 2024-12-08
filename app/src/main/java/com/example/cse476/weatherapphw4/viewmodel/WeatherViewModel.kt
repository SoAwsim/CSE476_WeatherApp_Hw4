package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val _todayTemp = MutableLiveData<Double?>(null)
    val todayTemp : LiveData<Double?> = this._todayTemp

    private val _todayWeatherStatus = MutableLiveData<String?>(null)
    val todayWeatherStatus: LiveData<String?> = this._todayWeatherStatus

    private val _todayWeatherBitmapImage = MutableLiveData<Bitmap?>(null)
    val todayWeatherBitmapImage: LiveData<Bitmap?> = this._todayWeatherBitmapImage


}