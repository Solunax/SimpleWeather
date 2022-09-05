package com.example.simpleweather.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecentChoice::class], version = 1)
abstract class AppDataBase : RoomDatabase(){
    abstract fun RecentChoiceDAO() : RecentChoiceDAO

    companion object{
        private var instance : AppDataBase? = null

        fun getInstance(context : Context) : AppDataBase?{
            if(instance == null){
                synchronized(AppDataBase::class){
                    instance = Room.databaseBuilder(context, AppDataBase::class.java, "recnet").build()
                }
            }
            return instance
        }
    }
}