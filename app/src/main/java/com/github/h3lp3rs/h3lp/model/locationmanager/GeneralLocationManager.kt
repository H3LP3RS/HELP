package com.github.h3lp3rs.h3lp.model.locationmanager

object GeneralLocationManager {
    // locationManager contains the currently used location manager
    private var locationManager: LocationManagerInterface? = null

    /**
     * Returns the current location manager (the default location manager method is the one on the
     * system, unless set otherwise)
     * @return The current location manager
     */
    fun get(): LocationManagerInterface {
        locationManager = locationManager ?: SystemLocationAdapter
        return locationManager!!
    }

    /**
     * Used for testing purposes to give mock location manager instances, can also be used to enable
     * multiple location managers for the app
     * @param newLocationManager the new location manager
     */
    fun set(newLocationManager: LocationManagerInterface) {
        locationManager = newLocationManager
    }

    /**
     * Sets the location manager to the default one
     */
    fun setDefaultSystemManager() {
        locationManager = SystemLocationAdapter
    }
}
