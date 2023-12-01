package com.example.weatheringwu

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)

        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)

        // add api request here

        val latitudeTextView: TextView = findViewById(R.id.latitudeTextView)
        val longitudeTextView: TextView = findViewById(R.id.longitudeTextView)

        latitudeTextView.text = "Latitude: $lat"
        longitudeTextView.text = "Longitude: $lon"
    }
}