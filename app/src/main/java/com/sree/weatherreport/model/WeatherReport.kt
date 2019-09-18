package com.sree.weatherreport.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_report")
data class WeatherReport(@PrimaryKey(autoGenerate = true)
                         var id : Int,
                         var cityID: Long,
                         var cityName: String,
                         var dateTime: Long,
                         var avgTemp: Double,
                         var minTemp:Double,
                         var maxTemp:Double,
                         var weatherTitle:String,
                         var weatherDescription:String,
                         var icon: String,
                         var dayOfTheWeek:Int,
                         var status:String
                         ){
    constructor():this(0,0,"",0,0.0,0.0,0.0,"","","",0,"")
}