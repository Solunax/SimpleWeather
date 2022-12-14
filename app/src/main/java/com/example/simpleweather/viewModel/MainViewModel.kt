package com.example.simpleweather.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleweather.bindingClass.WeatherClass
import com.example.simpleweather.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.math.floor

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: WeatherRepository) : ViewModel() {
    private val _weatherResponse : MutableLiveData<WeatherClass> = MutableLiveData()
    private val _recentResponse : MutableLiveData<Int> = MutableLiveData()
    val weatherResponse get() = _weatherResponse
    val recentResponse get() = _recentResponse

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
                        "TMP" -> info[it.key] = "${it.value}???"
                        "VVV" -> info[it.key] = "${it.value}m/s"
                        "WSD" -> info[it.key] = "${it.value}m/s"
                        "WAV" -> info[it.key] = "${it.value}m"
                        "VEC" -> {
                            val direction = floor((it.value.toInt() + 11.25) / 22.5).toInt()
                            info[it.key] = windDirectionArray[direction]
                        }
                        "SKY" -> {
                            when (it.value) {
                                "1" -> sb.append("??????")
                                "3" -> sb.append("?????? ??????")
                                "4" -> sb.append("??????")
                            }
                            info[it.key] = sb.toString()
                            sb.clear()
                        }
                        "PTY" -> {
                            when (it.value) {
                                "0" -> sb.append("??????")
                                "1" -> sb.append("???")
                                "2" -> sb.append("???/???")
                                "3" -> sb.append("???")
                                "4" -> sb.append("?????????")
                            }
                            info[it.key] = sb.toString()
                            sb.clear()
                        }
                        "SNO" -> {
                            when (it.value) {
                                "????????????" -> sb.append("????????????")
                                "1.0cm ??????" -> sb.append("1.0cm ??????")
                                "5.0cm ??????" -> sb.append("5.0cm ??????")
                                else -> sb.append("${it.value}cm")
                            }
                            info[it.key] = sb.toString()
                            sb.clear()
                        }
                        "PCP" -> {
                            when (it.value) {
                                "????????????" -> sb.append("????????????")
                                "1.0mm ??????" -> sb.append("1.0mm ??????")
                                "30.0mm~50.0mm" -> sb.append("30.0mm~50.0mm")
                                "50.0mm ??????" -> sb.append("50.0mm ??????")
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