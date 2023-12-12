package com.hfad.weathera

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CityDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: City)

    @Delete
    suspend fun delete(city: City)
    @Query("SELECT * FROM cities")
    suspend fun getAllCities(): List<City>
    @Query("SELECT city_name FROM cities")
    suspend fun getAllCityNames(): List<String>
    @Query("SELECT city_name FROM cities WHERE cityId=:cityId")
    suspend fun getCityNameById(cityId: Int): String
    @Query("SELECT * FROM cities WHERE city_name=:cityName")
    suspend fun getCityByName(cityName: String): City
}