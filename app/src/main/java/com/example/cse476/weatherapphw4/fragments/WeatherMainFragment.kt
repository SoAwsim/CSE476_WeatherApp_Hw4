package com.example.cse476.weatherapphw4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cse476.weatherapphw4.databinding.FragmentWeatherMainBinding
import com.example.cse476.weatherapphw4.extensions.capitalizeEveryWord
import com.example.cse476.weatherapphw4.extensions.toUIString
import com.example.cse476.weatherapphw4.viewmodel.WeatherViewModel
import com.example.cse476.weatherapphw4.widget.CustomWeatherWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherMainFragment : Fragment() {
    private val model: WeatherViewModel by viewModels()
    private var binding: FragmentWeatherMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentWeatherMainBinding.inflate(this.layoutInflater)

        this.model.todayTemp.observe(viewLifecycleOwner) { temp ->
            this.binding?.todayWeatherTempTextView?.text = temp.toUIString()
        }

        this.model.todayWeatherStatus.observe(viewLifecycleOwner) { status ->
            this.binding?.todayWeatherDescriptionTextView?.text = status?.capitalizeEveryWord() ?: "NaN"
        }

        this.model.todayWeatherBitmapImage.observe(viewLifecycleOwner) { image ->
            this.binding?.todayWeatherImageView?.setImageBitmap(image)
        }

        this.model.weeklyDataInformation.observe(viewLifecycleOwner) { weeklyData ->
            this.binding?.weatherWidgetContainer?.removeAllViews()
            for (data in weeklyData) {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(5, 5, 5, 5)
                val weatherWidget = CustomWeatherWidget(this.requireContext(), null)
                weatherWidget.layoutParams = params
                weatherWidget.setDayText(data.day)
                weatherWidget.setTemp(data.minTemp, data.maxTemp)
                weatherWidget.setWeatherText(data.weatherDescription)
                weatherWidget.setImage(data.image)
                this.binding?.weatherWidgetContainer?.addView(weatherWidget)
            }
        }

        return this.binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }
}