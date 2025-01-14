package com.project.ecorangeestimater.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.project.ecorangeestimater.R
import com.project.ecorangeestimater.databinding.ActivityMapsBinding
import com.project.ecorangeestimater.utils.LocationUtils

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val binding by lazy { ActivityMapsBinding.inflate(layoutInflater) }
    private lateinit var mMap: GoogleMap
    private val fused by lazy { LocationServices.getFusedLocationProviderClient(this) }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        fused.lastLocation.addOnSuccessListener {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f))
        }

        mMap.setOnMapClickListener {
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(it).title("Selected Location"))


            binding.location.text =
                LocationUtils.getAddressFromLatLng(applicationContext, it.latitude, it.longitude)



            binding.save.setOnClickListener { _ ->
                val intent = Intent().apply {
                    putExtra("latitude", it.latitude)
                    putExtra("longitude", it.longitude)
                }
                setResult(RESULT_OK, intent)
                finish()
            }


        }


    }

}
