package com.hfad.weathera

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//data class City()

@Entity(tableName = "cities",
//    indices = [Index(value = ["city_name"], unique = true)]
)

data class City(
    @PrimaryKey(autoGenerate = true)
    public val cityId: Int,
    @ColumnInfo(name = "city_name")
     val cityName: String,
){
     fun equals(other: City?): Boolean {
        return this.cityName==other?.cityName
//        return super.equals(other)
    }
}
