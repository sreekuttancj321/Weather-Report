package com.sree.weatherreport.view


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText

import com.sree.weatherreport.R
import com.sree.weatherreport.helper.ConnectivityHelper
import com.sree.weatherreport.viewmodel.CityViewModel
import com.sree.weatherreport.viewmodel.SharedViewModel
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Initial page (first time)
 * take user city as input and persist the data for later use
 *
 */
class CityFragment : Fragment(), View.OnClickListener {

    private lateinit var imageButtonForward: ImageButton
    private lateinit var textInputEditTextCity: TextInputEditText

    private lateinit var cityViewModel: CityViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var cityFragCommunicationChannel: CityFragCommunicationChannel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CityFragCommunicationChannel)
            cityFragCommunicationChannel = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cityViewModel = ViewModelProviders.of(this)[CityViewModel::class.java]
        sharedViewModel = activity.let {
            ViewModelProviders.of(it!!).get(SharedViewModel::class.java)
        }

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_city, container, false)
        setUpView(view)

        return view
    }

    private fun setUpView(view: View){
        imageButtonForward = view.findViewById(R.id.imageButton_forward)
        imageButtonForward.setOnClickListener(this)
        textInputEditTextCity = view.findViewById(R.id.edit_input_city_name)

        if (sharedViewModel.getIsFirstTime())
            (activity as AppCompatActivity).supportActionBar!!.hide()
        else{
            //show up button sec time onwards
            (activity as AppCompatActivity).supportActionBar!!.show()
            (activity as AppCompatActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(false)
            (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        (activity as AppCompatActivity).toolbar.setNavigationOnClickListener{
            findNavController().popBackStack(R.id.weatherFragment, false)
        }


    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imageButton_forward -> {
                    if (isValidCityName(textInputEditTextCity.text.toString())) {
                        if (ConnectivityHelper.isConnectedToNetwork(requireContext())){
                            if (sharedViewModel.getIsFirstTime()) {
                                cityViewModel.setCityNameAndStatus(false, textInputEditTextCity.text.toString())
                                cityFragCommunicationChannel.goToWeatherFragment(textInputEditTextCity.text.toString(),"first_time")
                            }else{
                                cityFragCommunicationChannel.goToWeatherFragment(textInputEditTextCity.text.toString(),"second_time")
                            }
                        }else{
                                 //if db has no data and nw not available
                                showDialogBox(requireContext())?.show()
                        }
                    } else {
                        Toast.makeText(requireContext(), resources.getString(R.string.enter_city_name_hint), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    interface CityFragCommunicationChannel{
        fun goToWeatherFragment(cityName:String,status: String)
    }

    private fun isValidCityName(cityName: String): Boolean{
        return cityName.isNotEmpty()
    }

    private fun showDialogBox(context: Context): AlertDialog.Builder? {
        return AlertDialog.Builder(context)
            .setTitle(getString(R.string.no_network))
            .setMessage(getString(R.string.network_message))
            .setPositiveButton(getString(R.string.retry)) {
                    dialog, which ->
                if (isValidCityName(textInputEditTextCity.text.toString())) {
                    if (ConnectivityHelper.isConnectedToNetwork(requireContext())) {
                        if (sharedViewModel.getIsFirstTime()) {
                            cityViewModel.setCityNameAndStatus(false, textInputEditTextCity.text.toString())
                            cityFragCommunicationChannel.goToWeatherFragment(textInputEditTextCity.text.toString(), "first_time")
                        }else{
                            cityFragCommunicationChannel.goToWeatherFragment(textInputEditTextCity.text.toString(), "second_time")
                        }
                    }else{
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.no_network), Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.enter_city_name_hint), Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel),null)
    }
}
