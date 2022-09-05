package com.example.simpleweather.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent")
data class RecentChoice(@PrimaryKey val index : Int, val location:Int)