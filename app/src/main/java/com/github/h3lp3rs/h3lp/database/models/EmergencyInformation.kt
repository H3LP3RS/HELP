package com.github.h3lp3rs.h3lp.database.models

import java.util.*

data class EmergencyInformation(
    val id: String = UUID.randomUUID().toString(),
    var latitude: Double,
    var longitude: Double,
    var meds: ArrayList<String>,
    var time: Date
)