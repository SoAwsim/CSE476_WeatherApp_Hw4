package com.example.cse476.weatherapphw4.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cse476.weatherapphw4.databinding.FragmentWeatherMainBinding
import com.example.cse476.weatherapphw4.enums.TempUnit
import com.example.cse476.weatherapphw4.extensions.capitalizeEveryWord
import com.example.cse476.weatherapphw4.extensions.kelvinToCelsius
import com.example.cse476.weatherapphw4.extensions.kelvinToFahrenheit
import com.example.cse476.weatherapphw4.extensions.toUIString
import com.example.cse476.weatherapphw4.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherMainFragment : Fragment() {
    private val model: WeatherViewModel by viewModels()
    private var binding: FragmentWeatherMainBinding? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWeatherMainBinding.inflate(this.layoutInflater)
        this.binding = binding

        this.model.todayTemp.observe(viewLifecycleOwner) { temp ->
            val unit = this.model.tempUnit.value ?: TempUnit.Celsius
            val tempWithUnit: String? = when (unit) {
                TempUnit.Celsius -> "${temp?.kelvinToCelsius().toUIString()} ℃"
                TempUnit.Fahrenheit -> "${temp?.kelvinToFahrenheit().toUIString()} ℉"
                TempUnit.Kelvin -> "${temp.toUIString()} K"
            }
            binding.todayWeatherTempTextView.text = tempWithUnit ?: "NaN"
        }

        this.model.todayWeatherStatus.observe(viewLifecycleOwner) { status ->
            binding.todayWeatherDescriptionTextView.text = status?.capitalizeEveryWord() ?: "NaN"
        }

        this.model.todayWeatherBitmapImage.observe(viewLifecycleOwner) { image ->
            binding.todayWeatherImageView.setImageBitmap(image)
        }

        this.model.minTemp.observe(viewLifecycleOwner) { temp ->
            val unit = this.model.tempUnit.value ?: TempUnit.Celsius
            val tempWithUnit: String? = when (unit) {
                TempUnit.Celsius -> "${temp?.kelvinToCelsius().toUIString()} ℃"
                TempUnit.Fahrenheit -> "${temp?.kelvinToFahrenheit().toUIString()} ℉"
                TempUnit.Kelvin -> "${temp.toUIString()} K"
            }
            binding.tempMinTextView.text = "Min Temp: ${tempWithUnit ?: "NaN"}"
        }

        this.model.maxTemp.observe(viewLifecycleOwner) { temp ->
            val unit = this.model.tempUnit.value ?: TempUnit.Celsius
            val tempWithUnit: String? = when (unit) {
                TempUnit.Celsius -> "${temp?.kelvinToCelsius().toUIString()} ℃"
                TempUnit.Fahrenheit -> "${temp?.kelvinToFahrenheit().toUIString()} ℉"
                TempUnit.Kelvin -> "${temp.toUIString()} K"
            }
            binding.tempMaxTextView.text = "Max Temp: ${tempWithUnit ?: "NaN"}"
        }

        this.model.feelsLikeTemp.observe(viewLifecycleOwner) { temp ->
            val unit = this.model.tempUnit.value ?: TempUnit.Celsius
            val tempWithUnit: String? = when (unit) {
                TempUnit.Celsius -> "${temp?.kelvinToCelsius().toUIString()} ℃"
                TempUnit.Fahrenheit -> "${temp?.kelvinToFahrenheit().toUIString()} ℉"
                TempUnit.Kelvin -> "${temp.toUIString()} K"
            }
            binding.feelsLikeTextView.text = "Feels Like: ${tempWithUnit ?: "NaN"}"
        }

        this.model.humidity.observe(viewLifecycleOwner) { humidity ->
            binding.humidityTextView.text = "Humidity: ${"${humidity?.toUIString() ?: "NaN"}%"}"
        }

        this.model.windSpeed.observe(viewLifecycleOwner) { speed ->
            binding.windSpeedTextView.text = "Wind Speed: ${"${speed?.toUIString() ?: "NaN"} m/s"}"
        }

        this.model.windDegree.observe(viewLifecycleOwner) { deg ->
            binding.windDegreeTextView.text = "Wind Degree: ${deg?.toUIString() ?: "NaN"}"
        }

        this.model.windGust.observe(viewLifecycleOwner) { gust ->
            binding.windGustTextView.text = "Wind Gust: ${"${gust?.toUIString() ?: "NaN"} m/s"}"
        }

        this.model.pressure.observe(viewLifecycleOwner) { pressure ->
            binding.pressureTextView.text = "Pressure: ${"${pressure?.toUIString() ?: "NaN"} hPa"}"
        }

//        this.model.weeklyDataInformation.observe(viewLifecycleOwner) { weeklyData ->
//            this.binding?.weatherWidgetContainer?.removeAllViews()
//            for (data in weeklyData) {
//                val params = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                params.setMargins(5, 5, 5, 5)
//                val weatherWidget = CustomWeatherWidget(this.requireContext(), null)
//                weatherWidget.layoutParams = params
//                weatherWidget.setDayText(data.day)
//                weatherWidget.setTemp(
//                    data.minTemp,
//                    data.maxTemp,
//                    this.model.tempUnit.value ?: TempUnit.Celsius
//                )
//                weatherWidget.setWeatherText(data.weatherDescription)
//                weatherWidget.setImage(data.image)
//                this.binding?.weatherWidgetContainer?.addView(weatherWidget)
//            }
//        }

        this.model.tempUnit.observe(viewLifecycleOwner) {
            this.model.triggerTempUpdate()
        }

        return this.binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }
}