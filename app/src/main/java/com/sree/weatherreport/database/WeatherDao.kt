package com.sree.weatherreport.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sree.weatherreport.model.WeatherReport

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentWeatherData(weatherReport: WeatherReport)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForecastData(forecastList: List<WeatherReport>)

    @Query("SELECT * FROM weather_report WHERE status='current'")
    fun getCurrentWeatherReport():LiveData<WeatherReport>

    @Query("SELECT dayOfTheWeek FROM weather_report WHERE status='current'")
    fun getCurrentDayOfWeek(): Int

    @Query("SELECT COUNT(*) FROM weather_report WHERE status='current'")
    fun isCurrentWeatherReportPresent(): Int

    @Query("DELETE FROM weather_report WHERE status='current'")
    fun deletePreviousWeatherData()

    @Query("SELECT * FROM weather_report WHERE status='forecast' GROUP BY dayOfTheWeek LIMIT 5")
    fun getForecastReport():LiveData<List<WeatherReport>>

    @Query("DELETE FROM weather_report WHERE status='forecast'")
    fun deletePreviousForecastData()

}