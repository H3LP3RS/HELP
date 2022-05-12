package com.github.h3lp3rs.h3lp

import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface.Companion.getDistanceFromLatLon
import org.junit.Test
import kotlin.test.assertEquals

class LocationManagerTest {

    @Test
    fun distanceCalculationUsingCoordinatesIsCorrect() {
        val coordinates1 = Pair(46.0, 30.0)
        val coordinates2 = Pair(20.0, 40.0)
        val result = getDistanceFromLatLon(coordinates1, coordinates2)
        assertEquals(3032000.0, result, 100.0)
    }
}