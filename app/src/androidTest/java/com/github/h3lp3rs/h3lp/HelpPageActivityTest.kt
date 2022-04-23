package com.github.h3lp3rs.h3lp

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.doubleClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.h3lp3rs.h3lp.database.Databases.MESSAGES
import com.github.h3lp3rs.h3lp.database.Databases.CONVERSATION_IDS
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface
import com.github.h3lp3rs.h3lp.messaging.ChatActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import org.hamcrest.Matchers
import org.hamcrest.Matchers.containsString
import org.junit.Assert.assertTrue
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

const val TEST_TIMEOUT = 5000

// Walking time from the user to the destination according to the Google directions API
const val TIME_TO_DESTINATION = "1 hour 19 mins"

@RunWith(AndroidJUnit4::class)
class HelpPageActivityTest {
    private val locationManagerMock: LocationManagerInterface =
        mock(LocationManagerInterface::class.java)
    private val locationMock: Location = mock(Location::class.java)
    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val HELPEE_ID = "14"


    @get:Rule
    val testRule = ActivityScenarioRule(
        HelpPageActivity::class.java
    )


    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        // Sign in initializations
        SignInActivity.globalContext = ApplicationProvider.getApplicationContext()
        SignInActivity.userUid = USER_TEST_ID

        // Mocking the location manager
        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            locationMock
        )
        When(locationMock.latitude).thenReturn(CURRENT_LAT)
        When(locationMock.longitude).thenReturn(CURRENT_LONG)
        GeneralLocationManager.set(locationManagerMock)

        // Mocking the databases
        CONVERSATION_IDS.db = MockDatabase()
        MESSAGES.db = MockDatabase()

        // Launching the activity with different parameters
        val bundle = Bundle()
        bundle.putDouble(EXTRA_DESTINATION_LAT, DESTINATION_LAT)
        bundle.putDouble(EXTRA_DESTINATION_LONG, DESTINATION_LONG)
        bundle.putStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS, arrayListOf(EPIPEN))
        bundle.putString(EXTRA_HELPEE_ID, HELPEE_ID)
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            HelpPageActivity::class.java
        ).apply {
            putExtras(bundle)
        }
        ActivityScenario.launch<HelpPageActivity>(intent)
    }

    @Test
    fun mapIsDisplayed() {
        onView(withId(R.id.mapHelpPage)).check(matches(isDisplayed()))
    }

    // Works on local but not on Cirrus
//    @Test
//    fun waitingTimeIsCorrectlyDisplayed() {
        // Espresso can't know that getting the time to person in need requires an API request
        // so we are obliged to make the test synchronous by waiting until the time to person in
        // need text appears
//        uiDevice.wait(
//            Until.findObject(By.res(BuildConfig.APPLICATION_ID + ":id/" + R.id.timeToPersonInNeed)),
//            TEST_TIMEOUT.toLong()
//        )
//        onView(withId(R.id.timeToPersonInNeed))
//            .check(matches(isDisplayed()))
//            .check(matches(withText(containsString(TIME_TO_DESTINATION))))
//    }

    @Test
    fun helpRequiredInformationIsCorrectlyDisplayed() {
        onView(withId(R.id.helpRequired))
            .check(matches(isDisplayed()))
            .check(matches(withText(containsString(EPIPEN))))
    }

    @Test
    fun acceptHelpRequestSendsUniqueId() {
        var hasSentUniqueId = false

        // Adding a listener to the conversations database and checking if the helper sends the
        // helpee a conversation id
        val callBack: (List<String>) -> Unit = {if (it.isNotEmpty()) hasSentUniqueId = true}
        CONVERSATION_IDS.db?.addListListener(HELPEE_ID, String::class.java, callBack)

        onView(withId(R.id.button_accept))
            .check(matches(isDisplayed()))
            .perform(click())

        // Checks that accepting to help sends a conversation id to the person in need of help
        assertTrue(hasSentUniqueId)
    }
    @Test
    fun onAcceptingHelpRequestLaunchesChatActivity(){
        init()
        onView(withId(R.id.button_accept))
            .check(matches(isDisplayed()))
            .perform(doubleClick())
        intended(Matchers.allOf(hasComponent(ChatActivity::class.java.name)))
        release()
    }
    @Test
    fun rejectHelpMakesAppGoToMain() {
        init()
        onView(withId(R.id.button_reject))
            .check(matches(isDisplayed()))
            .perform(click())
        intended(
            Matchers.allOf(
                hasComponent(MainPageActivity::class.java.name)
            )
        )
        release()
    }
}