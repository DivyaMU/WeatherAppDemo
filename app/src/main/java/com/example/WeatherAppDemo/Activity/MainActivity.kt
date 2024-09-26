package com.example.WeatherAppDemo.Activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.WeatherAppDemo.Adapter.ForecastAdapter
import com.example.WeatherAppDemo.R
import com.example.WeatherAppDemo.ViewModel.WeatherViewModel
import com.example.WeatherAppDemo.databinding.ActivityMainBinding
import com.example.WeatherAppDemo.model.CurrentResponseApi
import com.example.WeatherAppDemo.model.ForecastResponseApi
import com.github.matteobattilana.weather.PrecipType
import eightbitlab.com.blurview.RenderScriptBlur
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar
import java.util.TimeZone
import com.google.android.gms.location.FusedLocationProviderClient
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }

        // I am unable to retrieve the current location . Logic - Find current location , 1.pass the lat lang to the
       // geocode api and get the city name 2. Show the current location name in the Main Screen Text, 3. If user change the lcoation , then update the text(location) based
        //on the selection . I just commented the current location code.


       //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
// Request current location
        //requestCurrentLocation()

        binding.apply {
            var lat = intent.getDoubleExtra("lat", 0.0)
            var lon = intent.getDoubleExtra("lon", 0.0)
            var name = intent.getStringExtra("name")

            //Hardcoding values if lat,lang empty
            if (lat == 0.0) {
                lat = 40.73
                lon = -73.93
                name = "New York"
            }

            //Navigating to next activity
            addCity.setOnClickListener {
                startActivity(Intent(this@MainActivity, CityListActivity::class.java))
            }
            //current Temp
            cityTxt.text = name
            progressBar.visibility = View.VISIBLE
            weatherViewModel.loadCurrentWeather(lat, lon, "metric").enqueue(object :
                retrofit2.Callback<CurrentResponseApi> {
                override fun onResponse(
                    call: Call<CurrentResponseApi>,
                    response: Response<CurrentResponseApi>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        progressBar.visibility = View.GONE
                        detailLayout.visibility = View.VISIBLE
                        data?.let {
                            statusTxt.text = it.weather?.get(0)?.main ?: "-"
                            windTxt.text = it.wind?.speed?.let { Math.round(it).toString() } + "Km"
                            humidityTxt.text = it.main?.humidity?.toString() + "%"
                            currentTempTxt.text =
                                it.main?.temp?.let { Math.round(it).toString() } + "°"
                            maxTempTxt.text =
                                it.main?.tempMax?.let { Math.round(it).toString() } + "°"
                            minTempTxt.text =
                                it.main?.tempMin?.let { Math.round(it).toString() } + "°"

                            val drawable = if (isNightNow()) R.drawable.night_bg
                            else {
                                setDynamicallyWallpaper(it.weather?.get(0)?.icon ?: "-")
                            }
                            bgImage.setImageResource(drawable)
                            setEffectRainSnow(it.weather?.get(0)?.icon ?: "-")
                        }
                    }
                }

                override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                }

            })


            //settings Blue View
            var radius = 10f
            val decorView = window.decorView
            val rootView = (decorView.findViewById(android.R.id.content) as ViewGroup?)
            val windowBackground = decorView.background

            rootView?.let {
                blueView.setupWith(it, RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blueView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blueView.clipToOutline = true
            }


            //forecast temp
            weatherViewModel.loadForecastWeather(lat, lon, "metric")
                .enqueue(object : retrofit2.Callback<ForecastResponseApi> {
                    override fun onResponse(
                        call: Call<ForecastResponseApi>,
                        response: Response<ForecastResponseApi>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            blueView.visibility = View.VISIBLE

                            data?.let {
                                forecastAdapter.differ.submitList(it.list)
                                forecastView.apply {
                                    layoutManager = LinearLayoutManager(
                                        this@MainActivity,
                                        LinearLayoutManager.HORIZONTAL,
                                        false
                                    )
                                    adapter = forecastAdapter
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {

                    }

                })
        }

    }

    private fun isNightNow(): Boolean {
        calendar.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

    //Dynamic wallpaper based on the weatherforecast api.

    private fun setDynamicallyWallpaper(icon: String): Int {
        return when (icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.snow_bg
            }

            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }

            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }

            "13" -> {
                initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }

            "50" -> {
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }

            else -> 0
        }
    }

    private fun setEffectRainSnow(icon: String) {
        when (icon.dropLast(1)) {
            "01" -> {
                initWeatherView(PrecipType.CLEAR)

            }

            "02", "03", "04" -> {
                initWeatherView(PrecipType.CLEAR)

            }

            "09", "10", "11" -> {
                initWeatherView(PrecipType.RAIN)

            }

            "13" -> {
                initWeatherView(PrecipType.SNOW)

            }

            "50" -> {
                initWeatherView(PrecipType.CLEAR)

            }

        }
    }

    private fun initWeatherView(type: PrecipType) {
        binding.weatherView.apply {
            setWeatherData(type)
            angle = -20
            emissionRate = 100.0f
        }
    }

//    private fun requestCurrentLocation() {
//        // Check if the permission is granted
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Request permission
//            requestLocationPermission()
//            return
//        }
//
//        // Get last known location
//        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//            if (location != null) {
//                // Use the retrieved location to update weather
//                updateWeatherBasedOnLocation(location.latitude, location.longitude)
//            } else {
//                // Handle case where location is null (possibly due to disabled location services)
//                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
//            }
//        }.addOnFailureListener {
//            Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun requestLocationPermission() {
//        val requestPermissionLauncher = registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//                // Permission is granted, request the current location again
//                requestCurrentLocation()
//            } else {
//                // Permission denied
//                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//    }
//
//    private fun updateWeatherBasedOnLocation(lat: Double, lon: Double) {
//        // Now update your weather data with the current location's latitude and longitude
//        weatherViewModel.loadCurrentWeather(lat, lon, "metric").enqueue(object :
//            retrofit2.Callback<CurrentResponseApi> {
//            override fun onResponse(
//                call: Call<CurrentResponseApi>,
//                response: Response<CurrentResponseApi>
//            ) {
//                if (response.isSuccessful) {
//                    val data = response.body()
//                    binding.progressBar.visibility = View.GONE
//                    binding.detailLayout.visibility = View.VISIBLE
//                    data?.let {
//                        binding.statusTxt.text = it.weather?.get(0)?.main ?: "-"
//                        binding.windTxt.text = it.wind?.speed?.let { Math.round(it).toString() } + "Km"
//                        binding.humidityTxt.text = it.main?.humidity?.toString() + "%"
//                        binding.currentTempTxt.text =
//                            it.main?.temp?.let { Math.round(it).toString() } + "°"
//                        binding.maxTempTxt.text =
//                            it.main?.tempMax?.let { Math.round(it).toString() } + "°"
//                        binding.minTempTxt.text =
//                            it.main?.tempMin?.let { Math.round(it).toString() } + "°"
//
//                        val drawable = if (isNightNow()) R.drawable.night_bg
//                        else {
//                            setDynamicallyWallpaper(it.weather?.get(0)?.icon ?: "-")
//                        }
//                        binding.bgImage.setImageResource(drawable)
//                        setEffectRainSnow(it.weather?.get(0)?.icon ?: "-")
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
//                Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
}