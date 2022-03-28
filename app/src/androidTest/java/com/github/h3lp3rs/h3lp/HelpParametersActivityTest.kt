package com.github.h3lp3rs.h3lp

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.*

import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.Files.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.h3lp3rs.h3lp.LocalEmergencyCaller.DEFAULT_EMERGENCY_NUMBER
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface
import com.github.h3lp3rs.h3lp.preferences.Preferences
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.USER_AGREE
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.clearAllPreferences
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When

private val CORRECT_EMERGENCY_CALL = Triple( 6.6323, 46.5197,"144")

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
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setUp() {
        clearAllPreferences(ApplicationProvider.getApplicationContext())
        Preferences(PRESENTATION, ApplicationProvider.getApplicationContext()).setBool(
            USER_AGREE, true)
    }

    @Test
    fun clickSearchHelpWithMedsWorksAndSendsIntent() {
        init()
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

        release()
    }

    @Test
    fun clickPhoneButtonDialsCorrectEmergencyNumber() {
        init()
        val intent = Intent()


        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        intending(anyIntent()).respondWith(intentResult)


        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            locationMock)
        When(locationMock.longitude).thenReturn(CORRECT_EMERGENCY_CALL.first)
        When(locationMock.latitude).thenReturn(CORRECT_EMERGENCY_CALL.second)
        GeneralLocationManager.set(locationManagerMock)

        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        val number = "tel:${CORRECT_EMERGENCY_CALL.third}"

        // emergency number is dialed:
        intended(
            allOf(
                hasAction(Intent.ACTION_DIAL)
                ,
                hasData(Uri.parse(number))
            )
        )

        release()
    }


    @Test
    fun clickPhoneButtonWithNoLocationDialsDefaultEmergencyNumber() {
        init()
        val intent = Intent()


        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        intending(anyIntent()).respondWith(intentResult)


        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(null)
        GeneralLocationManager.set(locationManagerMock)

        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        val number = "tel:${DEFAULT_EMERGENCY_NUMBER}"

        // emergency number is dialed:
        intended(
            allOf(
                hasAction(Intent.ACTION_DIAL),
                hasData(Uri.parse(number))
            )
        )

        release()
    }


    @Test
    fun clickPhoneButtonWithSystemLocationManagerDialsEmergencyNumber() {
        init()
        val intent = Intent()


        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        intending(anyIntent()).respondWith(intentResult)

        GeneralLocationManager.setSystemManager()

        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        // emergency number is dialed:
        intended(
            allOf(
                hasAction(Intent.ACTION_DIAL)
            )
        )

        release()
    }

    @Test
    fun clickSearchHelpWithNoMedsDoesNotChangeActivity() {
        init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        intending(anyIntent()).respondWith(intentResult)

        val searchHelpButton = onView(withId(R.id.help_params_search_button))

        searchHelpButton.check(matches(isDisplayed()))
        searchHelpButton.perform(click())

        // no new intent:
        assertThat(getIntents().size, `is`(0))

        release()
    }
}