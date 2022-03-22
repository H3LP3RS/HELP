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
        Triple("democratic republic of the congo", 4.0383, 21.7587),
        Triple("switzerland", 46.5197, 6.6323),
        Triple("são tomé and príncipe", 0.1864, 6.6131)
    )

// Case examples of general queries to getLocalEmergencyNumber
private val TEST_COORDINATES_EMERGENCY_NUMBERS =
    listOf(
        Triple("22514242", 15.4542, 18.7322), // Chad
        Triple("112", 51.5072, 0.1276), // United Kingdom
        Triple("144", 46.5197, 6.6323) // Switzerland
    )

private const val NON_EXISTENT_COUNTRY = "falseCountry"

// Coordinates of point Nemo, a point in the middle of the South Pacific Ocean (it should thus
// have no associated country)
private const val POINT_NEMO_LONGITUDE = -48.876667
private const val POINT_NEMO_LATITUDE = -123.393333


@RunWith(AndroidJUnit4::class)
class LocalEmergencyCallTest : TestCase() {


    private val targetContext: Context = ApplicationProvider.getApplicationContext()
    private var localEmergencyCall = LocalEmergencyCall(0.0, 0.0, targetContext)

    @get:Rule
    val testRule = ActivityScenarioRule(
        HelpParametersActivity::class.java
    )

    @Test
    fun getLocalEmergencyNumberWorksOnCaseExamples() {
        for ((number, longitude, latitude) in TEST_COORDINATES_EMERGENCY_NUMBERS) {
            localEmergencyCall.longitude = longitude
            localEmergencyCall.latitude = latitude
            val actualNumber = localEmergencyCall.getLocalEmergencyNumber()
            assertEquals(number, actualNumber)
        }
    }


    @Test
    fun getLocalEmergencyNumberReturnsDefaultOnFalseCoordinates() {
        localEmergencyCall.longitude = POINT_NEMO_LONGITUDE
        localEmergencyCall.latitude = POINT_NEMO_LATITUDE
        assertEquals(
            LocalEmergencyCall.DEFAULT_EMERGENCY_NUMBER,
            localEmergencyCall.getLocalEmergencyNumber()
        )
    }

    @Test
    fun getUserCountryWorksOnCaseExamples() {
        for ((country, longitude, latitude) in TEST_COUNTRIES_COORDINATES) {
            localEmergencyCall.longitude = longitude
            localEmergencyCall.latitude = latitude
            val actualCountry = localEmergencyCall.getUserCountry()?.lowercase()
            assertEquals(country, actualCountry)
        }
    }

    @Test
    fun getUserCountryWorksOnFalseCoordinates() {
        localEmergencyCall.longitude = POINT_NEMO_LONGITUDE
        localEmergencyCall.latitude = POINT_NEMO_LATITUDE
        assertNull(localEmergencyCall.getUserCountry())
    }

    @Test
    fun getPhoneNumberFromActualCountryWorksOnCaseExamples() {
        for ((country, number) in TEST_COUNTRIES_EMERGENCY_NUMBERS) {
            val actualNumber = localEmergencyCall.getPhoneNumber(country)
            assertEquals(number, actualNumber)
        }
    }

    @Test
    fun getPhoneNumberFromFalseCountryFails() {
        assertEquals(
            localEmergencyCall.getPhoneNumber(NON_EXISTENT_COUNTRY),
            LocalEmergencyCall.DEFAULT_EMERGENCY_NUMBER
        )
    }
}