package com.example.cse476.weatherapphw4.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.example.cse476.weatherapphw4.databinding.WeatherCardWidgetBinding
import java.util.Locale

class CustomWeatherWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    private val binding: WeatherCardWidgetBinding =
        WeatherCardWidgetBinding.inflate(LayoutInflater.from(context), this, true)

    fun setWeatherText(weather: String) {
        this.binding.weatherStatusTextView.text = weather
    }

    fun setDayText(day: String) {
        this.binding.dayTextView.text = day;
    }

    fun setTemp(minTemp: Double, maxTemp: Double) {
        this.binding.minTempTextView.text = String.format(Locale.US, "%.1f", minTemp)
        this.binding.maxTempTextView.text = String.format(Locale.US, "%.1f", maxTemp)
    }
}