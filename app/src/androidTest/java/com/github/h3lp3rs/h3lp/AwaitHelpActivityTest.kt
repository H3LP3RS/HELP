package com.github.h3lp3rs.h3lp

import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.dataclasses.*
import com.github.h3lp3rs.h3lp.firstaid.AedActivity
import com.github.h3lp3rs.h3lp.firstaid.AllergyActivity
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when` as When
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import java.util.*
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import kotlin.collections.ArrayList

// Tests work on local but not on Cirrus

private const val VALID_CONTACT_NUMBER = "+41216933000"

class AwaitHelpActivityTest {
    private val locationManagerMock: LocationManagerInterface =
        mock(LocationManagerInterface::class.java)
    private val locationMock: Location = mock(Location::class.java)

    @get:Rule
    val testRule = ActivityScenarioRule(
        AwaitHelpActivity::class.java
    )

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        GeneralLocationManager.set(locationManagerMock)
        init()

        When(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            completedFuture(locationMock)
        )

        globalContext = getApplicationContext()
        userUid = USER_TEST_ID

        loadMedicalDataToLocalStorage()
    }

    @After
    fun release() {
        Intents.release()
    }

    @Test
    fun callEmergenciesButtonWorksAndSendIntent() {
        // close warning pop-up
        onView(withId(R.id.close_call_popup_button)).inRoot(RootMatchers.isFocusable()).perform(click())

        val phoneButton = onView(withId(R.id.await_help_call_button))

        //phoneButton.check(matches(isDisplayed()))
        phoneButton.inRoot(RootMatchers.isFocusable()).perform(click())

        // click the ambulance in the popup
        onView(withId(R.id.ambulance_call_button)).inRoot(RootMatchers.isFocusable()).perform(click())

        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_DIAL)
            )
        )
    }

    @Test
    fun clickPhoneButtonAndContactButtonDialsEmergencyContactNumber(){
        // close warning pop-up
        onView(withId(R.id.close_call_popup_button)).inRoot(RootMatchers.isFocusable()).perform(click())

        // Clicking on the call for emergency button
        val phoneButton = onView(withId(R.id.await_help_call_button))

        //phoneButton.inRoot(RootMatchers.isFocusable()).check(matches(isDisplayed()))
        phoneButton.inRoot(RootMatchers.isFocusable()).perform(click())

        // click the contact button in the popup
        onView(withId(R.id.contact_call_button)).inRoot(RootMatchers.isFocusable()).perform(click())

        // The expected ambulance phone number given the location (specified by the coordinates)
        val number = "tel:$VALID_CONTACT_NUMBER"

        // Checking that this emergency number is dialed
        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_DIAL),
                IntentMatchers.hasData(Uri.parse(number))
            )
        )
    }

    @Test
    fun callEmergenciesFromPopUpWorksAndSendsIntent() {
        val phoneButton = onView(withId(R.id.open_call_popup_button))

        phoneButton.inRoot(RootMatchers.isFocusable()).check(matches(isDisplayed()))
        phoneButton.inRoot(RootMatchers.isFocusable()).perform(click())

        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_DIAL)
            )
        )
    }

    /**
     * Auxiliary function to put a medical emergency contact in the local database
     */
    private fun loadMedicalDataToLocalStorage() {
        val medicalInformation = MedicalInformation(MedicalInformation.MAX_HEIGHT-1,
            MedicalInformation.MAX_WEIGHT-1, Gender.Male,
            Calendar.getInstance().get(Calendar.YEAR),
            "", "","",
            BloodType.ABn, "", VALID_CONTACT_NUMBER)


        Storages.storageOf(Storages.MEDICAL_INFO)
            .setObject(globalContext.getString(R.string.medical_info_key),
                MedicalInformation::class.java, medicalInformation)
    }

    private fun clickingOnButtonWorksAndSendsIntent(
        ActivityName: Class<*>?,
        id: Matcher<View>,
        isInScrollView: Boolean
    ) {
        // close pop-up
        onView(withId(R.id.close_call_popup_button)).inRoot(RootMatchers.isFocusable()).perform(click())
        if (isInScrollView) {
            onView(id).inRoot(RootMatchers.isFocusable()).perform(/*scrollTo(), */click())
        } else {
            onView(id).inRoot(RootMatchers.isFocusable()).perform(click())
        }
        intended(
            allOf(
                hasComponent(ActivityName!!.name)
            )
        )
    }

//    @Test
//    fun clickingOnHeartAttackButtonWorksAndSendsIntent() {
//        clickingOnButtonWorksAndSendsIntent(
//            HeartAttackActivity::class.java,
//            withId(R.id.heart_attack_tuto_button), true)
//    }

    @Test
    fun clickingOnEpipenButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            AllergyActivity::class.java,
            withId(R.id.epipen_tuto_button), true)
    }

    @Test
    fun clickingOnAedButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            AedActivity::class.java,
            withId(R.id.aed_tuto_button), true)
    }

//    @Test
//    fun clickingOnAsthmaButtonWorksAndSendsIntent() {
//        clickingOnButtonWorksAndSendsIntent(
//            AsthmaActivity::class.java,
//            withId(R.id.asthma_tuto_button), true)
//    }

    @Test
    fun cancelButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            MainPageActivity::class.java,
            withId(R.id.cancel_search_button), false)
    }

    @Test
    fun getsNotifiedWhenHelpIsComing() {
        // Forge the right intent
        val helpId = 1
        val bundle = Bundle()
        bundle.putInt(EXTRA_EMERGENCY_KEY, helpId)
        bundle.putBoolean(EXTRA_CALLED_EMERGENCIES, true)
        bundle.putStringArrayList(EXTRA_NEEDED_MEDICATION, arrayListOf(EPIPEN))
        val intent = Intent(
            getApplicationContext(),
            AwaitHelpActivity::class.java
        ).apply {
            putExtras(bundle)
        }
        // Setup the database accordingly
        val emergencyDb = MockDatabase()
        val skills = HelperSkills(true, true, true, true,
            true, true)
        val emergency = EmergencyInformation(helpId.toString(), 2.0, 2.0, skills,
            ArrayList(listOf("Epipen")), Date(), null, ArrayList())
        emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, emergency)
        EMERGENCIES.db = emergencyDb
        // Simulate arrival on await page after calling for help
        launch<AwaitHelpActivity>(intent).use {
            // Nobody coming
            onView(withId(R.id.incomingHelpersNumber)).check(matches(withText("")))
            // One person is coming
            val helper1 = Helper(USER_TEST_ID + 1, 2.0, 2.0)
            val withHelpers = emergency.copy(helpers = ArrayList(listOf(helper1)))
            emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, withHelpers)
            onView(withId(R.id.incomingHelpersNumber)).check(matches(withText(globalContext.getString(
                R.string.one_person_help))))
            // The same person is coming again, should NOT add a helper to the list
            emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, withHelpers)
            onView(withId(R.id.incomingHelpersNumber)).check(matches(withText(globalContext.getString(
                R.string.one_person_help))))
            // A second person is coming
            val helper2 = Helper(USER_TEST_ID + 2, 2.1, 2.1)
            val withMoreHelpers = emergency.copy(helpers = ArrayList(listOf(helper1, helper2)))
            emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, withMoreHelpers)
            onView(withId(R.id.incomingHelpersNumber)).check(matches(withText(String.format(
                globalContext.getString(R.string.many_people_help), 2))))
        }
    }
}