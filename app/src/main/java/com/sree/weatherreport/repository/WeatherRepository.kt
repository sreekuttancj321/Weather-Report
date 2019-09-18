package com.sree.weatherreport.repository

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.lifecycle.LiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sree.weatherreport.R
import com.sree.weatherreport.database.AppDatabase
import com.sree.weatherreport.helper.Common
import com.sree.weatherreport.helper.PrefManager
import com.sree.weatherreport.model.WeatherReport
import com.sree.weatherreport.network.VollySingleton
import org.json.JSONObject
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

class WeatherRepository(private val application: Application) {

    companion object{
        private var INSTANCE : WeatherRepository? =null

        fun getInstance(application: Application) =
            INSTANCE ?: WeatherRepository(application)
    }

    private val appDatabase = AppDatabase.getInstance(application)
    /**
     * API calls
     */

    fun getCurrentWeatherFromServer(cityName: String){
       val url = Common.CURRENT+cityName
        Log.i("volley_req_url",url)
       val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,url,null,
           Response.Listener<JSONObject> {
               Log.i("current_res", "current response: $it")
               if (it.getInt("cod") == 200) {
                   val weatherReport = WeatherReport()
                   val weatherObject = it.getJSONArray("weather")
                   val mainObject = it.getJSONObject("main")

                   weatherReport.weatherTitle = weatherObject.getJSONObject(0).getString("main")
                   weatherReport.weatherDescription = weatherObject.getJSONObject(0).getString("description")
                   weatherReport.icon = weatherObject.getJSONObject(0).getString("icon")
                   weatherReport.avgTemp = mainObject.getDouble("temp")
                   weatherReport.minTemp = mainObject.getDouble("temp_min")
                   weatherReport.maxTemp = mainObject.getDouble("temp_max")
                   weatherReport.dateTime = it.getLong("dt")
                   weatherReport.cityID = it.getLong("id")
                   weatherReport.cityName = it.getString("name")
                   weatherReport.dayOfTheWeek =  Common.getDayOfTheWeek(it.getLong("dt"))
                   weatherReport.status = "current"

                   //insert data into db
                   appDatabase.weatherDao().deletePreviousWeatherData()
                   appDatabase.weatherDao().insertCurrentWeatherData(weatherReport)
               }

       }, Response.ErrorListener {
               if (it?.networkResponse?.statusCode!=null){
                   when(it.networkResponse.statusCode){
                       404 ->{
                           Toast.makeText(application,application.getString(R.string.city_not_found),Toast.LENGTH_SHORT).show()
                       }
                       else -> {
                           Toast.makeText(application,application.getString(R.string.warning),Toast.LENGTH_SHORT).show()
                       }}
               }else{
                   Toast.makeText(application,application.getString(R.string.warning),Toast.LENGTH_SHORT).show()
               }
               Log.e("current_res","Error volley ${it.networkResponse} ${it.message}")
           })
        VollySingleton.getInstance(application).addToRequestQueue(jsonObjectRequest)
    }

    fun getForecastFromServer(cityName: String){
        val url = Common.FORECAST+cityName
        Log.i("volley_req_url",url)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,url,null,
            Response.Listener<JSONObject> {
                Log.i("current_res", "forecast response: $it")
                if (it.getInt("cod") == 200) {
                    val weatherReportList = ArrayList<WeatherReport>()

                    val cityObject = it.getJSONObject("city")
                    val forecastList = it.getJSONArray("list")

                    for (i in 0 until forecastList.length()){
                        val jsonObject = forecastList.getJSONObject(i)
                        val mainObject = jsonObject.getJSONObject("main")
                        val weatherObject = jsonObject.getJSONArray("weather").getJSONObject(0)
                        val weatherReport = WeatherReport()
                        weatherReport.cityID = cityObject.getLong("id")
                        weatherReport.cityName = cityObject.getString("name")
                        weatherReport.status = "forecast"
                        weatherReport.dateTime = jsonObject.getLong("dt")
                        weatherReport.avgTemp = mainObject.getDouble("temp")
                        weatherReport.minTemp = mainObject.getDouble("temp_min")
                        weatherReport.maxTemp = mainObject.getDouble("temp_max")
                        weatherReport.weatherTitle = weatherObject.getString("main")
                        weatherReport.weatherDescription = weatherObject.getString("description")
                        weatherReport.dayOfTheWeek = Common.getDayOfTheWeek(jsonObject.getLong("dt"))

                        weatherReport.icon = weatherObject.getString("icon")
                        weatherReportList.add(weatherReport)
                    }
                    //insertAll into db
                    appDatabase.weatherDao().deletePreviousForecastData()
                    appDatabase.weatherDao().insertForecastData(weatherReportList)
                }
        }, Response.ErrorListener {
                if (it?.networkResponse?.statusCode!=null){
                    when(it.networkResponse.statusCode){
                        404 ->{
                            Toast.makeText(application,application.getString(R.string.city_not_found),Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(application,application.getString(R.string.warning),Toast.LENGTH_SHORT).show()
                    }}
                }else{
                    Toast.makeText(application,application.getString(R.string.warning),Toast.LENGTH_SHORT).show()
                }
                Log.e("forecast_res","Error volley ${it.networkResponse} ${it.message}")
            })
        VollySingleton.getInstance(application).addToRequestQueue(jsonObjectRequest)
    }

    /**
     * Preference data
     */
    fun getIsFirstTime(): Boolean = PrefManager.getInstance().isFirstTime(application)

    fun setCityNameAndStatus(status: Boolean,cityName: String){
        PrefManager.getInstance().setFirstTime(status,cityName)
    }

    fun getPrefCityName():String{
        return PrefManager.getInstance().getCityName(application)
    }

    /**
     * Database
     */

    fun isCurrentWeatherReportPresent():Boolean {
        return appDatabase.weatherDao().isCurrentWeatherReportPresent()>0
    }

    fun getCurrentWeatherReport():LiveData<WeatherReport> = appDatabase.weatherDao().getCurrentWeatherReport()

    fun getForecastReport(): LiveData<List<WeatherReport>> =
        appDatabase.weatherDao().getForecastReport()

}