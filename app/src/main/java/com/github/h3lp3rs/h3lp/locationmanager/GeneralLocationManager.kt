package com.github.h3lp3rs.h3lp.locationmanager

import com.github.h3lp3rs.h3lp.signin.GoogleSignInAdaptor

object GeneralLocationManager {
    private var locationManager: LocationManagerInterface? = null

    fun get(): LocationManagerInterface {
        locationManager = locationManager ?: SystemLocationAdapter
        return locationManager!!
    }

    fun set(newLocationManager: LocationManagerInterface) {
        locationManager = newLocationManager
    }
}