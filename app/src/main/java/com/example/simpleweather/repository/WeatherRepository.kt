package com.example.simpleweather.repository

import com.example.simpleweather.api.ApiInterface
import com.example.simpleweather.response.WeatherData
import com.example.simpleweather.room.AppDataBase
import com.example.simpleweather.room.RecentChoice
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class WeatherRepository @Inject constructor(private val weatherApi: ApiInterface, private val internalDB : AppDataBase?) {
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