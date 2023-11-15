package com.example.weatheringwu

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("geo/1.0/direct")
    suspend fun searchCity(
        @Query("q") cityName: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey:String
    ): List<CityInfo>
}