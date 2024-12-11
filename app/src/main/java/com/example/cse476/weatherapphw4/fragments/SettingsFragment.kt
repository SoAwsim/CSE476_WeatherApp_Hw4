package com.example.cse476.weatherapphw4.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cse476.weatherapphw4.R
import com.example.cse476.weatherapphw4.databinding.FragmentSettingsBinding
import com.example.cse476.weatherapphw4.enums.LocationType
import com.example.cse476.weatherapphw4.enums.TempUnit
import com.example.cse476.weatherapphw4.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var binding: FragmentSettingsBinding? = null
    private val model: SettingsViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSettingsBinding.inflate(this.layoutInflater)
        this.binding = binding
        binding.tempRadioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.celsiusButton -> this.model.updateTempUnit(TempUnit.Celsius)
                R.id.fahrenheitButton -> this.model.updateTempUnit(TempUnit.Fahrenheit)
                R.id.kelvinButton -> this.model.updateTempUnit(TempUnit.Kelvin)
            }
        }

        binding.locationRadioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.currentLocationButton -> {
                    this.locationProvided()
                    this.model.changeLocationType(LocationType.Current)
                }
                R.id.citySelectButton -> this.model.changeLocationType(LocationType.City)
            }
        }

        this.model.tempUnit.observe(viewLifecycleOwner) { unit ->
            when (unit) {
                TempUnit.Celsius -> binding.tempRadioGroup.check(R.id.celsiusButton)
                TempUnit.Fahrenheit -> binding.tempRadioGroup.check(R.id.fahrenheitButton)
                TempUnit.Kelvin -> binding.tempRadioGroup.check(R.id.kelvinButton)
            }
        }

        this.model.locationStatus.observe(viewLifecycleOwner) { locationStatus ->
            when (locationStatus) {
                LocationType.Current -> {
                    binding.locationRadioGroup.check(R.id.currentLocationButton)
                    binding.enterCityField.visibility = View.GONE
                    binding.cityName.visibility = View.GONE
                    binding.lon.visibility = View.GONE
                    binding.lat.visibility = View.GONE
                    binding.country.visibility = View.GONE
                }
                LocationType.City -> {
                    binding.locationRadioGroup.check(R.id.citySelectButton)
                    binding.enterCityField.visibility = View.VISIBLE
                    binding.cityName.visibility = View.VISIBLE
                    binding.lon.visibility = View.VISIBLE
                    binding.lat.visibility = View.VISIBLE
                    binding.country.visibility = View.VISIBLE
                }
            }
        }

        this.model.city.observe(viewLifecycleOwner) { city ->
            binding.cityName.text = "City: ${city?.name}"
            binding.lon.text = "Lon: ${city?.lon}"
            binding.lat.text = "Lat: ${city?.lat}"
            binding.country.text = "Country: ${city?.country}"
        }

        binding.enterCityField.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val inputText = binding.enterCityField.text.toString()
                this.model.getCityAndSaveInformation(inputText)

                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.enterCityField.windowToken, 0)
                true
            } else {
                false
            }
        }

        return binding.root
    }

    private fun locationProvided() {
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) {
                Toast.makeText(this.requireContext(), "Location Rejected!", Toast.LENGTH_SHORT).show()
                this.binding?.locationRadioGroup?.check(R.id.citySelectButton)
            }
        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }
}