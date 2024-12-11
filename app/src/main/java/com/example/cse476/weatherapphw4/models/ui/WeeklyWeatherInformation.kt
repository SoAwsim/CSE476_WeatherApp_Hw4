package com.example.cse476.weatherapphw4.models.ui

import android.graphics.Bitmap

data class WeeklyWeatherInformation(
    val hours: String,
    val image: Bitmap?,
    val temp: Double,
    val weatherDescription: String
)