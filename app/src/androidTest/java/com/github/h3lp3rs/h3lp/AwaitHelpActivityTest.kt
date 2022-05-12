package com.github.h3lp3rs.h3lp

import android.Manifest
import android.content.Intent
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
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.collections.ArrayList

class AwaitHelpActivityTest : H3lpAppTest() {

    @get:Rule
    val testRule = ActivityScenarioRule(
        AwaitHelpActivity::class.java
    )

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        mockEmptyLocation()
        init()

        globalContext = getApplicationContext()
        userUid = USER_TEST_ID

        loadValidMedicalDataToStorage()
    }

    @After
    fun release() {
        Intents.release()
    }

    @Test
    fun callEmergenciesButtonWorksAndSendIntent() {
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
    }

    @Test
    fun clickPhoneButtonAndContactButtonDialsEmergencyContactNumber(){
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

        val emergency = EPIPEN_EMERGENCY_INFO

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