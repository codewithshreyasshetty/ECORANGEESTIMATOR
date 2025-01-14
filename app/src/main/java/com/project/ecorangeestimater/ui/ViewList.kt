package com.project.ecorangeestimater.ui

import android.os.Bundle
import android.se.omapi.Session
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.ecorangeestimater.R
import com.project.ecorangeestimater.adapter.StationAdapter
import com.project.ecorangeestimater.databinding.ActivityViewListBinding
import com.project.ecorangeestimater.response.RetrofitInstance
import com.project.ecorangeestimater.response.RetrofitInstance.TYPE
import com.project.ecorangeestimater.utils.SessionManager
import com.project.ecorangeestimater.utils.showToast
import com.project.trafficpulse.Response.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewList : AppCompatActivity() {
    private val bind by lazy { ActivityViewListBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(applicationContext) }
    private lateinit var stationAdapter: StationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        loadStations()
        stationAdapter = StationAdapter(emptyList())
        bind.recycleList.adapter = stationAdapter
        bind.recycleList.setHasFixedSize(true)
        bind.recycleList.layoutManager = LinearLayoutManager(applicationContext)

    }

    private fun loadStations() {
        CoroutineScope(IO).launch {
            RetrofitInstance.instance.getStationsEV()
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        p0: Call<LoginResponse>,
                        p1: Response<LoginResponse>
                    ) {
                        val response = p1.body()!!


                        Log.d("fkdkfhkdshfk", "Response body: ${response.data2}")

                        if (!response.error) {
                            // Ensure data2 is not null before filtering
                            val station = response.data2.filter { it.role == "evStation" && it.type == TYPE }
                                ?: emptyList() // If data2 is null, provide an empty list

                            // Check if station list is empty
                            if (station.isNotEmpty()) {
                                showToast("Successful")
                                stationAdapter.newList(station)
                            } else {
                                showToast("No stations available")
                            }
                        } else {
                            // Handle case where response is null or error flag is true
                            showToast("Failed to load the list")
                        }
                    }

                    override fun onFailure(p0: Call<LoginResponse?>, p1: Throwable) {
                        showToast(p1.message ?: "Unknown error occurred")
                    }
                })
        }
    }
}