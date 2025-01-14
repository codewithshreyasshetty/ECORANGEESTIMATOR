@file:Suppress("DEPRECATION")

package com.project.ecorangeestimater.utils

import android.location.Geocoder
import android.location.Address
import android.content.Context
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.project.ecorangeestimater.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object LocationUtils {

    fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String {
        var address = ""
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            address =
                geocoder.getFromLocation(latitude, longitude, 1)?.get(0)?.getAddressLine(0) ?: ""
        } catch (e: Exception) {
            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
        }
        return address
    }


    fun getLocality(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                address.locality
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun formatDateWithSuffix(date: Date): String {
        val dayFormat = SimpleDateFormat("d", Locale.getDefault())
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val day = dayFormat.format(date).toInt()
        val month = monthFormat.format(date)
        val time = timeFormat.format(date)

        val daySuffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }

        return "$day$daySuffix $month $time"
    }



}
