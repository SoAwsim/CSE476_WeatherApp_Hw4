package com.example.cse476.weatherapphw4

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cse476.weatherapphw4.databinding.LoadingScreenBinding
import com.example.cse476.weatherapphw4.viewmodel.LoadingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val LOCATION_REQUEST_CODE = 100
    }

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
            if (loading) {
                return@observe
            }
            val intent = Intent(this, WeatherActivity::class.java)
            this.startActivity(intent)
            this.finish()
        }
    }

    private fun locationProvided() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.model.startLocationService()
            return
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            this.locationProvided()
        } else {
            Toast.makeText(this, "Location not allowed", Toast.LENGTH_SHORT).show()
        }
    }
}