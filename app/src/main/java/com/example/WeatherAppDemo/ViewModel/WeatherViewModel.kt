package com.example.WeatherAppDemo.ViewModel

import androidx.lifecycle.ViewModel
import com.example.WeatherAppDemo.Repository.WeatherRepository
import com.example.WeatherAppDemo.Server.ApiClient
import com.example.WeatherAppDemo.Server.ApiServices

class WeatherViewModel(val repository: WeatherRepository) : ViewModel() {

    constructor() : this(WeatherRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCurrentWeather(lat: Double, lng: Double, unit: String) =
        repository.getCurrentWeather(lat, lng, unit)

    fun loadForecastWeather(lat: Double, lng: Double, unit: String) =
        repository.getForecastWeather(lat, lng, unit)


}