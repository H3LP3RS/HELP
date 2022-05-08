package com.github.h3lp3rs.h3lp.locationmanager

import android.content.Context
import android.location.Location
import com.github.h3lp3rs.h3lp.signin.SignInActivity
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
     */
    fun getCurrentLocation(context: Context): Location?

    /**
     * Returns the distance in meters from the user to the target coordinates, nullable
     * if the user cannot be located
     * @param coordinates The coordinates (latitude, longitude) of the target
     * @param context The activity from which the location manager is called to get
     * the user's permissions
     * @return distance (in meters)
     */
    fun distanceFrom(coordinates: Pair<Double, Double>, context: Context): Double? {
        getCurrentLocation(context)?.let {
            return getDistanceFromLatLon(Pair(it.latitude, it.longitude), coordinates)
        }
        return null
    }

    companion object {
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
            val r = 6371 // Radius of the earth in km
            val dLat = deg2rad(lat2 - lat1)
            val dLon = deg2rad(lon2 - lon1)
            val a = sin(dLat / 2) * sin(dLat / 2) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)

            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return 1000 * r * c
        }

        private fun deg2rad(deg: Double): Double {
            return deg * (PI / 180)
        }
    }
}