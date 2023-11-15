package com.example.weatheringwu

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var cityAdapter: CityAdapter

    private val apiService by lazy{
        Retrofit.Builder().baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerView)

        setupRecyclerView()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    searchCities(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Implement if needed
                return false
            }
        })
    }

    private fun setupRecyclerView(){
        cityAdapter=CityAdapter(emptyList())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cityAdapter
        }
    }

    private fun searchCities(cityName: String){
        CoroutineScope(Dispatchers.IO).launch{
            try{
                val apiKey = "49bdc31b415440304250deae9af0e13b"
                val cities = apiService.searchCity(cityName, 1000, apiKey)
                updateCityList(cities)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun updateCityList(cities: List<CityInfo>){
        CoroutineScope(Dispatchers.Main).launch{
            cityAdapter = CityAdapter(cities)
            recyclerView.adapter = cityAdapter
            cityAdapter.notifyDataSetChanged()
        }
    }
}

//Geocoding API for longitude and latitude: https://openweathermap.org/api/geocoding-api
//Geocoding API for detailed weather information: https://openweathermap.org/current