package com.example.cse476.weatherapphw4

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.cse476.weatherapphw4.databinding.WeatherMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: WeatherMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.binding = WeatherMainBinding.inflate(this.layoutInflater)
        setContentView(this.binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(this.binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHostFragment.navController)
    }
}