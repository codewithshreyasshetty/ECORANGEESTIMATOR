package com.project.ecorangeestimater.response

import com.project.ecorangeestimater.model.Entries
import com.project.ecorangeestimater.model.EvStation


data class CommonResponse(
    var error:Boolean,
    var message:String,
    var data: ArrayList<Entries>,
    var data2: ArrayList<EvStation>

)
