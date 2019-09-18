package com.sree.weatherreport.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sree.weatherreport.model.WeatherReport

@Database(entities = [WeatherReport::class],version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object{
        private var INSTANCE: AppDatabase? =null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this){
                     Room.databaseBuilder(context.applicationContext,AppDatabase::class.java,
                    "weather_report").allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
    }
}