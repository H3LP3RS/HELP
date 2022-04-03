package com.github.h3lp3rs.h3lp.database.models

import java.util.*

data class EmergencyInformation(
    val id: String = UUID.randomUUID().toString(),
    var emergencyType: String,
    var location: String,
    var meds: List<String>,
)