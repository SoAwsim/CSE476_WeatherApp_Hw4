package com.example.cse476.weatherapphw4.extensions

import kotlin.math.ceil
import kotlin.math.floor

fun Double?.toUIString(): String {
    if (this == null)
        return ""

    if (ceil(this) == floor(this))
        return this.toLong().toString()

    return this.toString()
}

fun Double.kelvinToCelsius(): Double {
    return this - 273.15
}

fun Double.kelvinToFahrenheit(): Double {
    return this * 1.8 - 459.67
}