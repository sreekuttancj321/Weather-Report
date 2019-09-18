package com.sree.weatherreport.view


import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sree.weatherreport.R
import com.sree.weatherreport.helper.Common
import com.sree.weatherreport.helper.ConnectivityHelper
import com.sree.weatherreport.helper.ConnectivityHelper.Companion.isConnectedToNetwork
import com.sree.weatherreport.model.WeatherReport
import com.sree.weatherreport.viewmodel.SharedViewModel
import com.sree.weatherreport.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.fragment_weather.*
import java.util.*


/**
 * Weather report page
 *
 */
class WeatherFragment : Fragment(), View.OnClickListener {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var pullToRefresh: SwipeRefreshLayout
    private lateinit var textViewCity: TextView
    private lateinit var textViewAvgTemp: TextView
    private lateinit var textViewMinTemp: TextView
    private lateinit var textViewMaxTemp: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var textViewFirstDay: TextView
    private lateinit var textViewSecondDay: TextView
    private lateinit var textViewThirdDay: TextView
    private lateinit var textViewFourthDay: TextView
    private lateinit var textViewFifthDay: TextView
    private lateinit var textViewFirstDayAvgTemp: TextView
    private lateinit var textViewSecondDayAvgTemp: TextView
    private lateinit var textViewThirdDayAvgTemp: TextView
    private lateinit var textViewFourthDayAvgTemp: TextView
    private lateinit var textViewFifthDayAvgTemp: TextView
    private lateinit var imageViewCurrent: ImageView
    private lateinit var imageViewFirstDay: ImageView
    private lateinit var imageViewSecondDay: ImageView
    private lateinit var imageViewThirdDay: ImageView
    private lateinit var imageViewFourthDay: ImageView
    private lateinit var imageViewFifthDay: ImageView

    private lateinit var imageButtonForward: ImageView

    private lateinit var weatherCommunicationChannel: WeatherCommunicationChannel
    private lateinit var cityName:String;

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is WeatherCommunicationChannel)
            weatherCommunicationChannel = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        activity?.let {
            sharedViewModel = ViewModelProviders.of(it).get(SharedViewModel::class.java)
        }
            //call api
            //city name from preference every time start the app
            if (sharedViewModel.getPreferenceCityName().isNotEmpty() && isConnectedToNetwork(requireContext())){
                cityName = sharedViewModel.getPreferenceCityName()
                weatherViewModel.getCurrentWeatherData(cityName)
                weatherViewModel.getForecasFromServer(cityName)
            }else{
                if (weatherViewModel.isCurrentWeatherReportPresent()){
                    weatherViewModel.getCurrentWeatherReportFromDb()
                    weatherViewModel.getForecastReportFromDb()
                    Log.i("check_offline",Arrays.deepToString(weatherViewModel.getForecastReportFromDb().value?.toTypedArray()))
                }else{
                    Toast.makeText(requireContext(),resources.getString(R.string.no_network),Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weather, container, false)
        setUpView(view)

        //come from city page navigation carry a bundle
        if (sharedViewModel.getCityName()!=null && isConnectedToNetwork(requireContext())){
            cityName = sharedViewModel.getCityName()!!
            weatherViewModel.getCurrentWeatherData(cityName)
            weatherViewModel.getForecasFromServer(cityName)
        }
        //pull to refresh
        pullToRefresh.setOnRefreshListener {
            if (isConnectedToNetwork(requireContext())){
                weatherViewModel.getCurrentWeatherData(cityName)
                weatherViewModel.getForecasFromServer(cityName)
            }else{
                Toast.makeText(requireContext(),resources.getString(R.string.no_network),Toast.LENGTH_SHORT).show()
            }
        }

        //get current data from db
            weatherViewModel.getCurrentWeatherReportFromDb().observe(this, Observer {
               if (it!=null)
                   updateCurrentView(it)
                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false
            })

            weatherViewModel.getForecastReportFromDb().observe(this, Observer {
                if (it!=null)
                    forecasttView(it)
                if (pullToRefresh.isRefreshing)
                    pullToRefresh.isRefreshing = false
            })

        return view
    }

    private fun setUpView(view: View) {
        (activity as AppCompatActivity).supportActionBar!!.hide()
        imageButtonForward = view.findViewById(R.id.imageButton_forward)
        imageButtonForward.setOnClickListener(this)
        pullToRefresh = view.findViewById(R.id.pullToRefresh)

        textViewCity = view.findViewById(R.id.textView_city_name)
        textViewAvgTemp = view.findViewById(R.id.textView_avg)
        textViewMinTemp = view.findViewById(R.id.textView_min)
        textViewMaxTemp = view.findViewById(R.id.textView_max)
        textViewDescription = view.findViewById(R.id.textView_type)
        textViewFirstDay = view.findViewById(R.id.textView_dt_first)
        textViewSecondDay = view.findViewById(R.id.textView_dt_second)
        textViewThirdDay = view.findViewById(R.id.textView_dt_third)
        textViewFourthDay = view.findViewById(R.id.textView_dt_fourth)
        textViewFifthDay = view.findViewById(R.id.textView_dt_fifth)
        textViewFirstDayAvgTemp = view.findViewById(R.id.textView_dt_avg_first)
        textViewSecondDayAvgTemp = view.findViewById(R.id.textView_dt_avg_second)
        textViewThirdDayAvgTemp = view.findViewById(R.id.textView_dt_avg_third)
        textViewFourthDayAvgTemp = view.findViewById(R.id.textView_dt_avg_fourth)
        textViewFifthDayAvgTemp = view.findViewById(R.id.textView_dt_avg_fifth)
        imageViewCurrent = view.findViewById(R.id.imageView)
        imageViewFirstDay = view.findViewById(R.id.imageView_first)
        imageViewSecondDay = view.findViewById(R.id.imageView_second)
        imageViewThirdDay = view.findViewById(R.id.imageView_third)
        imageViewFourthDay = view.findViewById(R.id.imageView_fourth)
        imageViewFifthDay = view.findViewById(R.id.imageView_fifth)
    }

    private fun updateCurrentView(weatherReport: WeatherReport) {
        textViewCity.text = weatherReport.cityName
        textViewAvgTemp.text =
            Html.fromHtml(weatherViewModel.convertKelvinToCelsius(weatherReport.avgTemp).toString() + "&#176")
        textViewMinTemp.text =
            Html.fromHtml(weatherViewModel.convertKelvinToCelsius(weatherReport.minTemp).toString() + "&#176")
        textViewMaxTemp.text =
            Html.fromHtml(weatherViewModel.convertKelvinToCelsius(weatherReport.maxTemp).toString() + "&#176")
        textViewDescription.text = weatherReport.weatherDescription

        if (weatherViewModel.checkInternalDirForImage(requireContext(), weatherReport.icon)) {
            val filePath = requireActivity().getFileStreamPath("${weatherReport.icon}.png")
            val imageUri = Uri.fromFile(filePath)
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView)
        } else {
            val url = Common.ICON.replace("icon_id", weatherReport.icon)
            Glide
                .with(this)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        Log.e("onLoadCleared", "onLoadCleared called in glide image $url")
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        //save data to storage
                        weatherViewModel.saveIconToInternalStorage(weatherReport.icon, resource, requireContext())
                        imageView.setImageBitmap(resource)
                    }
                })
        }
    }

    private fun forecasttView(it: List<WeatherReport>) {

        for (i in 0 until it.size) {
            when (i) {
                0 -> {
                    textViewFirstDay.text = Common.convertIntToDay(it[i].dayOfTheWeek)
                    textViewFirstDayAvgTemp.text =
                        "" + weatherViewModel.convertKelvinToCelsius(it[i].minTemp) + "/" + weatherViewModel.convertKelvinToCelsius(it[i].maxTemp)
                    if (weatherViewModel.checkInternalDirForImage(requireContext(), it[i].icon)) {
                        val filePath = requireActivity().getFileStreamPath("${it[i].icon}.png")
                        val imageUri = Uri.fromFile(filePath)
                        Glide.with(this)
                            .load(imageUri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imageViewFirstDay)
                    } else {
                        val url = Common.ICON.replace("icon_id", it[i].icon)
                        Glide
                            .with(this)
                            .asBitmap()
                            .load(url)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {
                                    Log.e("onLoadCleared", "onLoadCleared called in glide image $url")
                                }

                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    //save data to storage
                                    weatherViewModel.saveIconToInternalStorage(
                                        it[i].icon,
                                        resource,
                                        requireContext()
                                    )
                                    imageViewFirstDay.setImageBitmap(resource)
                                }
                            })
                    }
                }
                1 -> {
                    textViewSecondDay.text = Common.convertIntToDay(it[i].dayOfTheWeek)
                    textViewSecondDayAvgTemp.text =
                        "" + weatherViewModel.convertKelvinToCelsius(it[i].minTemp) + "/" + weatherViewModel.convertKelvinToCelsius(
                            it[i].maxTemp
                        )

                    if (weatherViewModel.checkInternalDirForImage(requireContext(), it[i].icon)) {
                        val filePath = requireActivity().getFileStreamPath("${it[i].icon}.png")
                        val imageUri = Uri.fromFile(filePath)
                        Glide.with(this)
                            .load(imageUri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imageViewSecondDay)
                    } else {
                        val url = Common.ICON.replace("icon_id", it[i].icon)

                        Glide
                            .with(this)
                            .asBitmap()
                            .load(url)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {
                                    Log.e("onLoadCleared", "onLoadCleared called in glide image $url")
                                }

                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    //save data to storage
                                    weatherViewModel.saveIconToInternalStorage(
                                        it[i].icon,
                                        resource,
                                        requireContext()
                                    )
                                    imageViewSecondDay.setImageBitmap(resource)
                                }
                            })
                    }
                }
                2 -> {
                    textViewThirdDay.text = Common.convertIntToDay(it[i].dayOfTheWeek)
                    textViewThirdDayAvgTemp.text =
                        "" + weatherViewModel.convertKelvinToCelsius(it[i].minTemp) + "/" + weatherViewModel.convertKelvinToCelsius(
                            it[i].maxTemp
                        )

                    if (weatherViewModel.checkInternalDirForImage(requireContext(), it[i].icon)) {
                        val filePath = requireActivity().getFileStreamPath("${it[i].icon}.png")
                        val imageUri = Uri.fromFile(filePath)
                        Glide.with(this)
                            .load(imageUri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imageViewThirdDay)
                    } else {
                        val url = Common.ICON.replace("icon_id", it[i].icon)

                        Glide
                            .with(this)
                            .asBitmap()
                            .load(url)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {
                                    Log.e("onLoadCleared", "onLoadCleared called in glide image $url")
                                }

                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    //save data to storage
                                    weatherViewModel.saveIconToInternalStorage(
                                        it[i].icon,
                                        resource,
                                        requireContext()
                                    )
                                    imageViewThirdDay.setImageBitmap(resource)
                                }
                            })
                    }

                }
                3 -> {
                    textViewFourthDay.text = Common.convertIntToDay(it[i].dayOfTheWeek)
                    textViewFourthDayAvgTemp.text =
                        "" + weatherViewModel.convertKelvinToCelsius(it[i].minTemp) + "/" + weatherViewModel.convertKelvinToCelsius(
                            it[i].maxTemp
                        )

                    if (weatherViewModel.checkInternalDirForImage(requireContext(), it[i].icon)) {
                        val filePath = requireActivity().getFileStreamPath("${it[i].icon}.png")
                        val imageUri = Uri.fromFile(filePath)
                        Glide.with(this)
                            .load(imageUri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imageViewFourthDay)
                    } else {
                        val url = Common.ICON.replace("icon_id", it[i].icon)

                        Glide
                            .with(this)
                            .asBitmap()
                            .load(url)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {
                                    Log.e("onLoadCleared", "onLoadCleared called in glide image $url")
                                }

                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    //save data to storage
                                    weatherViewModel.saveIconToInternalStorage(
                                        it[i].icon,
                                        resource,
                                        requireContext()
                                    )
                                    imageViewFourthDay.setImageBitmap(resource)
                                }
                            })
                    }

                }
                4 -> {
                    textViewFifthDay.text = Common.convertIntToDay(it[i].dayOfTheWeek)
                    textViewFifthDayAvgTemp.text =
                        "" + weatherViewModel.convertKelvinToCelsius(it[i].minTemp) + "/" + weatherViewModel.convertKelvinToCelsius(it[i].maxTemp)

                    if (weatherViewModel.checkInternalDirForImage(requireContext(), it[i].icon)) {
                        val filePath = requireActivity().getFileStreamPath("${it[i].icon}.png")
                        val imageUri = Uri.fromFile(filePath)
                        Glide.with(this)
                            .load(imageUri)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imageViewFifthDay)
                    } else {
                        val url = Common.ICON.replace("icon_id", it[i].icon)

                        Glide
                            .with(this)
                            .asBitmap()
                            .load(url)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {
                                    Log.e("onLoadCleared", "onLoadCleared called in glide image $url")
                                }

                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap>?
                                ) {
                                    //save data to storage
                                    weatherViewModel.saveIconToInternalStorage(
                                        it[i].icon,
                                        resource,
                                        requireContext()
                                    )
                                    imageViewFifthDay.setImageBitmap(resource)
                                }
                            })
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageButton_forward -> {
                weatherCommunicationChannel.goToCityFragment()
            }
        }
    }

    interface WeatherCommunicationChannel {
        fun goToCityFragment()
    }

}
