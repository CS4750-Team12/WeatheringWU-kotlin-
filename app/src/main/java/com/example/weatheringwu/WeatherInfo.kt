package com.example.weatheringwu

data class WeatherResponse(
    val weather: List<WeatherInfo>,
    val main: MainInfo,
    val coord: Coord,
    val base: String,
    val visibility: Int,
)

data class WeatherInfo(
    val id: Int,
    val main: String,
    val description: String,
)

data class MainInfo(
    val temp: Double,
    val humidity: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
)

data class Coord(
    val lon: Double,
    val lat: Double,
)

