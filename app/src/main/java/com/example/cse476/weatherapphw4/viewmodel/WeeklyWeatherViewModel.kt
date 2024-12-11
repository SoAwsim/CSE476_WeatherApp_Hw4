package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cse476.weatherapphw4.extensions.capitalizeEveryWord
import com.example.cse476.weatherapphw4.models.ui.WeeklyWeatherInformation
import com.example.cse476.weatherapphw4.service.SettingsService
import com.example.cse476.weatherapphw4.service.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class WeeklyWeatherViewModel @Inject constructor(
    application: Application,
    private val weatherService: WeatherService,
    private val settingsService: SettingsService
) : AndroidViewModel(application) {
    private val _weeklyWeatherInformation = MutableLiveData<Map<Int, List<WeeklyWeatherInformation>>>(mapOf())
    val weeklyWeatherInformation: LiveData<Map<Int, List<WeeklyWeatherInformation>>> = this._weeklyWeatherInformation

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = this._isLoading

    val tempUnit = this.settingsService.tempUnit

    fun getWeeklyWeatherData() {
        this._isLoading.value = true
        viewModelScope.launch {
            val weeklyData = this@WeeklyWeatherViewModel.weatherService.weeklyWeatherMapByDate
            val calendar = Calendar.getInstance()

            val result: MutableMap<Int, List<WeeklyWeatherInformation>> = mutableMapOf()
            for (dayData in weeklyData) {
                val day = dayData.key
                val dayInformationList: MutableList<WeeklyWeatherInformation> = mutableListOf()
                for (hourData in dayData.value) {
                    calendar.timeInMillis = hourData.dt * 1000L
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val image = this@WeeklyWeatherViewModel.weatherService.iconMap[hourData.weather.firstOrNull()?.icon]
                    val temp = hourData.main.temp
                    val description = hourData.weather.firstOrNull()?.description
                    dayInformationList.add(WeeklyWeatherInformation(
                        "$hour - ${hour + 3}",
                        image,
                        temp,
                        description?.capitalizeEveryWord() ?: ""
                    ))
                }
                result.put(day, dayInformationList)
            }
            this@WeeklyWeatherViewModel._weeklyWeatherInformation.value = result
        }
    }

    fun finishLoading() {
        this._isLoading.value = false
    }
}