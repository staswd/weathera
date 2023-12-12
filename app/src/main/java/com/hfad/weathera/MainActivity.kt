package com.hfad.weathera

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.hfad.weathera.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private var isFavorite: Boolean = false
    private lateinit var searchView: SearchView
    private lateinit var weatherApiService: WeatherApiService
    private lateinit var cityDao:CityDao
    private lateinit var currentCity: String

    object RetrofitInstance {
        private const val BASE_URL = "https://api.weatherapi.com/v1/"
        val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.toolbar
        onCreateOptionsMenu(toolbar.menu)
        binding.fragmentContainerView.setOnClickListener {
            searchView.clearFocus()
            searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text).setText("")
            searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text).isEnabled
        }
        weatherApiService =
            RetrofitInstance.retrofit.create(WeatherApiService::class.java)
        drawerLayout = binding.drawerLayout
        setSupportActionBar(toolbar)
        toggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        ) {
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
        }
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val drawerRecyclerView: RecyclerView = binding.drawerRecyclerView
//        val cities = listOf(
//            "odesa",
//            "lisboa",
//            "kyiv",)


        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
//
         cityDao = db.cityDao()
//        cityDao.insert(City(0,"odesa"))
//        cityDao.insert(City(0,"kyiv"))
//        cityDao.insert(City(0,"lviv"))
//        cityDao.insert(City(0,"mykolaiv"))
//        var cities: List<String>? = null
        drawerRecyclerView.layoutManager = LinearLayoutManager(this)

        MainScope().launch {
            try {
//                val db = Room.databaseBuilder(
//                    applicationContext,
//                    AppDatabase::class.java, "database-name"
//                ).build()
//
//                val cityDao=db.cityDao()
//                cityDao.insert(City(0, "odesa"))
//                cityDao.insert(City(0, "kyiv"))
////                cityDao.insert(City(0, "lviv"))
//                cityDao.insert(City(0, "mykolaiv"))
                val cities = cityDao.getAllCityNames()
                val adapter =
                    CityAdapter(cities, supportFragmentManager, drawerLayout, weatherApiService)
                drawerRecyclerView.adapter = adapter
            } catch (e: Exception) {
                println("Error: ${e.message}")
                Log.d("city", "errorr")
            }
        }
//        currentCity= binding.fragmentContainerView.findViewById<TextView>(R.id.city_name).toString()

//        val cities=cityDao.getAllCityNames()

//        val adapter =
//            CityAdapter(cities, supportFragmentManager, drawerLayout, weatherApiService)
//        drawerRecyclerView.layoutManager = LinearLayoutManager(this)
//        drawerRecyclerView.adapter = adapter
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val favItem = menu?.findItem(R.id.app_bar_favorite_button)

        if (isFavorite) {
            favItem?.setIcon(R.drawable.baseline_favorite_24)
        }
        val searchItem = menu?.findItem(R.id.app_bar_search)
        searchView = searchItem?.actionView as SearchView
        searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            .setTextColor(getColor(R.color.white))
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                toolbar.menu.findItem(R.id.app_bar_favorite_button).isVisible = false
                searchView.findViewById<View>(androidx.appcompat.R.id.search_mag_icon)?.visibility =
                    View.GONE
            } else {
                toolbar.menu.findItem(R.id.app_bar_favorite_button).isVisible = true
                searchView.isIconified = true
            }
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val city =
                    findViewById<EditText>(androidx.appcompat.R.id.search_src_text).text.toString()
                val fragment = CityFragment.newInstance(
                    city,
                    RetrofitInstance.retrofit.create(WeatherApiService::class.java)
                )
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container_view, fragment)
                transaction.commit()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.app_bar_favorite_button ->MainScope().launch {
                try {
                     updateFavoriteStatus(item,binding.fragmentContainerView.findViewById<TextView>(R.id.city_name).text.toString())

                } catch (e: Exception) {
                    println("Error: ${e.message}")
                    Log.d("city", "errorr")
                }
            }


//            R.id.app_bar_favorite_button -> updateFavoriteStatus(item)
            R.id.app_bar_search -> toolbar.menu.findItem(R.id.app_bar_favorite_button).isVisible =
                false
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun updateFavoriteStatus(favItem: MenuItem?,city:String) {
        if (isFavorite) {
            Toast.makeText(
                this,
                "Favourite is Removed ",
                Toast.LENGTH_LONG
            ).show()

//
//            binding.fragmentContainerView.findViewById<TextView>(R.id.city_name).visibility=View.GONE

            favItem?.setIcon(R.drawable.favorite_border)
            isFavorite = false
            val cityToDelete=cityDao.getCityByName(city)
            cityDao.delete(cityToDelete)
        } else {
            Toast.makeText(
                this,
                " Favourite is set",
                Toast.LENGTH_LONG
            ).show()
//            binding.fragmentContainerView.findViewById<TextView>(R.id.city_name).visibility=View.VISIBLE



            favItem?.setIcon(R.drawable.baseline_favorite_24)
            isFavorite = true
            cityDao.insert(City(0, city))

        }
    }
}



