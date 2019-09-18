package com.sree.weatherreport.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sree.weatherreport.repository.WeatherRepository

class CityViewModel(application: Application): AndroidViewModel(application) {

    private val weatherRepository = WeatherRepository.getInstance(application)
    fun setCityNameAndStatus(status: Boolean,cityName: String){
        weatherRepository.setCityNameAndStatus(status,cityName)
    }
}