package com.github.h3lp3rs.h3lp.model.locationmanager

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import java.util.concurrent.CompletableFuture

/**
 * SuperActivity that is common to all activities using location
 */
class LocationHelper {
    private var userLocation: Location? = null

    fun getUserLatitude(): Double? {
        return userLocation?.latitude
    }

    fun getUserLongitude(): Double? {
        return userLocation?.longitude
    }

    /**
     * Function that updates the user's current coordinates
     * @param context The context of the activity using the coordinates
     */
    fun updateCoordinates(context: Context) {
        val futureLocation = GeneralLocationManager.get().getCurrentLocation(context)
        futureLocation.thenAccept {
            userLocation = Location(LocationManager.GPS_PROVIDER)
            userLocation?.longitude = it.longitude
            userLocation?.latitude = it.latitude
        }
    }

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
        return GeneralLocationManager.get().distanceFrom(coordinates, context)
    }

    /**
     * Updates and handles the user's current coordinates as wanted, or returns to the
     * main activity in case of errors
     * @param context The context of the activity calling using the location
     * @param onSuccess The callback to execute once the location is available
     */
    fun requireAndHandleCoordinates(context: Context, onSuccess: (location: Location) -> Unit) {
        val futureLocation = GeneralLocationManager.get().getCurrentLocation(context)
        futureLocation.handle { location, exception ->
            if (exception != null) {
                // In case the permission to access the location is missing
                val intent = Intent(context, MainPageActivity::class.java)
                context.startActivity(intent)
            } else {
                userLocation = location
                onSuccess(location)
            }
        }
    }


    /**
     * Updates handles the user's current coordinates as wanted, or calls a failure callback in case
     * of errors
     * @param context The context of the activity calling using the location
     * @param onSuccess The callback to execute once the location is available
     * @param onFailure The callback to execute if the user didn't activate their location services
     *  for the app
     */
    fun requireAndHandleCoordinates(
        context: Context,
        onSuccess: (location: Location) -> Unit,
        onFailure: () -> Unit
    ) {
        val futureLocation = GeneralLocationManager.get().getCurrentLocation(context)
        futureLocation.handle { location, exception ->
            if (exception != null) {
                // In case the permission to access the location is missing
                onFailure()
            } else {
                userLocation = location
                onSuccess(location)
            }
        }
    }
}
