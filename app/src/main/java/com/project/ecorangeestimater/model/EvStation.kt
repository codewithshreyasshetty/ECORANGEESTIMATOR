package com.project.ecorangeestimater.model

data class EvStation(
    val stationName: String,
    val stationMobile: String,
    val location: String,
    val chargingpoints: String,
    val type: String,
    val role: String,
    val adapterTypes: List<String>
)
