package com.github.h3lp3rs.h3lp.locationmanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import java.util.concurrent.CompletableFuture




/**
 * This object adapts the System location manager, it is used as the central location manager in
 * the app
 */
object SystemLocationAdapter : LocationManagerInterface {
    private const val GET_LOCATION_EXCEPTION = "Location could not be retrieved"
    private const val GET_PERMISSIONS_EXCEPTION = "Location permissions were not granted"

    /**
     * Gets the user's current location from the system location manager
     * @param context The activity from which the location manager is called to check the user's
     * permissions
     */
    override fun getCurrentLocation(context: Context): CompletableFuture<Location> {
        // Checking if the location permissions have been granted
        val futureLocation: CompletableFuture<Location> = CompletableFuture()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { futureLocation.complete(it) }
                .addOnFailureListener { futureLocation.completeExceptionally(RuntimeException(GET_LOCATION_EXCEPTION)) }
        } else {
            futureLocation.completeExceptionally(RuntimeException(GET_PERMISSIONS_EXCEPTION))
        }
        return futureLocation
    }

}