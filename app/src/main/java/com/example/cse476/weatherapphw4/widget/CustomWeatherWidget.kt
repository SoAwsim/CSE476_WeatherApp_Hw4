package com.example.cse476.weatherapphw4.widget

import android.content.Context
import android.util.AttributeSet
import com.example.cse476.weatherapphw4.R
import com.example.cse476.weatherapphw4.databinding.WeatherCardWidgetBinding
import com.google.android.material.card.MaterialCardView
import java.util.Locale

class CustomWeatherWidget(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {
    private val binding: WeatherCardWidgetBinding

    init {
        inflate(context, R.layout.weather_card_widget, this)
        this.binding = WeatherCardWidgetBinding.bind(this)
    }

    fun setWeatherText(weather: String) {
        this.binding.weatherDescriptionTextView.text = weather
    }

    fun setDayText(day: String) {
        this.binding.dayTextView.text = day;
    }

    fun setTemp(minTemp: Double, maxTemp: Double) {
        this.binding.minTempTextView.text = String.format(Locale.US, "%.1f", minTemp)
        this.binding.maxTempTextView.text = String.format(Locale.US, "%.1f", maxTemp)
    }
}