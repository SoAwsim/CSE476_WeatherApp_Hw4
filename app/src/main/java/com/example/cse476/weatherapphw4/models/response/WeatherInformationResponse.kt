package com.example.cse476.weatherapphw4.models.response

data class WeatherResponse(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Int,
    val sys: Sys,
    val dt_txt: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)

data class Clouds (
    val all: Int
)

data class Wind (
    val speed: Double,
    val deg: Int,
    val gust: Double,
)

data class Sys (
    val pod: String
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
