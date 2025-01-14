package com.project.ecorangeestimater.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.project.ecorangeestimater.databinding.ActivityAddstationBinding
import com.project.ecorangeestimater.model.AdapterType
import com.project.ecorangeestimater.response.CommonResponse
import com.project.ecorangeestimater.response.RetrofitInstance
import com.project.ecorangeestimater.utils.LocationUtils
import com.project.ecorangeestimater.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStationActivity : AppCompatActivity() {

    private val binding by lazy { ActivityAddstationBinding.inflate(layoutInflater) }
    private lateinit var gestureDetector: GestureDetector
    private lateinit var getRequestLauncher: ActivityResultLauncher<Intent>
    private var lat: Double? = null
    private var lng: Double? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.titleTextView.text = "Add EV Station"

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val intent = Intent(this@AddStationActivity, MapsActivity::class.java)
                getRequestLauncher.launch(intent)
                return true
            }
        })

        getRequestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = it.data
                    lat = data?.getDoubleExtra("latitude", 0.0)
                    lng = data?.getDoubleExtra("longitude", 0.0)

                    if (lat != null && lng != null) {
                        val address = LocationUtils.getAddressFromLatLng(
                            applicationContext,
                            lat!!,
                            lng!!
                        )
                        showToast("$lat,$lng")

                        binding.etLocation.setText(address)
                    }
                }
            }

        binding.etLocation.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }

        binding.buttonConfirm.setOnClickListener {
            val stationName = binding.etStationName.text.toString().trim()
            val stationMobile = binding.etStationMobile.text.toString().trim() // Ensure you have this field in your layout
            val location = binding.etLocation.text.toString().trim()
            val chargingPoints = binding.etChargingPoints.text.toString().trim()

            val selectedAdapters = getSelectedAdapters()

            when {
                stationName.isEmpty() -> showToast("Please enter the station name")
                stationMobile.isEmpty() -> showToast("Please enter the station mobile number") // Added
                location.isEmpty() -> showToast("Please enter the location")
                chargingPoints.isEmpty() -> showToast("Please enter a valid number of charging points") // Updated
                selectedAdapters.isEmpty() -> showToast("Please select at least one adapter type")
                else -> {
                    binding.loadingIndicator.visibility = View.VISIBLE
                    binding.buttonConfirm.isEnabled = false

                    CoroutineScope(Dispatchers.IO).launch {

                        try {
                            RetrofitInstance.instance.addEVStation(
                                stationName = stationName,
                                stationMobile = stationMobile,
                                location = "$lat,$lng",
                                chargingPoints = chargingPoints,
                                type = RetrofitInstance.TYPE,
                                role = "evStation",
                                adapterTypes = selectedAdapters
                            ).enqueue(object : Callback<CommonResponse?> {
                                override fun onResponse(
                                    call: Call<CommonResponse?>,
                                    response: Response<CommonResponse?>
                                ) {
                                    runOnUiThread {
                                        binding.loadingIndicator.visibility = View.GONE
                                        binding.buttonConfirm.isEnabled = true
                                        if (response.isSuccessful) {
                                            val result = response.body()
                                            if (result != null && !result.error) {
                                                showToast(result.message)
                                                finish()
                                            } else {
                                                showToast("Failed to add the EV Station")
                                            }
                                        } else {
                                            showToast("Error: ${response.message()}")
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                                    runOnUiThread {
                                        binding.loadingIndicator.visibility = View.GONE
                                        binding.buttonConfirm.isEnabled = true
                                        showToast("Network error: ${t.message}")
                                    }
                                }
                            })

                        } catch (e: Exception) {
                            runOnUiThread {
                                binding.loadingIndicator.visibility = View.GONE
                                binding.buttonConfirm.isEnabled = true
                                showToast("Network error: ${e.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getSelectedAdapters(): List<String> {
        val selectedAdapters = mutableListOf<String>()
        if (binding.checkboxType1.isChecked) selectedAdapters.add(AdapterType.TYPE1.displayName)
        if (binding.checkboxType2.isChecked) selectedAdapters.add(AdapterType.TYPE2.displayName)
        if (binding.checkboxType3.isChecked) selectedAdapters.add(AdapterType.CCS.displayName)
        if (binding.checkboxType4.isChecked) selectedAdapters.add(AdapterType.CHADEMO.displayName)
        if (binding.checkboxType5.isChecked) selectedAdapters.add(AdapterType.TESLA.displayName)
        if (binding.checkboxType6.isChecked) selectedAdapters.add(AdapterType.GB_T.displayName)
        return selectedAdapters
    }
}
