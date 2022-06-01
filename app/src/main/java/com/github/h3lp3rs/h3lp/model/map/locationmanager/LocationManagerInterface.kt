package com.github.h3lp3rs.h3lp.locationmanager

import android.content.Context
import android.location.Location
import java.util.concurrent.CompletableFuture
import kotlin.math.*

/**
 * General interface displaying the methods required from a location manager in the context of the
 * app
 */
interface LocationManagerInterface {

    /**
     * Gets the user's current location
     * @param context The activity from which the location manager is called to get the user's
     * permissions
     * @return A future with the user's current location or a future completing exceptionally in
     * case the user didn't have location permissions activated (fails with
     * GET_PERMISSIONS_EXCEPTION message) or there was an exception while getting their location
     * (fails with GET_LOCATION_EXCEPTION message)
     */
    fun getCurrentLocation(context: Context): CompletableFuture<Location>

    /**
     * Returns the distance in meters from the user to the target coordinates, future fails
     * if the user's location cannot be retrieved
     * @param coordinates The coordinates (latitude, longitude) of the target
     * @param context The activity from which the location manager is called to get
     * the user's permissions
     * @return distance (in meters)
     */
    fun distanceFrom(
        coordinates: Pair<Double, Double>,
        context: Context
    ): CompletableFuture<Double> {
        return getCurrentLocation(context).thenApply {
            getDistanceFromLatLon(Pair(it.latitude, it.longitude), coordinates)
        }
    }

    companion object {
        // Names of the runtime exceptions to throw when getCurrentLocation fails
        const val GET_LOCATION_EXCEPTION = "Location could not be retrieved"
        const val GET_PERMISSIONS_EXCEPTION = "Location permissions were not granted"
        private const val EARTH_RADIUS = 6371 // Radius of the earth in km

        /**
         * Calculates the distance in meters between two lat/lon points on Earth
         * Code adapted from https://forum.mendix.com/link/questions/6986
         * @param coordinates1
         * @param coordinates2
         * @return distance (in meters)
         */
        fun getDistanceFromLatLon(
            coordinates1: Pair<Double, Double>,
            coordinates2: Pair<Double, Double>
        ): Double {
            val (lat1, lon1) = coordinates1
            val (lat2, lon2) = coordinates2
            val dLat = deg2rad(lat2 - lat1)
            val dLon = deg2rad(lon2 - lon1)
            val a = sin(dLat / 2) * sin(dLat / 2) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)

            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return 1000 * EARTH_RADIUS * c
        }

        private fun deg2rad(deg: Double): Double {
            return deg * (PI / 180)
        }
    }
}