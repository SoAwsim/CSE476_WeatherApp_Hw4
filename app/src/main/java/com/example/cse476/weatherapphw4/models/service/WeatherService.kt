package com.example.cse476.weatherapphw4.models.service

import android.location.Location
import android.util.Log
import com.example.cse476.weatherapphw4.BuildConfig
import com.example.cse476.weatherapphw4.models.response.WeatherApiResponse
import com.example.cse476.weatherapphw4.models.response.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherService @Inject constructor() {
    companion object {
        private const val TAG = "WeatherRepository"
        private const val WEATHER_API =
            "https://api.openweathermap.org/data/2.5/forecast?appid=" + BuildConfig.API_KEY
    }

    var weatherMapByDate: Map<Int, List<WeatherResponse>> = mapOf()

    suspend fun fetchDataFromApi(location: Location?) = withContext(Dispatchers.IO) {
        if (location == null)
            return@withContext
        var reader: BufferedReader? = null
        try {
            val connection = URL(
                WEATHER_API + "&lat=" + location.latitude + "&lon=" + location.longitude)
                .openConnection()
            connection.connect()
            reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            val gson = Gson()
            val parsedResponse = gson.fromJson(response.toString(), WeatherApiResponse::class.java)
            this@WeatherService.weatherMapByDate = parsedResponse.list.sortedBy {
                it.dt
            }.groupBy {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.dt * 1000L
                calendar.get(Calendar.DAY_OF_WEEK)
            }
            return@withContext
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get response from API", e)
        } finally {
            reader?.close()
        }
    }
}