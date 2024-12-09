package com.example.cse476.weatherapphw4.extensions

fun Int.convertFromCalendarEnum(): String =
    when(this) {
        1 -> "Sunday"
        2 -> "Monday"
        3 -> "Tuesday"
        4 -> "Wednesday"
        5 -> "Thursday"
        6 -> "Friday"
        7 -> "Saturday"
        else -> "Unknown"
    }