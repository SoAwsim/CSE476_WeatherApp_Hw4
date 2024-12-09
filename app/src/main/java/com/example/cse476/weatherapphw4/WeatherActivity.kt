package com.example.cse476.weatherapphw4

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cse476.weatherapphw4.databinding.ActivityMainBinding
import com.example.cse476.weatherapphw4.extensions.capitalizeEveryWord
import com.example.cse476.weatherapphw4.extensions.toUIString
import com.example.cse476.weatherapphw4.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {
    private val model: WeatherViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(this.binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(this.binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.model.todayTemp.observe(this) { temp ->
            this.binding.todayWeatherTempTextView.text = temp.toUIString()
        }

        this.model.todayWeatherStatus.observe(this) { status ->
            this.binding.todayWeatherDescriptionTextView.text = status?.capitalizeEveryWord() ?: "NaN"
        }
    }
}