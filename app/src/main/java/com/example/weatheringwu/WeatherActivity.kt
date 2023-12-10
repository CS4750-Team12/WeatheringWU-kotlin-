package com.example.weatheringwu

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import io.github.cdimascio.dotenv.dotenv

var currentTempUnit = 0
class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)

        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        val city = intent.getStringExtra("city")
        val country = intent.getStringExtra("country")
        val state = intent.getStringExtra("state")

        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val currentTime = utcCalendar.get(Calendar.HOUR_OF_DAY)
        val backgroundImageView: ImageView = findViewById(R.id.backgroundImageView)
        val coordsTextView: TextView = findViewById(R.id.coordsTextView)
        val cityInfoTextView: TextView = findViewById(R.id.cityInfoTextView)
        val cityInfoTextView2: TextView = findViewById(R.id.cityInfoTextViewTwo)


        fun setBackground(currTime: Int, timeZoneOffset: Int) {
            val timeZoneHours = timeZoneOffset / 3600
            val currentTimeWithOffset = (currTime + timeZoneHours) % 24

            val imageResource = if (currentTimeWithOffset in 6..18) {
                // Daytime (6 AM to 6 PM)
                R.drawable.day
            } else {
                // Nighttime (6 PM to 5 AM)
                R.drawable.night
            }
            backgroundImageView.setImageResource(imageResource)
        }

        val retrofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        val dotenv = dotenv {
            directory = "./assets"
            filename = "env"
        }
        val apiKey = dotenv["API_KEY"]

        val weatherApiService = retrofit.create(WeatherApiService::class.java)
        val call = weatherApiService.getWeatherData(lat, lon, apiKey)

        fun Double.round(d: Int): Double {
            var multiplier = 1.0
            repeat(d) { multiplier *= 10 }
            return (kotlin.math.round(this * multiplier) / multiplier)
        }

        fun celsiusConversion(k: Double): Double {
            return (k - 273.15).round(3)
        }

        fun fahrenheitConversion(k: Double): Double {
            return ((k - 273.15) * (9 / 5) + 32).round(3)
        }

        fun getFormattedTemperature(temperature: Double?): String {
            return when (currentTempUnit) {
                0 -> "${temperature?.let { celsiusConversion(it) }}℃"
                1 -> "${temperature?.let { fahrenheitConversion(it) }}℉"
                else -> "" // Handle unexpected cases if any
            }
        }
        call.enqueue(object : Callback<WeatherResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    Log.d("Weather Data: ", weatherResponse.toString())
                    val weatherInfo = weatherResponse?.weather?.get(0)
                    val mainInfo = weatherResponse?.main

                    var updateDescription = weatherInfo?.description
                    updateDescription = updateDescription
                        ?.split(' ')
                        ?.joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }

                    runOnUiThread {
                        if (mainInfo != null) {
                            val cityTempInfoTextView =
                                findViewById<TextView>(R.id.cityTempInfoTextView)
                            val weatherInfoTextView =
                                findViewById<TextView>(R.id.weatherInfoTextView)
                            val weatherInfoTextViewTwo =
                                findViewById<TextView>(R.id.weatherInfoTextViewTwo)

                            cityTempInfoTextView.text = getFormattedTemperature(mainInfo.temp)
                            findViewById<TextView>(R.id.cityWeatherInfoTextView).text = 
                                updateDescription

                            weatherInfoTextView.text =
                                "HighLow: (${getFormattedTemperature(mainInfo.temp_max)} " +
                                        "/ ${getFormattedTemperature(mainInfo.temp_min)})"
                            weatherInfoTextViewTwo.text =
                                "Humidity: ${mainInfo.humidity}%"

                            cityTempInfoTextView.setOnClickListener {
                                currentTempUnit = 1 - currentTempUnit
                                cityTempInfoTextView.text = getFormattedTemperature(mainInfo.temp)
                                weatherInfoTextView.text =
                                    "HighLow: (${getFormattedTemperature(mainInfo.temp_max)} " +
                                            "/ ${getFormattedTemperature(mainInfo.temp_min)})"
                            }
                            setBackground(currentTime, weatherResponse.timezone)
                        }
                    }
                } else {
                    Log.e("Weather Data", "Unsuccessful response: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("WeatherData", "onFailure: ${t.message}")
            }
        })

        coordsTextView.text = "($lat, $lon)"
        cityInfoTextView.text = "$city"
        val location = if (state != null) {
            "${country}, ${state}"
        } else {
            country
        }
        cityInfoTextView2.text = location
    }
}

//℃ ℉