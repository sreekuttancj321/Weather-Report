package com.sree.weatherreport.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sree.weatherreport.repository.WeatherRepository

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherRepository = WeatherRepository(application)
    private var cityNameUpdate :String?=null

    fun getIsFirstTime(): Boolean =
         weatherRepository.getIsFirstTime()

    fun getPreferenceCityName():String {
        return weatherRepository.getPrefCityName()
    }

    fun setCityName(cityName: String){
        this.cityNameUpdate = cityName
    }

    fun getCityName() = cityNameUpdate
}