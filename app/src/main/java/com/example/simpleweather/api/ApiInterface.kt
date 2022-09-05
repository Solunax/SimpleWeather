package com.example.simpleweather.api

import com.example.simpleweather.api.ApiKey.Companion.API_KEY
import com.example.simpleweather.response.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("getVilageFcst?serviceKey=$API_KEY")
    suspend fun getWeather(
        @Query("numOfRows") numOfRows : Int,
        @Query("pageNo") pageNo : Int,
        @Query("dataType") dataType : String,
        @Query("base_date") baseDate : Int,
        @Query("base_time") baseTime : Int,
        @Query("nx") nx : String,
        @Query("ny") ny : String
    ): Response<WeatherData>
}