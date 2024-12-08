package com.example.cse476.weatherapphw4

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cse476.weatherapphw4.databinding.LoadingScreenBinding
import com.example.cse476.weatherapphw4.viewmodel.LoadingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val model: LoadingViewModel by viewModels()
    private lateinit var binding: LoadingScreenBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.binding = LoadingScreenBinding.inflate(this.layoutInflater)
        setContentView(R.layout.loading_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loading)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.model.networkState.observe(this) { state ->
            this.binding.progressStatus.text = "Connected fetching data from API"
            this.model.initializeData()
        }

        this.model.isLoading.observe(this) { loading ->
            val intent = Intent(this, WeatherActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }
    }
}