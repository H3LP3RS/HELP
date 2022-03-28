package com.github.h3lp3rs.h3lp.locationmanager

import android.content.Context
import android.location.Location

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
}