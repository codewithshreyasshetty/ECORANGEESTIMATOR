package com.project.ecorangeestimater.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE)


    companion object {
        const val USER_ID = "id"
        const val USER_ROLE = "role"
        const val USER_NAME = "name"
        const val USER_MOBILE = "mobile"
        const val USER_LOCATION = "location"
        const val USER_EMAIL = "email"
        const val USER_PASSWORD = "Password"
        const val RATING = "rating"
    }


    fun saveLoginState(
        id: String,
        role: String,
        name: String?,
        location: String,
        mobile: String,
        email: String,
        password: String,
        rating:String
    ) {
        prefs.edit().apply {
            putString(USER_ID, id)
            putString(USER_ROLE, role)
            putString(USER_NAME, name)
            putString(USER_LOCATION, location)
            putString(USER_MOBILE, mobile)
            putString(USER_EMAIL, email)
            putString(USER_PASSWORD, password)
            putString(RATING, rating)
            apply()
        }
    }



    fun isLoggedIn(): Boolean {
        val userId = prefs.getString(USER_ID, null)
        val userRole = prefs.getString(USER_ROLE, null)
        return !userRole.isNullOrEmpty() && !userId.isNullOrEmpty()
    }

    fun getUserRole(): String? {
        return prefs.getString(USER_ROLE, null)
    }

    fun getUserName(): String? {
        return prefs.getString(USER_NAME, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(USER_EMAIL, null)
    }

    fun getUserLocation(): String? {
        return prefs.getString(USER_LOCATION, null)
    }

    fun getUserPassword(): String? {
        return prefs.getString(USER_PASSWORD, null)
    }

    fun getUserMobile(): String? {
        return prefs.getString(USER_MOBILE, null)
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID, null)
    }


    fun setBatteryPercentage(battery: Int) {
        prefs.edit().putInt("battery_percentage", battery).apply()
    }

    fun getBatteryPercentage(): Int {
        return prefs.getInt("battery_percentage", -1)
    }


    fun setConsumptionRate(rate: Double) {
        prefs.edit().putFloat("consumption_rate", rate.toFloat()).apply()
    }

    fun getConsumptionRate(): Double {
        return prefs.getFloat("consumption_rate", -1f).toDouble()
    }

    fun clearLoginState() {
        prefs.edit().apply {
            clear()
            apply()
        }
    }
}