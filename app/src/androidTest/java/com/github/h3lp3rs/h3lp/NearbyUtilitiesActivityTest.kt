package com.github.h3lp3rs.h3lp

import android.Manifest
import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Tests for the map and the display of markers on nearby utilities.
 */
@RunWith(AndroidJUnit4::class)
class NearbyUtilitiesActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        NearbyUtilitiesActivity::class.java
    )

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        globalContext = getApplicationContext()
    }

    @Test
    fun canLaunchMapWithPharmacyRequest() {
        val utility = R.string.nearby_phamacies

        val intent = Intent(
            getApplicationContext(),
            NearbyUtilitiesActivity::class.java
        ).apply {
            putExtra(EXTRA_NEARBY_UTILITIES, utility)
        }

        canLaunchMap(intent)
    }

    @Test
    fun canLaunchMapWithHospitalRequest() {
        val utility = R.string.nearby_hospitals

        val intent = Intent(
            getApplicationContext(),
            NearbyUtilitiesActivity::class.java
        ).apply {
            putExtra(EXTRA_NEARBY_UTILITIES, utility)
        }
        canLaunchMap(intent)
    }

    @Test
    fun canSelectMapButtons() {
        onView(ViewMatchers.withId(R.id.show_hospital_button))
            .check(matches(isDisplayed()))
            .perform(click())

        // Select pharmacies
        onView(ViewMatchers.withId(R.id.show_pharmacy_button))
            .check(matches(isDisplayed()))
            .perform(click())

        // Select defibrillators
        onView(ViewMatchers.withId(R.id.show_defibrillators_button))
            .check(matches(isDisplayed()))
            .perform(click())
    }

    private fun canLaunchMap(intent: Intent) {
        launch<NearbyUtilitiesActivity>(intent).use {
            onView(ViewMatchers.withId(R.id.mapNearbyUtilities))
                .check(matches(isDisplayed()))
        }
    }

}

