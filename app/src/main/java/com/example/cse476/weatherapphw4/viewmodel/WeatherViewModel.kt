package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.icu.util.Calendar
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cse476.weatherapphw4.extensions.capitalizeEveryWord
import com.example.cse476.weatherapphw4.extensions.convertFromCalendarEnum
import com.example.cse476.weatherapphw4.models.service.LocationService
import com.example.cse476.weatherapphw4.models.service.WeatherService
import com.example.cse476.weatherapphw4.models.ui.WeeklyDataInformation
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

    private val _weeklyWeatherInformation = MutableLiveData<List<WeeklyDataInformation>>()
    val weeklyDataInformation: LiveData<List<WeeklyDataInformation>> = this._weeklyWeatherInformation

    init {
        this.transformWeatherData()
    }

    fun transformWeatherData() {
        val weatherData = this.weatherService.weatherMapByDate
        val calendar = Calendar.getInstance()
        var weatherCurrentDay = calendar.get(Calendar.DAY_OF_WEEK)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        var currentWeather = weatherData[weatherCurrentDay]
        if (currentWeather == null) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            weatherCurrentDay = calendar.get(Calendar.DAY_OF_WEEK)
            currentWeather = weatherData[weatherCurrentDay] ?: throw Exception("Illegal state")
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        val currentTimeWeather = currentWeather.firstOrNull {
            val listCalendar = Calendar.getInstance()
            listCalendar.timeInMillis = it.dt * 1000L
            currentHour >= listCalendar.get(Calendar.HOUR_OF_DAY)
        } ?: currentWeather.first()

        this._todayTemp.value = currentTimeWeather.main.temp
        this._todayWeatherStatus.value = currentTimeWeather.weather.firstOrNull()?.description
        this._todayWeatherBitmapImage.value = this.weatherService
            .iconMap[currentTimeWeather.weather.first().icon]

        val weatherInformationList: MutableList<WeeklyDataInformation> = mutableListOf()
        for (dayInformation in weatherData) {
            val weatherEntries = dayInformation.value
            val minTemp = weatherEntries.map { it.main.temp_min }.min()
            val maxTemp = weatherEntries.map { it.main.temp_max }.max()
            val weatherDescription = weatherEntries.map { it.weather.first().description }
                .first().capitalizeEveryWord()
            val day = dayInformation.key.convertFromCalendarEnum()
            val image = this.weatherService
                .iconMap[weatherEntries.map { it.weather.first().icon }.first()]
            val weatherInfo = WeeklyDataInformation(
                day,
                image,
                minTemp,
                maxTemp,
                weatherDescription
            )
            weatherInformationList.add(weatherInfo)
        }
        this._weeklyWeatherInformation.value = weatherInformationList
    }
}