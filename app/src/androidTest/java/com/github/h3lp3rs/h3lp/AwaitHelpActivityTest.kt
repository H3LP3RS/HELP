package com.github.h3lp3rs.h3lp

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.app.Instrumentation.*
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
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
import com.github.h3lp3rs.h3lp.storage.Storages
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull

class AwaitHelpActivityTest {
    private val locationManagerMock: LocationManagerInterface =
        Mockito.mock(LocationManagerInterface::class.java)
    private val locationMock: Location = Mockito.mock(Location::class.java)

    private val selectedMeds = arrayListOf("Epipen")

    @get:Rule
    val testRule = ActivityScenarioRule(
        AwaitHelpActivity::class.java
    )

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        init()
        // Mocking the location manager
        Mockito.`when`(locationManagerMock.getCurrentLocation(anyOrNull())).thenReturn(
            locationMock
        )
        Mockito.`when`(locationMock.latitude).thenReturn(CURRENT_LAT)
        Mockito.`when`(locationMock.longitude).thenReturn(CURRENT_LONG)
        GeneralLocationManager.set(locationManagerMock)

        val intent = getIntent()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        intending(IntentMatchers.anyIntent()).respondWith(intentResult)
    }

    @After
    fun release() {
        Intents.release()
    }

    private fun clickingOnButtonWorksAndSendsIntent(
        ActivityName: Class<*>?,
        id: Matcher<View>,
        isInScrollView: Boolean
    ) {
        // close pop-up
        onView(withId(R.id.close_call_popup_button)).perform(click())

        if (isInScrollView) {
            onView(id).perform(scrollTo(), click())
        } else {
            onView(id).perform(click())
        }
        intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ActivityName!!.name)
            )
        )
    }

    @Test
    fun clickingOnHeartAttackButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            HeartAttackActivity::class.java,
            withId(R.id.heart_attack_tuto_button), true)
    }

// Tests work on local but not on Cirrus
//    @Test
//    fun clickingOnEpipenButtonWorksAndSendsIntent() {
//        clickingOnButtonWorksAndSendsIntent(
//            AllergyActivity::class.java,
//            withId(R.id.epipen_tuto_button), true)
//    }

    @Test
    fun clickingOnAedButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            AedActivity::class.java,
            withId(R.id.aed_tuto_button), true)
    }

    @Test
    fun clickingOnAsthmaButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            AsthmaActivity::class.java,
            withId(R.id.asthma_tuto_button), true)
    }

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

    private fun getIntent(): Intent {
        val bundle = Bundle()
        bundle.putStringArrayList(EXTRA_NEEDED_MEDICATION, selectedMeds)
        bundle.putBoolean(EXTRA_CALLED_EMERGENCIES, false)

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            AwaitHelpActivity::class.java
        ).apply {
            putExtras(bundle)
        }

        return intent
    }
}