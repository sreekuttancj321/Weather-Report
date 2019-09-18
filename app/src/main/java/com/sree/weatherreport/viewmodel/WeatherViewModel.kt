package com.sree.weatherreport.viewmodel

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.sree.weatherreport.model.WeatherReport
import com.sree.weatherreport.repository.WeatherRepository
import java.io.File
import java.io.IOException

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherRepository = WeatherRepository.getInstance(application)


    fun getCurrentWeatherData(cityName: String){
        weatherRepository.getCurrentWeatherFromServer(cityName)
    }

    fun getForecasFromServer(cityName: String){
        weatherRepository.getForecastFromServer(cityName)
    }

    fun isCurrentWeatherReportPresent(): Boolean = weatherRepository.isCurrentWeatherReportPresent()

    fun getCurrentWeatherReportFromDb(): LiveData<WeatherReport> =
        weatherRepository.getCurrentWeatherReport()

    fun getForecastReportFromDb(): LiveData<List<WeatherReport>> =
        weatherRepository.getForecastReport()

    fun convertKelvinToCelsius(kelvin: Double): Int{
        return (kelvin-273.15).toInt()
    }


    /**
     * save icon in internal storage for offline use
     */
    fun saveIconToInternalStorage(fileName: String,image: Bitmap,context: Context){
        try{
            context.openFileOutput("$fileName.png", MODE_PRIVATE).use{
                image.compress(Bitmap.CompressFormat.PNG,100,it)
                it.close()
            }
        }catch (e: IOException){
            Log.e("saveToInternalStorage()", e.message)
        }
    }
    fun checkInternalDirForImage(context: Context,fileName: String): Boolean{
        val filePath = context.getFileStreamPath("$fileName.png")
        Log.i("check_path",filePath.exists().toString())
        return filePath.exists()
    }
}