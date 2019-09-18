package com.sree.weatherreport.helper

import java.util.*

class Common {
    companion object{
        private const val API_KEY = "182546f3513b24508df5e0d8fd6a9c17"
        private const val END_POINT= "http://api.openweathermap.org/"
        const val CURRENT = END_POINT + "data/2.5/weather?appid=$API_KEY&q="
        const val FORECAST = END_POINT + "data/2.5/forecast?appid=$API_KEY&q="
        const val ICON = END_POINT +"img/w/icon_id.png"

        fun getDayOfTheWeek(timestamp: Long): Int{
            val unixSeconds: Long = timestamp
            val date = Date(unixSeconds * 1000L)

            val localCalendar = Calendar.getInstance()
            localCalendar.time = date
            return localCalendar.get(Calendar.DAY_OF_WEEK)
        }

        fun convertIntToDay(dayVal:Int): String{
            return when(dayVal){
                1 -> "Sun"
                2 -> "Mon"
                3 -> "Tue"
                4 -> "Wed"
                5 -> "Thu"
                6 -> "Fri"
                7 -> "Sat"
                else -> "Mon"
            }
        }
    }
}
