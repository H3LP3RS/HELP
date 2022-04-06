package com.github.h3lp3rs.h3lp.database.models

import java.util.*

/**
 * A model describing an emergency information object
 */
data class EmergencyInformation(
    val id: String = UUID.randomUUID().toString(),
    val latitude: Double,
    val longitude: Double,
    val meds: ArrayList<String>,
    val time: Date,
    val medicalInfo: String
)