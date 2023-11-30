package com.hfad.weathera

import android.os.Parcelable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService:Parcelable {
    @GET("/v1/forecast.json")
    suspend fun getCurrentWeather(
        @Query("q") city: String?,
        @Query("key") apiKey: String ="a078778068864bfca87103302230310",
        @Query("days") days: Int=1,

        ): WeatherResponse
}