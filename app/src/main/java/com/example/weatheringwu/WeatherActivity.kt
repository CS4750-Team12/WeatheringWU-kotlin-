package com.example.weatheringwu

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import io.github.cdimascio.dotenv.dotenv
class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)

        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        val city = intent.getStringExtra("city")
        val country = intent.getStringExtra("country")
        val state = intent.getStringExtra("state")

        val dotenv = dotenv {
            directory = "./assets"
            filename = "env"
        }
        val apiKey = dotenv["API_KEY"]

        val retrofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        val weatherApiService = retrofit.create(WeatherApiService::class.java)

        val call = weatherApiService.getWeatherData(lat, lon, apiKey)

        fun Double.round(d: Int): Double {
            var multiplier = 1.0
            repeat(d) { multiplier *= 10 }
            return (kotlin.math.round(this * multiplier) / multiplier)
        }

        fun celsiusConvertion(k: Double): Double {
            return (k - 273.15).round(3)
        }

        fun fahrenheitConvertion(k: Double): Double {
            return ((k - 273.15) * (9 / 5) + 32).round(3)
        }

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    Log.d("Weather Data: ", weatherResponse.toString())
                    val weatherInfo = weatherResponse?.weather?.get(0)
                    val mainInfo = weatherResponse?.main

                    runOnUiThread {
                        if (mainInfo != null) {
                            findViewById<TextView>(R.id.weatherInfoTextView).text =
                                "Weather: ${weatherInfo?.description}" +
                                        "\nTemperature: ${celsiusConvertion(mainInfo.temp)}℃ / ${fahrenheitConvertion(mainInfo.temp)}℉" +
                                        "\nFeels Like: ${celsiusConvertion(mainInfo.feels_like)}℃ / ${fahrenheitConvertion(mainInfo.feels_like)}℉" +
                                        "\nLowest Temperature: ${celsiusConvertion(mainInfo.temp_min)}℃ / ${fahrenheitConvertion(mainInfo.temp_min)}℉" +
                                        "\nHighest Temperature: ${celsiusConvertion(mainInfo.temp_max)}℃ / ${fahrenheitConvertion(mainInfo.temp_max)}℉" +
                                        "\nHumidity: ${mainInfo.humidity}%"
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

        val coordsTextView: TextView = findViewById(R.id.coordsTextView)
        val cityInfoTextView: TextView = findViewById(R.id.cityInfoTextView)

        coordsTextView.text = "Latitude: $lat \nLongitude: $lon"
        cityInfoTextView.text = "City: $city \nCountry: $country \nState: $state"
    }
}

//℃ ℉