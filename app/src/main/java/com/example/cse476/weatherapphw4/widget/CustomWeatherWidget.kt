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

    fun setDayText(day: String) {
        this.binding.dayTextView.text = day;
    }

    fun setTemp(minTemp: Double, maxTemp: Double, unit: TempUnit) {
        val minUnitTemp: Double
        val maxUnitTemp: Double
        when (unit){
            TempUnit.Celsius -> {
                minUnitTemp = minTemp.kelvinToCelsius()
                maxUnitTemp = maxTemp.kelvinToCelsius()
            }
            TempUnit.Fahrenheit -> {
                minUnitTemp = minTemp.kelvinToFahrenheit()
                maxUnitTemp = maxTemp.kelvinToFahrenheit()
            }
            TempUnit.Kelvin -> {
                minUnitTemp = minTemp
                maxUnitTemp = maxTemp
            }
        }
        this.binding.minTempTextView.text = minUnitTemp.toUIString()
        this.binding.maxTempTextView.text = maxUnitTemp.toUIString()
    }

    fun setImage(image: Bitmap?) {
        this.binding.weatherImageView.setImageBitmap(image)
    }
}