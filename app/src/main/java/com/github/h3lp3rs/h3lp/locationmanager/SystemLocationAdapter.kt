package com.github.h3lp3rs.h3lp.locationmanager

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat

/**
 * This object adapts the System location manager, it is used as the central location manager in
 * the app
 */
object SystemLocationAdapter : LocationManagerInterface {

    /**
     * Gets the user's current location from the system location manager
     * @param context The activity from which the location manager is called to check the user's
     * permissions
     */
    override fun getCurrentLocation(context: Context): Location? {
        // Checking if the location permissions have been granted
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
            val provider = locationManager.getBestProvider(Criteria(), true)
            return provider?.let { locationManager.getLastKnownLocation(it) }
        }
        return null
    }
}