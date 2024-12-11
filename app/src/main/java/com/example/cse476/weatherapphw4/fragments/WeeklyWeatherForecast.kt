package com.example.cse476.weatherapphw4.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cse476.weatherapphw4.databinding.FragmentWeeklyWeatherForecastBinding
import com.example.cse476.weatherapphw4.enums.TempUnit
import com.example.cse476.weatherapphw4.extensions.convertFromCalendarEnum
import com.example.cse476.weatherapphw4.viewmodel.WeeklyWeatherViewModel
import com.example.cse476.weatherapphw4.widget.CustomWeatherWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeeklyWeatherForecast : Fragment() {
    val model: WeeklyWeatherViewModel by viewModels()
    private var binding: FragmentWeeklyWeatherForecastBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWeeklyWeatherForecastBinding.inflate(this.layoutInflater)
        this.binding = binding

        this.model.weeklyWeatherInformation.observe(viewLifecycleOwner) { weeklyData ->
            if (weeklyData.isEmpty()) {
                this.model.getWeeklyWeatherData()
                return@observe
            }

            binding.weatherWidgetContainer.removeAllViews()
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(5, 5, 5, 5)
            val context = this.requireContext()
            for (dayData in weeklyData) {
                val dayTextView = TextView(context)
                dayTextView.layoutParams = params
                dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
                dayTextView.text = dayData.key.convertFromCalendarEnum()
                binding.weatherWidgetContainer.addView(dayTextView)
                for (hourData in dayData.value) {
                    val weatherWidget = CustomWeatherWidget(context, null)
                    weatherWidget.layoutParams = params
                    weatherWidget.setHourText(hourData.hours)
                    weatherWidget.setTemp(
                        hourData.temp,
                        this.model.tempUnit.value ?: TempUnit.Celsius
                    )
                    weatherWidget.setImage(hourData.image)
                    weatherWidget.setWeatherText(hourData.weatherDescription)
                    binding.weatherWidgetContainer.addView(weatherWidget)
                }
            }
            this.model.finishLoading()
        }

        this.model.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.weeklyView.visibility = if (loading) View.GONE else View.VISIBLE
            binding.weeklyLoadingProgress.visibility = if (loading) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }
}