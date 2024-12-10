package com.example.cse476.weatherapphw4.service

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cse476.weatherapphw4.enums.TempUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsService @Inject constructor() {
    companion object {
        private const val preferenceName = "WeatherPreferences"
        private const val tempUnitKey = "TempUnit"
    }

    private val _tempUnit = MutableLiveData<TempUnit>(TempUnit.Celsius)
    val tempUnit: LiveData<TempUnit> = this._tempUnit

    private lateinit var sharedPreferences: SharedPreferences

    fun initializeSettings(context: Context) {
        this.sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        val settingsTemp = this.sharedPreferences.getInt(tempUnitKey, 0)
        when (settingsTemp) {
            0 -> this._tempUnit.value = TempUnit.Celsius
            1 -> this._tempUnit.value = TempUnit.Fahrenheit
            2 -> this._tempUnit.value = TempUnit.Kelvin
            else -> this._tempUnit.value = TempUnit.Celsius
        }
    }

    fun changeTemp(unit: TempUnit) {
        var editor = this.sharedPreferences.edit()
        when (unit) {
            TempUnit.Celsius -> editor.putInt(tempUnitKey, 0)
            TempUnit.Fahrenheit -> editor.putInt(tempUnitKey, 1)
            TempUnit.Kelvin -> editor.putInt(tempUnitKey, 2)
        }
        editor.apply()
        this._tempUnit.value = unit
    }
}