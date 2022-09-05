package com.example.simpleweather

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.simpleweather.bindingClass.WeatherClass
import com.example.simpleweather.databinding.ActivityMainBinding
import com.example.simpleweather.viewModel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val viewModel : MainViewModel by viewModels()
    private val type = "JSON"
    private val rows = 14
    private val page = 1
    private val baseTime = arrayOf(2, 5, 8, 11, 14, 17, 20, 23)
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        val locationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.location_array))
        val windDirectionArray = resources.getStringArray(R.array.windDirection)
        val locationArray = resources.getStringArray(R.array.locationXyArray)

        val locationSpinner = binding.locationSpinner
        val weatherImageView = binding.weatherImage
        val precipitationImage = binding.precipitationImage
        val precipitation = binding.precipitation

        locationSpinner.adapter = locationAdapter
        locationSpinner.setSelection(0, false)
        locationSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                val data = getDateInformation()
                Log.d("DEBUG", "${data[0]}   ${data[1]}")

                viewModel.getWeather(rows, page, type, data[0], data[1], position, windDirectionArray, locationArray)
                viewModel.setRecentChoice(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        viewModel.recentResponse.observe(this){
            val data = getDateInformation()
            Log.d("pos", "$it")
            if(it != null)
                viewModel.getWeather(rows, page, type, data[0], data[1], it, windDirectionArray, locationArray)
            else
                viewModel.getWeather(rows, page, type, data[0], data[1], 0, windDirectionArray, locationArray)

            locationSpinner.setSelection(it)
        }


        viewModel.weatherResponse.observe(this){
            binding.weathers = it

            setWeatherImage(weatherImageView, it)
            if(it.pcp != "강수없음"){
                precipitationImage.visibility = View.VISIBLE
                precipitation.visibility = View.VISIBLE
            }else{
                precipitationImage.visibility = View.GONE
                precipitation.visibility = View.GONE
            }
            Toast.makeText(this, "Value Updated", Toast.LENGTH_SHORT).show()
        }

        viewModel.getRecentChoice()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateInformation():Array<Int>{
        val cal = Calendar.getInstance()
        cal.time = Date()
        var dateFormat = SimpleDateFormat("yyyyMMdd kk:mm")
        val now = dateFormat.format(cal.time).split(" ")
        val timeData = now[1].split(":").map{ it.toInt() }

        var reqTime = 0
        if(timeData[0] == 24 || timeData[0] < 2){
            cal.add(Calendar.DATE, -1)
            reqTime = 2300
        }
        else{
            for(i in baseTime.indices){
                if(timeData[0] < baseTime[i]){
                    reqTime = if(timeData[0] == baseTime[i - 1] && timeData[1] < 40){
                        if(timeData[0] == 2){
                            cal.add(Calendar.DATE, -1)
                            baseTime.last() * 100
                        } else
                            baseTime[i - 2] * 100
                    } else
                        baseTime[i - 1] * 100
                    break
                }
            }
        }
        dateFormat =  SimpleDateFormat("yyyyMMdd")
        val reqDate = dateFormat.format(cal.time).toInt()

        return arrayOf(reqDate, reqTime)
    }

    @SuppressLint("SimpleDateFormat")
    private fun setWeatherImage(view: ImageView, weather: WeatherClass){
        val cal = Calendar.getInstance()
        cal.time = Date()
        val dateFormat = SimpleDateFormat("kk")
        val now = dateFormat.format(cal.time).toInt()

        when (weather.pty) {
            "없음" -> {
                when(weather.sky){
                    "맑음" -> {
                        if(now < 8 || now > 19)
                            view.setImageResource(R.drawable.ic_moon)
                        else
                            view.setImageResource(R.drawable.ic_sunny)
                    }
                    "구름 많음" -> view.setImageResource(R.drawable.ic_cloudy)
                    "흐림" -> view.setImageResource(R.drawable.ic_cloudy)
                }
            }
            "눈" -> view.setImageResource(R.drawable.ic_snowy)
            else -> {
                view.setImageResource(R.drawable.ic_rainy)

            }
        }
    }
}