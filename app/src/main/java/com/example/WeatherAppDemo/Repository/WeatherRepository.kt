package com.example.WeatherAppDemo.Repository

import com.example.WeatherAppDemo.Server.ApiServices

class WeatherRepository(val api:ApiServices) {

    fun getCurrentWeather(lat: Double,lng:Double,unit:String)=
        api.getCurrentWeather(lat,lng,unit,"ffde7b8b4db3f4366fbcb3f91e1a25e4")

    fun getForecastWeather(lat: Double,lng:Double,unit:String)=
        api.getForecastWeather(lat,lng,unit,"ffde7b8b4db3f4366fbcb3f91e1a25e4")
}