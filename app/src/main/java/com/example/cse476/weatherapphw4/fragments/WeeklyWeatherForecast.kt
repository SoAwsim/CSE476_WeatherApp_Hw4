package com.example.cse476.weatherapphw4.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cse476.weatherapphw4.R
import com.example.cse476.weatherapphw4.databinding.FragmentWeeklyWeatherForecastBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeeklyWeatherForecast : Fragment() {
    private var binding: FragmentWeeklyWeatherForecastBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentWeeklyWeatherForecastBinding.inflate(this.layoutInflater)
        this.binding = binding


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }
}