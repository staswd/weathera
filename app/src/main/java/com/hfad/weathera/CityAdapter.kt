package com.hfad.weathera

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView

class CityAdapter(
    private val cities: List<String>,
    private val fragmentManager: FragmentManager,
    private val drawerLayout: DrawerLayout,
    private val weatherApiService: WeatherApiService
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val cityNameTextView: TextView = itemView.findViewById(R.id.item_city_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)
        println("onCreateViewHolder called")

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityNameTextView.text = cities[position]
        Log.d("CityAdapter", "Item binded: ${cities[position]}")




        holder.itemView.setOnClickListener {

            drawerLayout.closeDrawer(GravityCompat.START)
            Log.d("CityAdapter", "Item clicked: ${cities[position]}")
            val fragment = CityFragment.newInstance(cities[position], weatherApiService)


            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container_view, fragment)
            transaction.commit()
        }


        Log.d("CityAdapter", "Item clicked:       ${holder.itemView.hasOnClickListeners()} ")
        Log.d("CityAdapter", "Item clicked:       ${holder.cityNameTextView.hasOnClickListeners()} ")
    }

    override fun getItemCount(): Int {
        return cities.size
    }
}






