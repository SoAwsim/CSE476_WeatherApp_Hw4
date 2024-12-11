package com.example.cse476.weatherapphw4.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import com.example.cse476.weatherapphw4.BuildConfig
import com.example.cse476.weatherapphw4.models.response.CurrentWeatherApiResponse
import com.example.cse476.weatherapphw4.models.response.WeatherResponse
import com.example.cse476.weatherapphw4.models.response.WeeklyWeatherApiResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherService @Inject constructor() {
    companion object {
        private const val TAG = "WeatherRepository"
        private const val WEEKLY_WEATHER_API =
            "https://api.openweathermap.org/data/2.5/forecast?appid=" + BuildConfig.API_KEY
        private const val CURRENT_WEATHER_API =
            "https://api.openweathermap.org/data/2.5/weather?appid=" + BuildConfig.API_KEY
        private const val WEATHER_IMAGE_API = "https://openweathermap.org/img/wn/"
    }

    var currentWeather: CurrentWeatherApiResponse? = null
    private var weeklyWeatherMapByDate: Map<Int, List<WeatherResponse>> = mapOf()
    private val currentImageDownloads: ConcurrentHashMap<String, Object> = ConcurrentHashMap()
    val iconMap: ConcurrentHashMap<String, Bitmap?> = ConcurrentHashMap()

    private var weeklyFetchTask: Deferred<Unit>? = null

    suspend fun awaitWeeklyTask(): Map<Int, List<WeatherResponse>> {
        this.weeklyFetchTask?.await()
        this.weeklyFetchTask = null
        return this.weeklyWeatherMapByDate
    }

    fun fetchCurrentWeatherDataFromApi(
        location: Location?,
        context: Context,
        scope: CoroutineScope
    ): Deferred<Unit> = scope.async(Dispatchers.IO) {
        if (location == null)
            return@async
        var reader: BufferedReader? = null
        try {
            val connection = URL(
                CURRENT_WEATHER_API + "&lat=" + location.latitude + "&lon=" + location.longitude)
                .openConnection()
            connection.connect()
            reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            val gson = Gson()
            val parsedResponse = gson.fromJson(response.toString(), CurrentWeatherApiResponse::class.java)
            this@WeatherService.currentWeather = parsedResponse

            val imagesTaskList: MutableMap<String, Deferred<Bitmap?>> = mutableMapOf()
            for (weather in parsedResponse.weather) {
                val previous = this@WeatherService.currentImageDownloads
                    .putIfAbsent(weather.icon, Object())

                if (previous != null)
                    continue

                if (this@WeatherService.iconMap.contains(weather.icon)) {
                    this@WeatherService.currentImageDownloads.remove(weather.icon)
                    continue
                }

                imagesTaskList.put(
                    weather.icon,
                    this@WeatherService.fetchImage(
                        weather.icon,
                        context,
                        scope
                    )
                )
            }
            imagesTaskList.map {
                val result = it.value.await()
                if (result != null)
                    iconMap.putIfAbsent(it.key, result)
                this@WeatherService.currentImageDownloads.remove(it.key)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get response from API", e)
        } finally {
            reader?.close()
        }
    }

    fun fetchWeeklyWeatherDataFromApi(
        location: Location?,
        context: Context,
        scope: CoroutineScope
    ) {
        this.weeklyFetchTask = scope.async(Dispatchers.IO) {
            if (location == null)
                return@async
            var reader: BufferedReader? = null
            try {
                val connection = URL(
                    WEEKLY_WEATHER_API + "&lat=" + location.latitude + "&lon=" + location.longitude)
                    .openConnection()
                connection.connect()
                reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                val gson = Gson()
                val parsedResponse = gson.fromJson(response.toString(), WeeklyWeatherApiResponse::class.java)
                val imagesTaskList: MutableMap<String, Deferred<Bitmap?>> = mutableMapOf()
                parsedResponse.list.map {
                    for (weather in it.weather) {
                        val previous = this@WeatherService.currentImageDownloads
                            .putIfAbsent(weather.icon, Object())

                        if (previous != null)
                            continue

                        if (this@WeatherService.iconMap.contains(weather.icon)) {
                            this@WeatherService.currentImageDownloads.remove(weather.icon)
                            continue
                        }

                        imagesTaskList.put(
                            weather.icon,
                            this@WeatherService.fetchImage(
                                weather.icon,
                                context,
                                scope
                            )
                        )
                    }
                }
                this@WeatherService.weeklyWeatherMapByDate = parsedResponse.list.sortedBy {
                    it.dt
                }.groupBy {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = it.dt * 1000L
                    calendar.get(Calendar.DAY_OF_WEEK)
                }
                imagesTaskList.map {
                    val result = it.value.await()
                    if (result != null)
                        iconMap.putIfAbsent(it.key, result)
                    this@WeatherService.currentImageDownloads.remove(it.key)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get response from API", e)
            } finally {
                reader?.close()
            }
        }
    }

    private fun fetchImage(
        id: String,
        context: Context,
        scope: CoroutineScope
    ): Deferred<Bitmap?> = scope.async(Dispatchers.IO) {
        val imageFolder = File(context.filesDir, "images")
        if (!imageFolder.exists())
            imageFolder.mkdirs()

        val imageFile = File(imageFolder, "$id.png")

        if (imageFile.exists())
            return@async BitmapFactory.decodeFile(imageFile.absolutePath)

        try {
            val connection = URL(
                WEATHER_IMAGE_API + id + "@4x.png"
            ).openConnection()
            connection.connect()

            val buffer = ByteArray(8092)
            var bytesRead: Int

            val imageStream = connection.inputStream
            imageFile.outputStream().use { output ->
                while (imageStream.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                }
            }
            imageStream.close()
            return@async BitmapFactory.decodeFile(imageFile.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to download image $id", e)
        }
        return@async null
    }
}