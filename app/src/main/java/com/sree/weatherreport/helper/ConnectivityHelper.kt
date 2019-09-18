package com.sree.weatherreport.helper

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager



class ConnectivityHelper {

    companion object{
        fun isConnectedToNetwork(context: Context): Boolean{
            val connectivityManager: ConnectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            var isConnected = false
            val activeNetwork = connectivityManager.activeNetworkInfo
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

            return isConnected
        }
    }
}