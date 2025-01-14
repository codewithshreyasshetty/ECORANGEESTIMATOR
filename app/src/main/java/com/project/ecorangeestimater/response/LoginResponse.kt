package com.project.trafficpulse.Response

import com.project.ecorangeestimater.model.Entries
import com.project.ecorangeestimater.model.EvStation


data class LoginResponse(
    var error: Boolean,
    var message: String,
    var data: ArrayList<Entries>,
    var data2: ArrayList<EvStation>



)
