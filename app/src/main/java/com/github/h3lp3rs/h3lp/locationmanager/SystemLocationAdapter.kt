package com.github.h3lp3rs.h3lp.locationmanager

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener




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
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null

            // Finds the best location estimate for the user
            for (provider in providers) {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null) {
                    if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                        bestLocation = location
                    }
                }
            }
            return bestLocation
        }
        return null
    }

}