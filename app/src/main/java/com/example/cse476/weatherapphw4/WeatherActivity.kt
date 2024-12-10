package com.example.cse476.weatherapphw4

import android.os.Bundle
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cse476.weatherapphw4.databinding.ActivityMainBinding
import com.example.cse476.weatherapphw4.extensions.capitalizeEveryWord
import com.example.cse476.weatherapphw4.extensions.toUIString
import com.example.cse476.weatherapphw4.viewmodel.WeatherViewModel
import com.example.cse476.weatherapphw4.widget.CustomWeatherWidget
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

        this.model.todayWeatherBitmapImage.observe(this) { image ->
            this.binding.todayWeatherImageView.setImageBitmap(image)
        }

        this.model.weeklyDataInformation.observe(this) { weeklyData ->
            this.binding.weatherWidgetContainer.removeAllViews()
            for (data in weeklyData) {
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(5, 5, 5, 5)
                val weatherWidget = CustomWeatherWidget(this, null)
                weatherWidget.layoutParams = params
                weatherWidget.setDayText(data.day)
                weatherWidget.setTemp(data.minTemp, data.maxTemp)
                weatherWidget.setWeatherText(data.weatherDescription)
                weatherWidget.setImage(data.image)
                this.binding.weatherWidgetContainer.addView(weatherWidget)
            }
        }
    }
}