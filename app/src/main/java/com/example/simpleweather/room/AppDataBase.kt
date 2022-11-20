package com.example.simpleweather.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecentChoice::class], version = 1)
abstract class AppDataBase : RoomDatabase(){
    abstract fun RecentChoiceDAO() : RecentChoiceDAO
}