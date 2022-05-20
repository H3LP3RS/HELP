package com.github.h3lp3rs.h3lp.locationmanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface.Companion.GET_LOCATION_EXCEPTION
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface.Companion.GET_PERMISSIONS_EXCEPTION
import com.google.android.gms.location.LocationServices
import java.util.concurrent.CompletableFuture

/**
 * This object adapts the System location manager, it is used as the central location manager in
 * the app
 */
object SystemLocationAdapter : LocationManagerInterface {

    override fun getCurrentLocation(context: Context): CompletableFuture<Location> {
        // Checking if the location permissions have been granted
        val futureLocation: CompletableFuture<Location> = CompletableFuture()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // A fused location provider uses both GPS and Network data to get an accurate
            // location estimation without requiring too much battery use
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { futureLocation.complete(it) }
                .addOnFailureListener { futureLocation.completeExceptionally(RuntimeException(GET_LOCATION_EXCEPTION)) }
        } else {
            // In case the user didn't give location permissions / removed them
            futureLocation.completeExceptionally(RuntimeException(GET_PERMISSIONS_EXCEPTION))
        }
        return futureLocation
    }
}