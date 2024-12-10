package com.example.cse476.weatherapphw4.fragments

import com.example.cse476.weatherapphw4.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cse476.weatherapphw4.databinding.FragmentSettingsBinding
import com.example.cse476.weatherapphw4.enums.TempUnit
import com.example.cse476.weatherapphw4.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var binding: FragmentSettingsBinding? = null
    private val model: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSettingsBinding.inflate(this.layoutInflater)
        this.binding = binding
""
        binding.tempRadioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.celsiusButton -> this.model.updateTempUnit(TempUnit.Celsius)
                R.id.fahrenheitButton -> this.model.updateTempUnit(TempUnit.Fahrenheit)
                R.id.kelvinButton -> this.model.updateTempUnit(TempUnit.Kelvin)
            }
        }

        this.model.tempUnit.observe(viewLifecycleOwner) { unit ->
            when (unit) {
                TempUnit.Celsius -> binding.tempRadioGroup.check(R.id.celsiusButton)
                TempUnit.Fahrenheit -> binding.tempRadioGroup.check(R.id.fahrenheitButton)
                TempUnit.Kelvin -> binding.tempRadioGroup.check(R.id.kelvinButton)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }
}