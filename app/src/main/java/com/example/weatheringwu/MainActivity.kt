package com.example.weatheringwu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var editTextCitySearch: EditText
    private lateinit var buttonSearch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextCitySearch = findViewById(R.id.editTextCitySearch)
        buttonSearch = findViewById(R.id.buttonSearch)

        buttonSearch.setOnClickListener{
            val city = editTextCitySearch.text.toString()
            if(city.isNotEmpty()){
                fetchWeatherForCity(city)
            }else{
                Toast.makeText(this, "Please enter the name of a city", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeatherForCity(city: String){
        //todo
    }
}

//Geocoding API for longitude and latitude: https://openweathermap.org/api/geocoding-api
//Geocoding API for deatiled weather information: https://openweathermap.org/current