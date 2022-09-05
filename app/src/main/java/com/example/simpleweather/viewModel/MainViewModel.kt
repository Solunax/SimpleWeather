package com.example.simpleweather.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleweather.api.ApiClient
import com.example.simpleweather.bindingClass.WeatherClass
import com.example.simpleweather.repository.WeatherRepository
import com.example.simpleweather.room.AppDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import kotlin.collections.HashMap
import kotlin.math.floor

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _weatherResponse : MutableLiveData<WeatherClass> = MutableLiveData()
    private val _recentResponse : MutableLiveData<Int> = MutableLiveData()
    private val repository : WeatherRepository
    val weatherResponse get() = _weatherResponse
    val recentResponse get() = _recentResponse

    init {
        repository = WeatherRepository(ApiClient.getApi(ApiClient.getRetrofit()), AppDataBase.getInstance(application))

    }

    fun getRecentChoice(){
        viewModelScope.launch(Dispatchers.IO) {
            _recentResponse.postValue(repository.getRecentChoice())
        }
    }

    fun setRecentChoice(index : Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.setRecentChoice(index)
        }
    }

    fun getWeather(rows :  Int, page : Int, type : String, date : Int, time : Int,
                   locationIndex: Int, windDirectionArray : Array<String>, locationArray : Array<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val locationData = getLocationData(locationIndex, locationArray)
            val response = repository.getWeather(rows, page, type, date, time, locationData.first, locationData.second)
            if (response.body()?.response?.header?.resultCode == "00") {
                val info = HashMap<String, String>()
                response.body()!!.response.body.items.item.forEach {
                    info[it.category] = it.fcstValue
                }

                val sb = StringBuilder()
                info.forEach {
                    when (it.key) {
                        "POP" -> info[it.key] = "${it.value}%"
                        "UUU" -> info[it.key] = "${it.value}m/s"
                        "REH" -> info[it.key] = "${it.value}%"
                        "TMP" -> info[it.key] = "${it.value}℃"
                        "VVV" -> info[it.key] = "${it.value}m/s"
                        "WSD" -> info[it.key] = "${it.value}m/s"
                        "WAV" -> info[it.key] = "${it.value}m"
                        "VEC" -> {
                            val direction = floor((it.value.toInt() + 11.25) / 22.5).toInt()
                            info[it.key] = windDirectionArray[direction]
                        }
                        "SKY" -> {
                            when (it.value) {
                                "1" -> sb.append("맑음")
                                "3" -> sb.append("구름 많음")
                                "4" -> sb.append("흐림")
                            }
                            info[it.key] = sb.toString()
                            sb.clear()
                        }
                        "PTY" -> {
                            when (it.value) {
                                "0" -> sb.append("없음")
                                "1" -> sb.append("비")
                                "2" -> sb.append("비/눈")
                                "3" -> sb.append("눈")
                                "4" -> sb.append("소나기")
                            }
                            info[it.key] = sb.toString()
                            sb.clear()
                        }
                        "SNO" -> {
                            when (it.value) {
                                "적설없음" -> sb.append("적설없음")
                                "1.0cm 미만" -> sb.append("1.0cm 미만")
                                "5.0cm 이상" -> sb.append("5.0cm 이상")
                                else -> sb.append("${it.value}cm")
                            }
                            info[it.key] = sb.toString()
                            sb.clear()
                        }
                        "PCP" -> {
                            when (it.value) {
                                "강수없음" -> sb.append("강수없음")
                                "1.0mm 미만" -> sb.append("1.0mm 미만")
                                "30.0mm~50.0mm" -> sb.append("30.0mm~50.0mm")
                                "50.0mm 이상" -> sb.append("50.0mm 이상")
                                else -> sb.append(it.value)
                            }
                            info[it.key] = sb.toString()
                            sb.clear()
                        }
                    }
                }

                val weatherClass = WeatherClass(
                    info["POP"]!!, info["SKY"]!!, info["PTY"]!!, info["UUU"]!!,
                    info["REH"]!!, info["VEC"]!!, info["SNO"]!!, info["TMP"]!!,
                    info["VVV"]!!, info["WSD"]!!, info["PCP"]!!, info["WAV"]!!
                )
                _weatherResponse.postValue(weatherClass)
            }
        }
    }

    private fun getLocationData(locationIndex : Int, locationArray : Array<String>):Pair<String, String>{
        val location = locationArray[locationIndex].split(" ")
        return Pair(location.first(), location.last())
    }
}