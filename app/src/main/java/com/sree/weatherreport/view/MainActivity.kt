package com.sree.weatherreport.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.sree.weatherreport.R
import com.sree.weatherreport.helper.ConnectivityHelper
import com.sree.weatherreport.viewmodel.SharedViewModel

class MainActivity : AppCompatActivity(), CityFragment.CityFragCommunicationChannel,
WeatherFragment.WeatherCommunicationChannel{

    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
        //if it is not first time then go to weather report page
        if (!sharedViewModel.getIsFirstTime())
            findNavController(R.id.fragment_nav_host).navigate(R.id.action_cityFragment_to_weatherFragment)
    }

    /**
     * From CityFragment
     */
    override fun goToWeatherFragment(cityName: String,status: String) {

        if (status != "first_time") {
            sharedViewModel.setCityName(cityName)
            findNavController(R.id.fragment_nav_host).popBackStack(R.id.weatherFragment, false)
        }
        else {

            findNavController(R.id.fragment_nav_host).navigate(R.id.action_cityFragment_to_weatherFragment)
        }
    }
    /**
     * From WeatherFragment
     */
    override fun goToCityFragment() {
        if (ConnectivityHelper.isConnectedToNetwork(this))
      findNavController(R.id.fragment_nav_host).navigate(R.id.action_weatherFragment_to_cityFragment32)
        else
            Toast.makeText(this,resources.getString(R.string.no_network), Toast.LENGTH_SHORT).show()

    }
}
