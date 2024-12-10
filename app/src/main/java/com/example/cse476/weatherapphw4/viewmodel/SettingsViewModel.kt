package com.example.cse476.weatherapphw4.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.cse476.weatherapphw4.enums.TempUnit
import com.example.cse476.weatherapphw4.service.SettingsService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val settingsService: SettingsService
) : AndroidViewModel(application) {
    val tempUnit = this.settingsService.tempUnit

    fun updateTempUnit(unit: TempUnit) {
        this.settingsService.changeTemp(unit)
    }
}