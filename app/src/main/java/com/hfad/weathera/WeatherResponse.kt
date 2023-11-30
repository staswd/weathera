package com.hfad.weathera

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val current: CurrentWeather,
    val location: CurrentLocation,
    val forecast: Forecast,
)

data class CurrentWeather(
    val last_updated_epoch: Long,
    val temp_c: Double,
    val condition: Condition,
    val feelslike_c: String,
    val wind_kph: Double,
    val wind_dir: String,

    )

data class CurrentLocation(
    val name: String,

    )

data class Condition(
    val text: String,
    val icon: String,
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val day: Day
)

data class Day(
    val mintemp_c: Double,
    val maxtemp_c: Double,
    val daily_chance_of_rain: Int
)