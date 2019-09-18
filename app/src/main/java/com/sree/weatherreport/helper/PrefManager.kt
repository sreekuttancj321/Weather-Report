package com.sree.weatherreport.helper

import android.app.Application
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log

class PrefManager {
    //preference name
    private val prefName = "weather_pref"
    var mode = 0
    // Shared Preferences Keys
    private val  isFirstTime= "IsFirstTime"
    private val cityName = "cityName"


    companion object{
        private var INSTANCE: PrefManager? = null
        private var sharedPreferences: SharedPreferences? =null
        private lateinit var editor: Editor

        fun getInstance(): PrefManager =
            INSTANCE ?: PrefManager()
    }

    private fun getSharedPreference(application: Application): SharedPreferences {
        if (sharedPreferences == null) {
            sharedPreferences = application.getSharedPreferences(prefName, mode)
            editor = sharedPreferences!!.edit()
            Log.i("check_pre","null")
        }else{
            Log.i("check_pre","not null")
        }
        return sharedPreferences as SharedPreferences
    }
    fun setFirstTime(status: Boolean,city: String){
        editor.putBoolean(isFirstTime, status)
        editor.putString(cityName,city)
        editor.commit()
    }

    fun isFirstTime(application: Application): Boolean =
         getSharedPreference(application).getBoolean(isFirstTime,true)

    fun getCityName(application: Application):String {
        Log.i("get_cty",getSharedPreference(application).getString("city_name","chandigarh"))
        return getSharedPreference(application).getString(cityName,"")
    }
}