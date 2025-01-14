package com.project.ecorangeestimater.ui


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.ecorangeestimater.R
import com.project.ecorangeestimater.model.Entries
import com.project.ecorangeestimater.model.EvStation
import com.project.ecorangeestimater.response.RetrofitInstance
import com.project.ecorangeestimater.response.RetrofitInstance.TYPE
import com.project.ecorangeestimater.utils.SessionManager
import com.project.ecorangeestimater.utils.showToast
import com.project.trafficpulse.Response.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val fused by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val shared by lazy { SessionManager(applicationContext) }
    private var lat = 0.0
    private var lng = 0.0
    private var userLocation = LatLng(0.0, 0.0)
    private var role: String = ""

    private val evStations = mutableListOf<EvStation>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initializeLocation()
        } else {
            showToast("Location permission is required to use this feature.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //calculateNearestStation()


        val fabSetParameters = findViewById<FloatingActionButton>(R.id.fab_set_parameters)
        fabSetParameters.setOnClickListener {
            showInputDialog()
        }

        role = shared.getUserRole() ?: ""


        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {

                initializeLocation()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {

                MaterialAlertDialogBuilder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app requires location access to display EV stations near you.")
                    .setPositiveButton("OK") { _, _ ->

                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .setCancelable(false)
                    .show()
            }

            else -> {

                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }


        loadLocations()
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocation() {
        fused.lastLocation.addOnSuccessListener { location ->
            location?.let {
                lat = it.latitude
                lng = it.longitude
            }
            userLocation = LatLng(lat, lng)
            Log.d("UserLocation", "onCreate: $userLocation")

            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    private fun showInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_user_input, null)
        val etBattery = dialogView.findViewById<EditText>(R.id.etBatteryPercentage)
        val etConsumption = dialogView.findViewById<EditText>(R.id.etConsumptionRate)


        val existingBattery = shared.getBatteryPercentage()
        if (existingBattery != -1) {
            etBattery.setText(existingBattery.toString())
        }

        val existingConsumption = shared.getConsumptionRate()
        if (existingConsumption != -1.0) {
            etConsumption.setText(existingConsumption.toString())
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Set Your EV Parameters")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val batteryInput = etBattery.text.toString()
                val consumptionInput = etConsumption.text.toString()

                if (batteryInput.isBlank() || consumptionInput.isBlank()) {
                    showToast("Please fill all fields.")
                    return@setPositiveButton
                }

                val battery = batteryInput.toIntOrNull()
                val consumption = consumptionInput.toDoubleOrNull()

                if (battery == null || consumption == null || battery < 0 || battery > 100 || consumption <= 0) {
                    showToast("Please enter valid values.")
                    return@setPositiveButton
                }


                shared.setBatteryPercentage(battery)
                shared.setConsumptionRate(consumption)

                showToast("Parameters saved.")
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun loadLocations() {
        RetrofitInstance.instance.getStationsEV()
            .enqueue(object : Callback<LoginResponse?> {
                override fun onResponse(
                    call: Call<LoginResponse?>,
                    response: Response<LoginResponse?>,
                ) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        val data = responseBody.data2
                        val locations = data.filter { it.type == TYPE }

                        evStations.clear()

                        locations.forEach { station ->
                            evStations.add(station)
                        }

                        if (::mMap.isInitialized) {
                            mMap.clear()
                            addUserMarker(userLocation)
                            addEvStations()
                        }
                    } else {
                        showToast(responseBody?.message ?: "Unknown error")
                    }
                }

                override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                    showToast(t.message ?: "Network Error")
                }
            })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        addUserMarker(userLocation)
        addEvStations()

        mMap.setOnMarkerClickListener { marker ->

            when (marker.tag) {
                "USER" -> {
                    true
                }

                is EvStation -> {
                    val station = marker.tag as EvStation
                    showEstimationBottomSheet(station)
                    true
                }

                else -> {
                    showToast("Unknown Marker")
                    true
                }
            }
        }
    }

    private fun addUserMarker(location: LatLng) {
        val userIcon = BitmapDescriptorFactory.fromResource(R.drawable.car)
        val userMarker = mMap.addMarker(
            MarkerOptions()
                .position(location)
                .title("Your Location")
                .icon(userIcon)
        )
        userMarker?.tag = "USER"
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
    }

    private fun addEvStations() {
        evStations.forEach { station ->
            val evIcon = BitmapDescriptorFactory.fromResource(R.drawable.station)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(parseLatLng(station.location))
                    .title(station.stationName)
                    .icon(evIcon)
            )
            marker?.tag = station
        }
    }

    private fun parseLatLng(location: String): LatLng {
        val parts = location.split(",")
        val lat = parts[0].trim().toDouble()
        val lng = parts[1].trim().toDouble()
        return LatLng(lat, lng)
    }

    private fun showEstimationBottomSheet(station: EvStation) {
        val batteryPercentage = shared.getBatteryPercentage()
        val consumptionRate = shared.getConsumptionRate()

        if (batteryPercentage == -1 || consumptionRate == -1.0) {
            showToast("Please set your battery percentage and consumption rate.")
            return
        }

        val stationLatLng = parseLatLng(station.location)
        val distanceKm = haversine(
            userLocation.latitude,
            userLocation.longitude,
            stationLatLng.latitude,
            stationLatLng.longitude
        )
        val formattedDistanceKm = String.format("%.1f km", distanceKm)

        val totalRange = 100 / consumptionRate
        val estimatedRange = (batteryPercentage / 100.0) * totalRange

        val canReach = estimatedRange >= distanceKm
        val estimationMessage = if (canReach) {
            "You can reach this station with your current battery level ($batteryPercentage%)."
        } else {
            "Your battery level ($batteryPercentage%) is insufficient. Consider recharging."
        }
        val suggestionMessage = getSuggestionMessage(batteryPercentage, canReach)


        CoroutineScope(Dispatchers.IO).launch {
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            val stationAddress = try {
                val addresses = geocoder.getFromLocation(
                    stationLatLng.latitude,
                    stationLatLng.longitude,
                    1
                )
                if (addresses != null && addresses.isNotEmpty()) {
                    addresses[0].getAddressLine(0) ?: "Unknown Location"
                } else {
                    "Unknown Location"
                }
            } catch (e: Exception) {
                Log.e("Geocoder", "Error getting station name", e)
                "Unknown Location"
            }


            withContext(Dispatchers.Main) {

                EstimationBottomSheet(
                    stationName = station.stationName,
                    stationMobile = station.stationMobile,
                    location = stationAddress,
                    chargingPoints = station.chargingpoints,
                    adapterTypes = station.adapterTypes,
                    distance = formattedDistanceKm,
                    estimation = estimationMessage,
                    suggestion = suggestionMessage
                ).show(
                    supportFragmentManager,
                    "EstimationBottomSheet"
                )
            }
        }
    }


    private fun getSuggestionMessage(batteryPercentage: Int, canReach: Boolean): String {
        if (batteryPercentage < 40) {
            val nearestInfo = findNearestStationInfo()
            return if (canReach) {
                "Your battery is low! You can still reach this station, but consider recharging. Nearest station for next time: ${nearestInfo.name} is ${nearestInfo.distance} away."
            } else {
                "Your battery is below 40% and you may not reach this station. Consider recharging soon. Nearest station is ${nearestInfo.name}, located ${nearestInfo.distance} away."
            }
        }

        return when {
            batteryPercentage >= 80 && canReach -> "Excellent! You have ample battery to reach the station."
            batteryPercentage in 60..79 && canReach -> "Good! You can reach the station with your current battery."
            batteryPercentage in 40..59 && canReach -> "Fair! You can reach the station, but consider recharging soon."
            !canReach -> "Cannot reach the station with your current battery level. Please make alternate arrangements."
            else -> "Low! You can reach the station, but it's advisable to recharge your EV."
        }
    }

    private fun findNearestStationInfo(): NearestStationInfo {
        var nearestStation: EvStation? = null
        var shortestDistance = Double.MAX_VALUE

        evStations.forEach { station ->
            val stationLatLng = parseLatLng(station.location)
            val distance = haversine(
                userLocation.latitude,
                userLocation.longitude,
                stationLatLng.latitude,
                stationLatLng.longitude
            )
            if (distance < shortestDistance) {
                shortestDistance = distance
                nearestStation = station
            }
        }

        val formattedDistance = String.format("%.1f km", shortestDistance)
        return NearestStationInfo(nearestStation?.stationName ?: "Unknown", formattedDistance)
    }

    data class NearestStationInfo(val name: String, val distance: String)

    private fun calculateNearestStation() {
        var nearestStation: EvStation? = null
        var shortestDistance = Double.MAX_VALUE

        evStations.forEach { station ->
            val stationLatLng = parseLatLng(station.location)
            val distance = haversine(
                userLocation.latitude,
                userLocation.longitude,
                stationLatLng.latitude,
                stationLatLng.longitude
            )
            if (distance < shortestDistance) {
                shortestDistance = distance
                nearestStation = station
            }
        }

        if (nearestStation == null) {
            return
        }

        val formattedDistance = String.format("%.1f km", shortestDistance)
        val estimationMessage = "You can reach this station with your current battery level."
        val suggestionMessage = "Great! Safe travels!"


        CoroutineScope(Dispatchers.IO).launch {
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            val stationAddress = try {
                val addresses = geocoder.getFromLocation(
                    parseLatLng(nearestStation!!.location).latitude,
                    parseLatLng(nearestStation!!.location).longitude,
                    1
                )
                if (addresses != null && addresses.isNotEmpty()) {
                    addresses[0].getAddressLine(0) ?: "Unknown Location"
                } else {
                    "Unknown Location"
                }
            } catch (e: Exception) {
                Log.e("Geocoder", "Error getting station name", e)
                "Unknown Location"
            }

            withContext(Dispatchers.Main) {

                EstimationBottomSheet(
                    stationName = nearestStation!!.stationName,
                    stationMobile = nearestStation!!.stationMobile,
                    location = stationAddress,
                    chargingPoints = nearestStation!!.chargingpoints,
                    adapterTypes = nearestStation!!.adapterTypes,
                    distance = formattedDistance,
                    estimation = estimationMessage,
                    suggestion = suggestionMessage
                ).show(
                    supportFragmentManager,
                    "EstimationBottomSheet"
                )
            }
        }
    }


    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}

