package com.example.simpleweather.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface RecentChoiceDAO {
    @Insert(onConflict = REPLACE)
    fun insertRecentChoice(choice : RecentChoice)

    @Query("SELECT * FROM recent")
    fun getRecentChoice() : RecentChoice
}