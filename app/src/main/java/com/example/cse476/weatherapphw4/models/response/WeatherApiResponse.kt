package com.example.cse476.weatherapphw4.models.response

data class WeeklyWeatherApiResponse(
    val city: CityResponse,
    val cnt: Int,
    val list: List<WeatherResponse>
)

data class CurrentWeatherApiResponse(
    val weather: List<Weather>,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val timezone: Long,
    val name: String
)

data class CityResponse(
    val name: String,
    val lon: Double,
    val lat: Double,
    val country: String
)