package com.hfad.weathera

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.util.TimeZone
import com.hfad.weathera.databinding.FragmentCityBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


class CityFragment : Fragment() {

    companion object {
        fun newInstance(city: String, weatherApiService: WeatherApiService): CityFragment {
            return CityFragment().apply {
                arguments = Bundle().apply {
                    putString("city", city)
                    putParcelable("weatherApiService", weatherApiService)
                    println(city)
                }
            }
        }
    }

    private lateinit var binding: FragmentCityBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCityBinding.inflate(inflater, container, false)

        val arguments = arguments
        val weatherApiService =
            MainActivity.RetrofitInstance.retrofit.create(WeatherApiService::class.java)

        binding.updateButton.setOnClickListener {


            renderUIWithCoroutine(weatherApiService, binding.cityName.text.toString())
            Toast.makeText(activity,
                "Updated",
                Toast.LENGTH_SHORT).show();        }

        if (arguments != null) {
            val city = arguments.getString("city")
            renderUIWithCoroutine(weatherApiService, city)
        } else {
            renderUIWithCoroutine(weatherApiService, "odesa")
        }
        return binding.root
    }


    suspend fun weatherRequest(
        weatherApiService: WeatherApiService,
        city: String?
    ): WeatherResponse? {
        return weatherApiService.getCurrentWeather(city)
    }

    fun renderUI(weatherResponse: WeatherResponse?) {
        binding.condition.text = weatherResponse?.current?.condition?.text
        binding.currentTemperature.text = weatherResponse?.current?.temp_c.toString()
        binding.cityName.text = weatherResponse?.location?.name
        binding.currentRealFeel.text = weatherResponse?.current?.feelslike_c.toString()
        val epoch = weatherResponse?.current?.last_updated_epoch
        val date: Date = Date(epoch!! * 1000)
        val timeZone = TimeZone.getTimeZone("Europe/Kiev")
        val sdf = SimpleDateFormat("HH:mm")
        sdf.timeZone = timeZone
        binding.lastUpdate.text = sdf.format(date)
        Picasso.get().load("https:" + weatherResponse.current.condition.icon)
            .into(binding.conditionImage)
        val windText = String.format(
            "%s %s",
            weatherResponse.current.wind_dir,
            weatherResponse.current.wind_kph.toString()
        )
        binding.wind.text = windText
        val forecastText = String.format(
            "Today: %s-%s",
            weatherResponse.forecast.forecastday.firstOrNull()?.day?.mintemp_c,
            weatherResponse.forecast.forecastday.firstOrNull()?.day?.maxtemp_c
        )
        binding.forecastForToday.text = forecastText

        val chanceOfRainText = String.format(
            "Rain: %s%%",
            weatherResponse.forecast.forecastday.firstOrNull()?.day?.daily_chance_of_rain
        )
        binding.chanceOfRain.text = chanceOfRainText}


        fun renderUIWithCoroutine(weatherApiService: WeatherApiService, city: String?) {
        MainScope().launch {
            try {
                val response = weatherRequest(weatherApiService, city)
                renderUI(response)
            } catch (e: Exception) {
                println("Error: ${e.message}")
                Log.d("city","errorr")
            }
        }
    }
}
