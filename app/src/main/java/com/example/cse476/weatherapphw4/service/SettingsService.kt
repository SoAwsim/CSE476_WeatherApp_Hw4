package com.example.cse476.weatherapphw4.service

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cse476.weatherapphw4.enums.LocationType
import com.example.cse476.weatherapphw4.enums.TempUnit
import com.example.cse476.weatherapphw4.models.response.CityResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsService @Inject constructor() {
    companion object {
        private const val preferenceName = "WeatherPreferences"
        private const val tempUnitKey = "TempUnit"
        private const val locationTypeKey = "LocationType"

        private const val cityNameKey = "City"
        private const val latKey = "Lat"
        private const val lonKey = "Lon"
        private const val countryKey = "Country"
    }

    private val _tempUnit = MutableLiveData<TempUnit>(TempUnit.Celsius)
    val tempUnit: LiveData<TempUnit> = this._tempUnit

    private val _locationType = MutableLiveData<LocationType>(LocationType.Current)
    val locationType: LiveData<LocationType> = this._locationType

    private val _cityLocation = MutableLiveData<Location?>(null)
    val cityLocation: LiveData<Location?> = this._cityLocation

    private val _cityInformation = MutableLiveData<CityResponse?>(null)
    val cityInformation: LiveData<CityResponse?> = this._cityInformation

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

        val locationType = this.sharedPreferences.getInt(locationTypeKey, 0)
        when (locationType) {
            0 -> this._locationType.value = LocationType.Current
            1 -> this._locationType.value = LocationType.City
            else -> this._locationType.value = LocationType.Current
        }

        if (this._locationType.value != LocationType.City)
            return

        val cityName = this.sharedPreferences.getString(cityNameKey, "")
        val lon = Double.fromBits(this.sharedPreferences.getLong(lonKey, 0))
        val lat = Double.fromBits(this.sharedPreferences.getLong(latKey, 0))
        val country = this.sharedPreferences.getString(countryKey, "")
        this._cityInformation.value = CityResponse(
            cityName!!,
            lon,
            lat,
            country!!
        )
        val location = Location("")
        location.longitude = lon
        location.latitude = lat
        this._cityLocation.value = location
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

    fun changeLocationType(locationType: LocationType) {
        var editor = this.sharedPreferences.edit()
        when (locationType) {
            LocationType.Current -> editor.putInt(locationTypeKey, 0)
            LocationType.City -> editor.putInt(locationTypeKey, 1)
        }
        editor.apply()
        this._locationType.value = locationType
    }

    fun saveCityInformation(cityInfo: CityResponse) {
        this.sharedPreferences
            .edit()
            .putString(cityNameKey, cityInfo.name)
            .putLong(latKey, cityInfo.lat.toRawBits())
            .putLong(lonKey, cityInfo.lon.toRawBits())
            .putString(countryKey, cityInfo.country)
            .apply()
        this._cityInformation.value = cityInfo
        val location = Location("")
        location.longitude = cityInfo.lon
        location.latitude = cityInfo.lat
        this._cityLocation.value = location
    }
}