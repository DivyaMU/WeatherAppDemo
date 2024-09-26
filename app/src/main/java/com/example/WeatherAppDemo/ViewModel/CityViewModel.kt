package com.example.WeatherAppDemo.ViewModel

import androidx.lifecycle.ViewModel
import com.example.WeatherAppDemo.Repository.CityRepository
import com.example.WeatherAppDemo.Server.ApiClient
import com.example.WeatherAppDemo.Server.ApiServices

class CityViewModel(val repository: CityRepository) : ViewModel() {
    constructor() : this(CityRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCity(q: String, limit: Int) =
        repository.getCities(q, limit)
}