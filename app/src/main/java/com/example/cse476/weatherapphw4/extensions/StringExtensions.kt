package com.example.cse476.weatherapphw4.extensions

import java.util.Locale

fun String.capitalizeEveryWord(): String {
    val everyWord = this.split(" ")
    return everyWord.map { word ->
        word.replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase())
                firstChar.titlecase(Locale.getDefault())
            else
                firstChar.toString()
        }
    }.joinToString(" ")
}