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

class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)

        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)
        val city = intent.getStringExtra("city")
        val country = intent.getStringExtra("country")
        val state = intent.getStringExtra("state")

        val apiKey = "49bdc31b415440304250deae9af0e13b"

        val retrofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create()).build()

        val weatherApiService = retrofit.create(WeatherApiService::class.java)

        val call = weatherApiService.getWeatherData(lat, lon, apiKey)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    Log.d("Weather Data: ", weatherResponse.toString())
                    //todo
                } else {
                    Log.e("Weather Data", "Unsuccessful response: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("WeatherData", "onFailure: ${t.message}")
            }
        })


        val latitudeTextView: TextView = findViewById(R.id.latitudeTextView)
        val longitudeTextView: TextView = findViewById(R.id.longitudeTextView)
        val nameTextView: TextView = findViewById(R.id.nameTextView)
        val countryTextView: TextView = findViewById(R.id.countryTextView)
        val stateTextView: TextView = findViewById(R.id.stateTextView)

        latitudeTextView.text = "Latitude: $lat"
        longitudeTextView.text = "Longitude: $lon"
        nameTextView.text = "City: $city"
        countryTextView.text = "Country: $country"
        stateTextView.text = "State: $state"
    }
}