package com.github.h3lp3rs.h3lp.locationmanager

import com.github.h3lp3rs.h3lp.model.locationmanager.LocationManagerInterface.Companion.getDistanceFromLatLon
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface.Companion.getDistanceFromLatLon
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LocationManagerTest {

    @Test
    fun distanceCalculationUsingCoordinatesIsCorrect() {
        val coordinates1 = Pair(46.0, 30.0)
        val coordinates2 = Pair(20.0, 40.0)
        val result = getDistanceFromLatLon(coordinates1, coordinates2)
        assertEquals(3032000.0, result, 100.0)
    }

    @Test
    fun defaultLocationManagerWork(){
        GeneralLocationManager.setDefaultSystemManager()
        assertNotNull(GeneralLocationManager.get())
    }
}