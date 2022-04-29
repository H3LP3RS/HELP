package com.github.h3lp3rs.h3lp.locationmanager

import android.content.Context
import android.location.Location
import java.util.concurrent.CompletableFuture

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
     * case the user didn't have location permissions activated or there was an exception while
     * getting their location
     */
    fun getCurrentLocation(context: Context): CompletableFuture<Location>
}