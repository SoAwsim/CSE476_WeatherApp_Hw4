package com.example.cse476.weatherapphw4.extensions

fun Int.convertFromCalendarEnum(): String =
    when(this) {
        1 -> "Sun"
        2 -> "Mon"
        3 -> "Tue"
        4 -> "Wed"
        5 -> "Thu"
        6 -> "Fri"
        7 -> "Sat"
        else -> "Unknown"
    }