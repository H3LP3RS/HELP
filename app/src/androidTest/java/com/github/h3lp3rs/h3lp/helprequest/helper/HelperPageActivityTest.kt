package com.github.h3lp3rs.h3lp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.h3lp3rs.h3lp.model.database.Databases
import com.github.h3lp3rs.h3lp.model.database.Databases.*
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.model.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.model.messaging.Conversation.Companion.publicKeyPath
import com.github.h3lp3rs.h3lp.model.messaging.Messenger.HELPER
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.model.storage.Storages
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.disableOnlineSync
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.helprequest.helpee.EXTRA_EMERGENCY_KEY
import com.github.h3lp3rs.h3lp.view.helprequest.helper.EXTRA_DESTINATION_LAT
import com.github.h3lp3rs.h3lp.view.helprequest.helper.EXTRA_DESTINATION_LONG
import com.github.h3lp3rs.h3lp.view.helprequest.helper.EXTRA_HELP_REQUIRED_PARAMETERS
import com.github.h3lp3rs.h3lp.view.helprequest.helper.HelperPageActivity
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import junit.framework.TestCase
import junit.framework.TestCase.assertTrue
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.collections.ArrayList

@RunWith(AndroidJUnit4::class)
class HelpPageActivityTest : H3lpAppTest<HelperPageActivity>() {

    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val helpId = 1

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        mockLocationToCoordinates(SWISS_LAT, SWISS_LONG)

        userUid = USER_TEST_ID

        for(db in Databases.values()) {
            setDatabase(db, MockDatabase())
        }

        resetStorage()
    }

    override fun launch(): ActivityScenario<HelperPageActivity> {
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
            val updatedEmergency = databaseOf(EMERGENCIES, getApplicationContext()).getObject(
                helpId.toString(),
                EmergencyInformation::class.java
            )
            val helpers = ArrayList(updatedEmergency.get().helpers)
            // Check the helper has been added to the emergency object
            assertTrue(helpers[0].uid == USER_TEST_ID)
        }
    }

    @Test
    fun acceptingAnEmergencySendsPublicKeyOnDb() {
        setupEmergencyAndDo {
            // Accept
            onView(withId(R.id.button_accept)).perform(click())

            val conversationIds = databaseOf(CONVERSATION_IDS, getApplicationContext()).getObjectsList(
                TEST_EMERGENCY_ID,
                Int::class.java
            ).get()

            // retrieve the public key sent on the database
            databaseOf(MESSAGES, getApplicationContext())
                .getString(publicKeyPath(conversationIds[conversationIds.size - 1].toString(), HELPER.name))
                .thenAccept(TestCase::assertNotNull).join()

        }
    }

    @Test
    fun refusingAnEmergencyGoesBackToMainPage() {
        setupEmergencyAndDo {
            init()
            // Reject
            onView(withId(R.id.button_reject)).perform(click())
            intended(allOf(hasComponent(MainPageActivity::class.java.name)))
            release()
        }
    }

    @Test
    fun showsPopUpWhenNotSignedIn(){
        // Not signed in
        userUid = null
        Storages.disableOnlineSync(getApplicationContext())

        launchAndDo {

            // We can close the popup => It's displayed :)
            onView(withId(R.id.close_popup_button)).inRoot(RootMatchers.isFocusable()).perform(click())

            intended(
                allOf(
                    hasComponent(MainPageActivity::class.java.name)
                )
            )
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

        setDatabase(PREFERENCES, MockDatabase())
        val emergencyDb = MockDatabase()

        val skills = EPIPEN_SKILL
        val emergency = EPIPEN_EMERGENCY_INFO

        emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, emergency)
        setDatabase(EMERGENCIES, emergencyDb)

        // Setup skills storage accordingly
        storageOf(Storages.SKILLS, getApplicationContext()).setObject(
            getApplicationContext<Context>().getString(R.string.my_skills_key),
            HelperSkills::class.java, skills)
        launch<HelperPageActivity>(intent).use {
            action()
        }
    }
}