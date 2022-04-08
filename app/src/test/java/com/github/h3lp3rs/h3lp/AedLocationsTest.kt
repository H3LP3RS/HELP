package com.github.h3lp3rs.h3lp

import com.github.h3lp3rs.h3lp.util.AED_LOCATIONS_LAUSANNE
import org.junit.Assert.*
import org.junit.Test

class AedLocationsTest {

    @Test
    fun aedLocationHaveCorrectFormat(){
        AED_LOCATIONS_LAUSANNE.forEach{ e ->
            assertEquals(e["name"],"aed")
            assertNotNull(e["lat"])
            assertNotNull(e["lng"])
        }
    }
}