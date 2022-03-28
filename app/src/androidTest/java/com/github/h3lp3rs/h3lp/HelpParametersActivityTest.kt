package com.github.h3lp3rs.h3lp

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.h3lp3rs.h3lp.LocalEmergencyCaller.DEFAULT_EMERGENCY_NUMBER
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface
import com.github.h3lp3rs.h3lp.preferences.Preferences
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.Files.PRESENTATION
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.USER_AGREE
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.clearAllPreferences
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When


// Case example of a possible query when a user clicks on the call for emergency button
private val CORRECT_EMERGENCY_CALL = Triple(6.632, 46.519, "144")

@RunWith(AndroidJUnit4::class)
class HelpParametersActivityTest {
    private val locationManagerMock: LocationManagerInterface =
        mock(LocationManagerInterface::class.java)
    private val locationMock: Location = mock(Location::class.java)

    @get:Rule
    val testRule = ActivityScenarioRule(
        HelpParametersActivity::class.java
    )


    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setUp() {
        init()
        clearAllPreferences(ApplicationProvider.getApplicationContext())
        Preferences(PRESENTATION, ApplicationProvider.getApplicationContext()).setBool(
            USER_AGREE, true
        )

    }

    @Test
    fun clickSearchHelpWithMedsWorksAndSendsIntent() {
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)

        // select one med
        val medButton0 = onView(withId(R.id.selectMedsButton0))

        medButton0.check(matches(isNotChecked()))

        // select the medication
        medButton0.perform(click())

        // click the search help button
        val searchHelpButton = onView(withId(R.id.help_params_search_button))

        searchHelpButton.check(matches(isDisplayed()))
        searchHelpButton.perform(click())

        intended(
            allOf(
                hasComponent(AwaitHelpActivity::class.java.name),
            )
        )
    }

    @Test
    fun clickPhoneButtonDialsCorrectEmergencyNumber() {
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)

        // Mocking the user's location to a predefined set of coordinates
        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            locationMock
        )
        When(locationMock.longitude).thenReturn(CORRECT_EMERGENCY_CALL.first)
        When(locationMock.latitude).thenReturn(CORRECT_EMERGENCY_CALL.second)
        GeneralLocationManager.set(locationManagerMock)

        // Clicking on the call for emergency button
        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        // The expected ambulance phone number given the location (specified by the coordinates)
        val number = "tel:${CORRECT_EMERGENCY_CALL.third}"

        // Checking that this emergency number is dialed
        intended(
            allOf(
                hasAction(Intent.ACTION_DIAL),
                hasData(Uri.parse(number))
            )
        )
    }


    @Test
    fun clickPhoneButtonWithNoLocationDialsDefaultEmergencyNumber() {
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)


        // Mocking the location manager as if an error occurred (in which case, the returned location
        // would be null
        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(null)
        GeneralLocationManager.set(locationManagerMock)

        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        // In case of such an error, the default emergency number should be called
        val number = "tel:${DEFAULT_EMERGENCY_NUMBER}"

        // Checking that this emergency number is dialed
        intended(
            allOf(
                hasAction(Intent.ACTION_DIAL),
                hasData(Uri.parse(number))
            )
        )
    }


    @Test
    fun clickPhoneButtonWithSystemLocationManagerDialsEmergencyNumber() {
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)

        // Here we are simply testing that using the system location (the one actually used in the
        // app) also makes an emergency call
        GeneralLocationManager.setSystemManager()

        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        // Here, we can't check for a specific number (the emulator could be anywhere on Earth
        // but we can verify that a number was indeed called)
        intended(
            allOf(
                hasAction(Intent.ACTION_DIAL)
            )
        )
    }

    @Test
    fun clickSearchHelpWithNoMedsDoesNotChangeActivity() {
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)

        val searchHelpButton = onView(withId(R.id.help_params_search_button))

        searchHelpButton.check(matches(isDisplayed()))
        searchHelpButton.perform(click())

        // No new intent:
        assertThat(getIntents().size, `is`(0))
    }

    @Test
    fun screenDisplaysCorrectLocation() {
        // Mocking the user's location to a predefined set of coordinates
        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            locationMock
        )
        When(locationMock.longitude).thenReturn(CORRECT_EMERGENCY_CALL.first)
        When(locationMock.latitude).thenReturn(CORRECT_EMERGENCY_CALL.second)
        GeneralLocationManager.set(locationManagerMock)

        // Checking that the user's actual location is displayed before they call an ambulance
        val locationInformation = onView(withId(R.id.location_information))
        locationInformation
            .check(matches(withText(containsString(CORRECT_EMERGENCY_CALL.first.toString()))))
        locationInformation
            .check(matches(withText(containsString(CORRECT_EMERGENCY_CALL.second.toString()))))
    }

    @After
    fun cleanUp() {
        release()
    }
}