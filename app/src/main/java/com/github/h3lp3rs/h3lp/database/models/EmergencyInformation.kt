package com.github.h3lp3rs.h3lp.database.models

import java.util.*

/**
 * A model describing an emergency information object
 */
data class EmergencyInformation(
    // Unique id of the emergency information object
    val id: Int,
    // Longitude of current location of the user on the map
    val latitude: Double,
    // Longitude of current location of the user on the map
    val longitude: Double,
    // List of medications selected by the user
    val meds: ArrayList<String>,
    // The time the emergency was launched
    val time: Date,
    // Medical card of the user
    val medicalInfo: String
)