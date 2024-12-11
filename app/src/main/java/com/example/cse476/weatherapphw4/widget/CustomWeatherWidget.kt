package com.example.cse476.weatherapphw4.widget

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import com.example.cse476.weatherapphw4.R
import com.example.cse476.weatherapphw4.databinding.WeatherCardWidgetBinding
import com.example.cse476.weatherapphw4.enums.TempUnit
import com.example.cse476.weatherapphw4.extensions.kelvinToCelsius
import com.example.cse476.weatherapphw4.extensions.kelvinToFahrenheit
import com.example.cse476.weatherapphw4.extensions.toUIString
import com.google.android.material.card.MaterialCardView

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

    fun setHourText(hour: String) {
        this.binding.hourTextView.text = hour
    }

    fun setTemp(temp: Double, unit: TempUnit) {
        val tempUnit = when (unit) {
            TempUnit.Celsius -> "${temp.kelvinToCelsius().toUIString()} ℃"
            TempUnit.Fahrenheit -> "${temp.kelvinToFahrenheit().toUIString()} ℉"
            TempUnit.Kelvin -> "${temp.toUIString()} K"
        }
        this.binding.tempTextView.text = tempUnit
    }

    fun setImage(image: Bitmap?) {
        this.binding.weatherImageView.setImageBitmap(image)
    }
}