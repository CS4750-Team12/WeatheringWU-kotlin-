package com.example.weatheringwu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), CityAdapter.OnItemClickListener {
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
                if (!query.isNullOrBlank()) searchCities(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        cityAdapter.setOnItemClickListener(this)
    }

    private fun setupRecyclerView(){
        cityAdapter = CityAdapter(mutableListOf())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cityAdapter
        }
    }

    private fun searchCities(cityName: String){
        CoroutineScope(Dispatchers.IO).launch{
            try{
                val dotenv = dotenv {
                    directory = "./assets"
                    filename = "env"
                }
                val apiKey = dotenv["API_KEY"]
                val cities = apiService.searchCity(cityName, 1000, apiKey)
                Log.d("API Response: ", cities.toString())
                updateCityList(cities)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    private fun updateCityList(cities: List<CityInfo>){
        runOnUiThread {
            cityAdapter.updateData(cities);
        }
    }

    override fun onItemClick(cityInfo: CityInfo){
        val intent = Intent(this, WeatherActivity::class.java)
        intent.putExtra("lat", cityInfo.lat)
        intent.putExtra("lon", cityInfo.lon)
        intent.putExtra("city", cityInfo.name)
        intent.putExtra("country", cityInfo.country)
        intent.putExtra("state", cityInfo.state)
        startActivity(intent)
    }
}

//Geocoding API for longitude and latitude: https://openweathermap.org/api/geocoding-api
//Geocoding API for detailed weather information: https://openweathermap.org/current