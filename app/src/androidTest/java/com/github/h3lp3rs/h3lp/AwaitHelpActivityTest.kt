package com.github.h3lp3rs.h3lp

import android.Manifest
import android.app.Activity
import android.app.Instrumentation.*
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.github.h3lp3rs.h3lp.firstaid.AedActivity
import com.github.h3lp3rs.h3lp.firstaid.AllergyActivity
import com.github.h3lp3rs.h3lp.firstaid.AsthmaActivity
import com.github.h3lp3rs.h3lp.firstaid.HeartAttackActivity
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.locationmanager.LocationManagerInterface
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull

class AwaitHelpActivityTest {
    private val locationManagerMock: LocationManagerInterface =
        mock(LocationManagerInterface::class.java)
    private val locationMock: Location = mock(Location::class.java)

    private val selectedMeds = arrayListOf("Epipen")

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

        SignInActivity.globalContext = getApplicationContext()
        SignInActivity.userUid = USER_TEST_ID
        resetStorage()
        storageOf(Storages.USER_COOKIE)
            .setBoolean(SignInActivity.globalContext.getString(R.string.KEY_USER_AGREE), true)
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
        onView(withId(R.id.close_call_popup_button)).inRoot(RootMatchers.isFocusable()).perform(click())

        if (isInScrollView) {
            onView(id).inRoot(RootMatchers.isFocusable()).perform(scrollTo(), click())
        } else {
            onView(id).inRoot(RootMatchers.isFocusable()).perform(click())
        }
        intended(
            allOf(
                hasComponent(ActivityName!!.name)
            )
        )
    }

    @Test
    fun callEmergenciesButtonWorksAndSendIntent() {
        // close pop-up
        onView(withId(R.id.close_call_popup_button)).inRoot(RootMatchers.isFocusable()).perform(click())

        val phoneButton = onView(withId(R.id.await_help_call_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_DIAL)
            )
        )
    }

    @Test
    fun callEmergenciesFromPopUpWorksAndSendsIntent() {
        val phoneButton = onView(withId(R.id.open_call_popup_button))

        phoneButton.check(matches(isDisplayed()))
        phoneButton.perform(click())

        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_DIAL)
            )
        )
    }

    @Test
    fun clickingOnHeartAttackButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            HeartAttackActivity::class.java,
            withId(R.id.heart_attack_tuto_button), true)
    }

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



    private fun getIntent(): Intent {
        val bundle = Bundle()
        bundle.putStringArrayList(EXTRA_NEEDED_MEDICATION, selectedMeds)
        bundle.putBoolean(EXTRA_CALLED_EMERGENCIES, false)

        val intent = Intent(
            getApplicationContext(),
            AwaitHelpActivity::class.java
        ).apply {
            putExtras(bundle)
        }

        return intent
    }
}