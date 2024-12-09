package com.example.cse476.weatherapphw4.models.ui

import android.graphics.Bitmap

data class WeeklyDataInformation(
    val day: String,
    val image: Bitmap?,
    val minTemp: Double,
    val maxTemp: Double,
    val weatherDescription: String
)