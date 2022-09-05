package com.example.simpleweather.repository

import com.example.simpleweather.api.ApiInterface
import com.example.simpleweather.response.WeatherData
import com.example.simpleweather.room.AppDataBase
import com.example.simpleweather.room.RecentChoice
import retrofit2.Response

class WeatherRepository(private val weatherApi: ApiInterface, private val internalDB : AppDataBase?) {
    suspend fun getWeather(rows :  Int, page : Int, type : String,
                           date : Int, time : Int, nx : String, ny : String):Response<WeatherData>{
        return weatherApi.getWeather(rows, page, type, date, time, nx, ny)
    }

    fun getRecentChoice() : Int{
        val value = internalDB?.RecentChoiceDAO()!!.getRecentChoice()
        return value.location
    }

    fun setRecentChoice(index : Int){
        internalDB?.RecentChoiceDAO()?.insertRecentChoice(RecentChoice(0, index))
    }
}