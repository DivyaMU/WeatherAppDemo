package com.example.WeatherAppDemo.Repository

import com.example.WeatherAppDemo.Server.ApiServices

class CityRepository(val api: ApiServices) {
    fun getCities(q: String, limit: Int) =
        api.getCitiesList(q, limit, "ffde7b8b4db3f4366fbcb3f91e1a25e4")
}