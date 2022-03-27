package com.github.h3lp3rs.h3lp.locationmanager

import android.content.Context
import android.location.Location

interface LocationManagerInterface {
    fun getCurrentLocation(context: Context): Location?
}