package com.github.h3lp3rs.h3lp

import android.Manifest
import android.content.Intent.*
import android.net.Uri
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.h3lp3rs.h3lp.LocalEmergencyCaller.DEFAULT_EMERGENCY_NUMBER
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.Databases.EMERGENCIES
import com.github.h3lp3rs.h3lp.database.Databases.NEW_EMERGENCIES
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// Case example of a possible query when a user clicks on the call for emergency button

@RunWith(AndroidJUnit4::class)
class HelpParametersActivityTest : H3lpAppTest() {

    @get:Rule
    val testRule = ActivityScenarioRule(
        HelpeeSelectionActivity::class.java
    )

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setUp() {
        initIntentAndCheckResponse()

        globalContext = getApplicationContext()

        userUid = USER_TEST_ID
        NEW_EMERGENCIES.db = MockDatabase()
        val emergencyDb = MockDatabase()

        emergencyDb.setInt(globalContext.getString(R.string.EMERGENCY_UID_KEY), 0)
        setDatabase(EMERGENCIES, emergencyDb)
        resetStorage()

        storageOf(Storages.USER_COOKIE).setBoolean(
            globalContext.getString(R.string.KEY_USER_AGREE),
            true
        )
    }

    /*@Test
    fun clickSearchHelpWithMedsWorksAndSendsIntent() {
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
    fun clickPhoneButtonAndContactButtonDialsEmergencyContactNumber() {
        mockEmptyLocation()

        loadValidMedicalDataToStorage()

        // Clicking on the call for emergency button
        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        // click the contact button in the popup
        onView(withId(R.id.contact_call_button)).inRoot(RootMatchers.isFocusable()).perform(click())

        // The expected ambulance phone number given the location (specified by the coordinates)
        val number = "tel:$VALID_CONTACT_NUMBER"

        // Checking that this emergency number is dialed
        intended(
            allOf(
                hasAction(ACTION_DIAL),
                hasData(Uri.parse(number))
            )
        )
    }*/

    /*@Test
    fun clickPhoneButtonDialsCorrectEmergencyNumber() {
        mockLocationToCoordinates(SWISS_LONG, SWISS_LAT)

        loadValidMedicalDataToStorage()

        // Clicking on the call for emergency button
        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        // click the ambulance in the popup
        onView(withId(R.id.ambulance_call_button)).inRoot(RootMatchers.isFocusable())
            .perform(click())

        // The expected ambulance phone number given the location (specified by the coordinates)
        val number = "tel:${SWISS_EMERGENCY_NUMBER}"

        // Checking that this emergency number is dialed
        intended(
            allOf(
                hasAction(ACTION_DIAL) //,
                // hasData(Uri.parse(number)) Cirrus doesn't like this
            )
        )
    }
*/

    /*@Test
    fun clickPhoneButtonWithNoLocationDialsDefaultEmergencyNumber() {
        loadValidMedicalDataToStorage()

        mockFailingLocation()

        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        // click the ambulance in the popup
        onView(withId(R.id.ambulance_call_button)).perform(click())

        // In case of such an error, the default emergency number should be called
        val number = "tel:${DEFAULT_EMERGENCY_NUMBER}"

        // Checking that this emerclickPhoneButtonDialsCorrectEmergencyNumbergency number is dialed
        intended(
            allOf(
                hasAction(ACTION_DIAL) //TODO: Had to remove data Cirrus
            )
        )
    }*/


    /*@Test
    fun clickPhoneButtonWithSystemLocationManagerDialsEmergencyNumber() {
        loadValidMedicalDataToStorage()

        // Here we are simply testing that using the system location (the one actually used in the
        // app) also makes an emergency call
        GeneralLocationManager.setSystemManager()

        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        // click the ambulance in the popup
        onView(withId(R.id.ambulance_call_button)).perform(click())

        // Here, we can't check for a specific number (the emulator could be anywhere on Earth
        // but we can verify that a number was indeed called)
        intended(
            allOf(
                hasAction(ACTION_DIAL)
            )
        )
    }*/

    /*@Test
    fun clickPhoneButtonWithoutContactNumberDialsEmergenciesDirectly() {
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
                hasAction(ACTION_DIAL)
            )
        )
    }

    @Test
    fun clickSearchHelpWithNoMedsDoesNotChangeActivity() {
        val searchHelpButton = onView(withId(R.id.help_params_search_button))

        searchHelpButton.check(matches(isDisplayed()))
        searchHelpButton.perform(click())

        // No new intent:
        assertThat(getIntents().size, `is`(0))
    }*/

    /*@Test
    fun screenDisplaysCorrectLocation() {
        mockLocationToCoordinates(SWISS_LONG, SWISS_LAT)

        // Checking that the user's actual location is displayed before they call an ambulance
        val locationInformation = onView(withId(R.id.location_information))
        locationInformation
            .check(matches(withText(containsString(SWISS_LONG.toString()))))
        locationInformation
            .check(matches(withText(containsString(SWISS_LAT.toString()))))
    }*/

    @After
    fun cleanUp() {
        release()
    }
}