package com.github.h3lp3rs.h3lp

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.app.Instrumentation.*
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.github.h3lp3rs.h3lp.firstaid.AedActivity
import com.github.h3lp3rs.h3lp.firstaid.AllergyActivity
import com.github.h3lp3rs.h3lp.firstaid.AsthmaActivity
import com.github.h3lp3rs.h3lp.firstaid.HeartAttackActivity
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import java.util.*

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

        `when`(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            locationMock
        )

        globalContext = getApplicationContext()
        userUid = USER_TEST_ID

        loadMedicalDataToLocalStorage()
    }

    @After
    fun release() {
        Intents.release()
    }

//    @Test
//    fun callEmergenciesButtonWorksAndSendIntent() {
//        // close warning pop-up
//        onView(withId(R.id.close_call_popup_button)).inRoot(RootMatchers.isFocusable()).perform(click())
//
//        val phoneButton = onView(withId(R.id.await_help_call_button))
//
//        //phoneButton.check(matches(isDisplayed()))
//        phoneButton.inRoot(RootMatchers.isFocusable()).perform(click())
//
//        // click the ambulance in the popup
//        onView(withId(R.id.ambulance_call_button)).inRoot(RootMatchers.isFocusable()).perform(click())
//
//        intended(
//            allOf(
//                IntentMatchers.hasAction(Intent.ACTION_DIAL)
//            )
//        )
//    }
//
//    @Test
//    fun clickPhoneButtonAndContactButtonDialsEmergencyContactNumber(){
//        // close warning pop-up
//        onView(withId(R.id.close_call_popup_button)).inRoot(RootMatchers.isFocusable()).perform(click())
//
//        // Clicking on the call for emergency button
//        val phoneButton = onView(withId(R.id.await_help_call_button))
//
//        //phoneButton.inRoot(RootMatchers.isFocusable()).check(matches(isDisplayed()))
//        phoneButton.inRoot(RootMatchers.isFocusable()).perform(click())
//
//        // click the contact button in the popup
//        onView(withId(R.id.contact_call_button)).inRoot(RootMatchers.isFocusable()).perform(click())
//
//        // The expected ambulance phone number given the location (specified by the coordinates)
//        val number = "tel:$VALID_CONTACT_NUMBER"
//
//        // Checking that this emergency number is dialed
//        intended(
//            allOf(
//                IntentMatchers.hasAction(Intent.ACTION_DIAL),
//                IntentMatchers.hasData(Uri.parse(number))
//            )
//        )
//    }

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
            MedicalInformation.MAX_WEIGHT-1,Gender.Male,
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

// Tests work on local but not on Cirrus
//    @Test
//    fun callEmergenciesButtonWorksAndSendIntent() {
//        // close pop-up
//        onView(withId(R.id.close_call_popup_button)).perform(click())
//
//        val phoneButton = onView(withId(R.id.await_help_call_button))
//
//        phoneButton.check(ViewAssertions.matches(isDisplayed()))
//        phoneButton.perform(click())
//
//        intended(
//            Matchers.allOf(
//                IntentMatchers.hasAction(Intent.ACTION_DIAL)
//            )
//        )
//    }


// Tests work on local but not on Cirrus
//    @Test
//    fun callEmergenciesFromPopUpWorksAndSendsIntent() {
//        val phoneButton = onView(withId(R.id.open_call_popup_button))
//
//        phoneButton.check(ViewAssertions.matches(isDisplayed()))
//        phoneButton.perform(click())
//
//        intended(
//            Matchers.allOf(
//                IntentMatchers.hasAction(Intent.ACTION_DIAL)
//            )
//        )
//    }

}