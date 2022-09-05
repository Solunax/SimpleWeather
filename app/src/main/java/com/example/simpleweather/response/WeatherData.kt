package com.example.simpleweather.response


data class WeatherData(
    val response: Response
) {
    data class Response(
        val header: Header,
        val body: Body
    )

    data class Header(
        val resultCode : String,
        val resultMsg : String
    )

    data class  Body(
        val dataType : String,
        val items : Items
    )

    data class Items(
        val item: List<Item>
    )

    data class Item(
        val baseData : Int,
        val baseTime : Int,
        val category : String,
        val fcstDate : Int,
        val fcstTime : Int,
        val fcstValue : String,
        val nx : Int,
        val ny : Int
    )
}