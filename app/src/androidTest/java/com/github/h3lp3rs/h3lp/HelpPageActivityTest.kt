package com.github.h3lp3rs.h3lp

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When

const val EPIPEN = "Epipen"
// Current coordinates to mock a user
const val CURRENT_LAT = 46.514
const val CURRENT_LONG = 6.604

// Destination coordinates
const val DESTINATION_LAT = 46.519
const val DESTINATION_LONG = 6.667

// Walking time from the user to the destination according to the Google directions API
const val TIME_TO_DESTINATION = "1 hour 19 mins"

@RunWith(AndroidJUnit4::class)
class HelpPageActivityTest {
    private val locationManagerMock: LocationManagerInterface =
        mock(LocationManagerInterface::class.java)
    private val locationMock: Location = mock(Location::class.java)
    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())


    @get:Rule
    val testRule = ActivityScenarioRule(
        HelpPageActivity::class.java
    )


    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun init() {
        // Mocking the location manager
        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            locationMock
        )
        When(locationMock.latitude).thenReturn(CURRENT_LAT)
        When(locationMock.longitude).thenReturn(CURRENT_LONG)
        GeneralLocationManager.set(locationManagerMock)

    }

    @Test
    fun mapIsDisplayed() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            HelpPageActivity::class.java
        )
        ActivityScenario.launch<HelpPageActivity>(intent).use {
            onView(withId(R.id.mapHelpPage))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun waitingTimeIsCorrectlyDisplayed() {
        val bundle = Bundle()
        bundle.putDouble(EXTRA_DESTINATION_LAT, DESTINATION_LAT)
        bundle.putDouble(EXTRA_DESTINATION_LONG, DESTINATION_LONG)
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            HelpPageActivity::class.java
        ).apply {
            putExtras(bundle)
        }

        ActivityScenario.launch<HelpPageActivity>(intent).use {
            // Espresso can't know that getting the time to person in need requires an API request
            // so we are obliged to make the test synchronous by waiting until the time to person in
            // need text appears
            uiDevice.wait(
                Until.findObject(By.res(BuildConfig.APPLICATION_ID + ":id/" + R.id.timeToPersonInNeed)),
                1000
            )
            onView(withId(R.id.timeToPersonInNeed))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString(TIME_TO_DESTINATION))))
        }
    }

    @Test
    fun helpRequiredInformationIsCorrectlyDisplayed() {
        val bundle = Bundle()
        bundle.putStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS, arrayListOf(EPIPEN))
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            HelpPageActivity::class.java
        ).apply {
            putExtras(bundle)
        }

        ActivityScenario.launch<HelpPageActivity>(intent).use {
            onView(withId(R.id.helpRequired))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString(EPIPEN))))
        }
    }
}