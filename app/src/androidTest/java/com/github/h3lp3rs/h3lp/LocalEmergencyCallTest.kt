package com.github.h3lp3rs.h3lp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



// Case examples of queries to getPhoneNumber (the casing is random since it shouldn't affect
// the result)
private val TEST_COUNTRIES_EMERGENCY_NUMBERS =
    listOf(Pair("SwitzerlanD", "144"), Pair("burkIna faso", "112"), Pair("EcuaDor", "911"))


// Case examples of queries to getUserCountry
private val TEST_COUNTRIES_COORDINATES =
    listOf(
        Triple("democratic republic of the congo", 21.7587, 4.0383),
        Triple("switzerland", 6.6323, 46.5197),
        Triple("são tomé and príncipe", 6.6131, 0.1864)
    )

// Case examples of general queries to getLocalEmergencyNumber
private val TEST_COORDINATES_EMERGENCY_NUMBERS =
    listOf(
        Triple("22514242", 18.7322, 15.4542), // Chad
        Triple("112", 0.1276, 51.5072), // United Kingdom
        Triple("144", 6.6323, 46.5197) // Switzerland
    )

private const val NON_EXISTENT_COUNTRY = "falseCountry"

// Coordinates of point Nemo, a point in the middle of the South Pacific Ocean (it should thus
// have no associated country)
private const val POINT_NEMO_LONGITUDE = -123.393333
private const val POINT_NEMO_LATITUDE = -48.876667

@RunWith(AndroidJUnit4::class)
class LocalEmergencyCallTest : TestCase() {


    private val targetContext: Context = ApplicationProvider.getApplicationContext()

    @get:Rule
    val testRule = ActivityScenarioRule(
        HelpParametersActivity::class.java
    )

    @Test
    fun getLocalEmergencyNumberWorksOnCaseExamples() {
        for ((number, longitude, latitude) in TEST_COORDINATES_EMERGENCY_NUMBERS) {
            val actualNumber =
                LocalEmergencyCaller.getLocalEmergencyNumber(longitude, latitude, targetContext)
            assertEquals(number, actualNumber)
        }
    }


    @Test
    fun getLocalEmergencyNumberReturnsDefaultOnFalseCoordinates() {
        assertEquals(
            LocalEmergencyCaller.DEFAULT_EMERGENCY_NUMBER,
            LocalEmergencyCaller.getLocalEmergencyNumber(
                POINT_NEMO_LONGITUDE,
                POINT_NEMO_LATITUDE,
                targetContext
            )
        )
    }

    @Test
    fun getUserCountryWorksOnCaseExamples() {
        for ((country, longitude, latitude) in TEST_COUNTRIES_COORDINATES) {
            val actualCountry = LocalEmergencyCaller
                .getUserCountry(longitude, latitude, targetContext)
                ?.lowercase()
            assertEquals(country, actualCountry)
        }
    }

    @Test
    fun getUserCountryWorksOnFalseCoordinates() {
        assertNull(
            LocalEmergencyCaller.getUserCountry(
                POINT_NEMO_LONGITUDE,
                POINT_NEMO_LATITUDE,
                targetContext
            )
        )
    }

    @Test
    fun getPhoneNumberFromActualCountryWorksOnCaseExamples() {
        for ((country, number) in TEST_COUNTRIES_EMERGENCY_NUMBERS) {
            val actualNumber =
                LocalEmergencyCaller.getPhoneNumberFromCountry(targetContext, country)
            assertEquals(number, actualNumber)
        }
    }

    @Test
    fun getPhoneNumberFromFalseCountryFails() {
        assertEquals(
            LocalEmergencyCaller.getPhoneNumberFromCountry(targetContext, NON_EXISTENT_COUNTRY),
            LocalEmergencyCaller.DEFAULT_EMERGENCY_NUMBER
        )
    }
}