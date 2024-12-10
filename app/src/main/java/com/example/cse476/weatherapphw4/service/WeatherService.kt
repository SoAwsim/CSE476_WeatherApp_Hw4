package com.example.cse476.weatherapphw4.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.util.Log
import com.example.cse476.weatherapphw4.BuildConfig
import com.example.cse476.weatherapphw4.models.response.WeatherApiResponse
import com.example.cse476.weatherapphw4.models.response.WeatherResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
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
        private const val WEATHER_IMAGE_API = "https://openweathermap.org/img/wn/"
    }

    var weatherMapByDate: Map<Int, List<WeatherResponse>> = mapOf()
    val iconMap: MutableMap<String, Bitmap?> = mutableMapOf()

    suspend fun fetchDataFromApi(
        location: Location?,
        context: Context
    ) = withContext(Dispatchers.IO) {
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
            val imagesTaskList: MutableMap<String, Deferred<Bitmap?>> = mutableMapOf()
            parsedResponse.list.map {
                it.weather.map {
                    if (!imagesTaskList.contains(it.icon)) {
                        imagesTaskList.put(
                            it.icon,
                            fetchImage(
                                it.icon,
                                context,
                                CoroutineScope(Dispatchers.IO)
                            )
                        )
                    }
                }
            }
            this@WeatherService.weatherMapByDate = parsedResponse.list.sortedBy {
                it.dt
            }.groupBy {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it.dt * 1000L
                calendar.get(Calendar.DAY_OF_WEEK)
            }
            imagesTaskList.map {
                val result = it.value.await()
                if (!iconMap.contains(it.key)) {
                    iconMap.put(it.key, result)
                } else if (result != null && iconMap[it.key] == null) {
                    iconMap.put(it.key, result)
                } else { }
            }
            return@withContext
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get response from API", e)
        } finally {
            reader?.close()
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