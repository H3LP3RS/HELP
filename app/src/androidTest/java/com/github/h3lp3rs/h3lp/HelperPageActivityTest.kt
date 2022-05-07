package com.github.h3lp3rs.h3lp

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.EMERGENCIES
import com.github.h3lp3rs.h3lp.database.Databases.PREFERENCES
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import junit.framework.TestCase.assertTrue
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import java.util.*
import org.mockito.Mockito.`when` as When

const val EPIPEN = "Epipen"

// Current coordinates to mock a user
const val CURRENT_LAT = 46.514
const val CURRENT_LONG = 6.604

// Destination coordinates
const val DESTINATION_LAT = 46.519
const val DESTINATION_LONG = 6.667

const val TEST_TIMEOUT = 5000

// Walking time from the user to the destination according to the Google directions API
const val TIME_TO_DESTINATION = "1 hour 19 mins"

@RunWith(AndroidJUnit4::class)
class HelperPageActivityTest {
    private val locationManagerMock: LocationManagerInterface =
        mock(LocationManagerInterface::class.java)
    private val locationMock: Location = mock(Location::class.java)
    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val helpId = 1

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        // Mocking the location manager
        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            locationMock
        )
        When(locationMock.latitude).thenReturn(CURRENT_LAT)
        When(locationMock.longitude).thenReturn(CURRENT_LONG)
        GeneralLocationManager.set(locationManagerMock)

        // Setup context
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID

        // Setup databases
        resetStorage()
        PREFERENCES.db = MockDatabase()
        val emergencyDb = MockDatabase()
        val skills = HelperSkills(
            true, true, true, true,
            true, true
        )
        val emergency = EmergencyInformation(
            helpId.toString(), 2.0, 2.0, skills,
            ArrayList(listOf("Epipen")), Date(), null, ArrayList()
        )

        emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, emergency)
        EMERGENCIES.db = emergencyDb
        // Setup skills storage accordingly
        storageOf(Storages.SKILLS).setObject(
            globalContext.getString(R.string.my_skills_key),
            HelperSkills::class.java, skills
        )
    }

    private fun launch(): ActivityScenario<HelperPageActivity> {
        // Forge the right intent
        val bundle = Bundle()
        bundle.putString(EXTRA_EMERGENCY_KEY, helpId.toString())
        bundle.putStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS, arrayListOf(EPIPEN))
        bundle.putDouble(EXTRA_DESTINATION_LAT, 1.0)
        bundle.putDouble(EXTRA_DESTINATION_LONG, 1.0)
        val intent = Intent(
            getApplicationContext(),
            HelperPageActivity::class.java
        ).apply {
            putExtras(bundle)
        }

        return launch(intent)
    }

    private fun launchAndDo(action: () -> Unit) {
        launch().use {
            init()
            action()
            release()
        }
    }

    @Test
    fun mapIsDisplayed() {
        launchAndDo {
            onView(withId(R.id.mapHelpPage))
                .check(matches(isDisplayed()))
        }
    }

    // Works on local but not on Cirrus
//    @Test
//    fun waitingTimeIsCorrectlyDisplayed() {
//        val bundle = Bundle()
//        bundle.putDouble(EXTRA_DESTINATION_LAT, DESTINATION_LAT)
//        bundle.putDouble(EXTRA_DESTINATION_LONG, DESTINATION_LONG)
//        val intent = Intent(
//            ApplicationProvider.getApplicationContext(),
//            HelpPageActivity::class.java
//        ).apply {
//            putExtras(bundle)
//        }
//
//        ActivityScenario.launch<HelpPageActivity>(intent).use {
//            // Espresso can't know that getting the time to person in need requires an API request
//            // so we are obliged to make the test synchronous by waiting until the time to person in
//            // need text appears
//            uiDevice.wait(
//                Until.findObject(By.res(BuildConfig.APPLICATION_ID + ":id/" + R.id.timeToPersonInNeed)),
//                TEST_TIMEOUT.toLong()
//            )
//            onView(withId(R.id.timeToPersonInNeed))
//                .check(matches(isDisplayed()))
//                .check(matches(withText(containsString(TIME_TO_DESTINATION))))
//        }
//    }

    @Test
    fun helpRequiredInformationIsCorrectlyDisplayed() {
        launchAndDo {
            onView(withId(R.id.helpRequired))
                .check(matches(isDisplayed()))
                .check(matches(withText(containsString(EPIPEN))))
        }
    }

    @Test
    fun acceptingAnEmergencyNotifiesTheHelpee() {
        setupEmergencyAndDo {
            // Accept
            onView(withId(R.id.button_accept)).perform(click())
            val updatedEmergency = databaseOf(EMERGENCIES).getObject(
                helpId.toString(),
                EmergencyInformation::class.java
            )
            val helpers = ArrayList(updatedEmergency.get().helpers)
            // Check the helper has been added to the emergency object
            assertTrue(helpers[0].uid == USER_TEST_ID)
        }
    }

    @Test
    fun refusingAnEmergencyGoesBackToMainPage() {
        launchAndDo {
            // Reject
            onView(withId(R.id.button_reject)).perform(click())
            intended(allOf(hasComponent(MainPageActivity::class.java.name)))
        }
    }

    /* Forge an emergency situation */
    private fun setupEmergencyAndDo(action: () -> Unit) {
        // Forge the right intent
        val bundle = Bundle()
        bundle.putString(EXTRA_EMERGENCY_KEY, helpId.toString())
        bundle.putStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS, arrayListOf(EPIPEN))
        bundle.putDouble(EXTRA_DESTINATION_LAT, 1.0)
        bundle.putDouble(EXTRA_DESTINATION_LONG, 1.0)
        val intent = Intent(
            getApplicationContext(),
            HelperPageActivity::class.java
        ).apply {
            putExtras(bundle)
        }
        // Setup the database accordingly
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        launch<HelperPageActivity>(intent).use {
            action()
        }
    }
}