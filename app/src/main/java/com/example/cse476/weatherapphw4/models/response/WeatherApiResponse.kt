package com.example.cse476.weatherapphw4.models.response

data class WeatherApiResponse(
    val city: CityResponse,
    val cnt: Int,
    val list: List<WeatherResponse>
)

data class CityResponse(
    val name: String,
    val lon: Double,
    val lat: Double,
    val country: String
)