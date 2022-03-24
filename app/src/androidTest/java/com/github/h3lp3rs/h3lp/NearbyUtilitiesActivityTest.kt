package com.github.h3lp3rs.h3lp

import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    @Test
    fun canLaunchMapWithPharmacyRequest() {
        val utility = R.string.nearby_phamacies
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
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
            ApplicationProvider.getApplicationContext(),
            NearbyUtilitiesActivity::class.java
        ).apply {
            putExtra(EXTRA_NEARBY_UTILITIES, utility)
        }
        canLaunchMap(intent)
    }

    @Test
    fun launchWithIntentSelectsRightButtons() {
        val utility = R.string.nearby_hospitals
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            NearbyUtilitiesActivity::class.java
        ).apply {
            putExtra(EXTRA_NEARBY_UTILITIES, utility)
        }

        launch<NearbyUtilitiesActivity>(intent).use {
            onView(ViewMatchers.withId(R.id.map))
                .check(matches(isDisplayed()))

            onView(ViewMatchers.withId(R.id.show_hospital_button_layout))
                .check(matches(isDisplayed()))

            onView(ViewMatchers.withId(R.id.show_hospital_button))
                .check(matches(isDisplayed()))

            // Select pharmacies
            onView(ViewMatchers.withId(R.id.show_pharmacy_button))
                .check(matches(isDisplayed()))
                .perform(click())

            // Select defibrillators
            onView(ViewMatchers.withId(R.id.show_defibrillators_button))
                .check(matches(isDisplayed()))
                .perform(click())

        }
    }

    private fun canLaunchMap(intent: Intent) {
        launch<NearbyUtilitiesActivity>(intent).use {
            launch<NearbyUtilitiesActivity>(intent).use {
                onView(ViewMatchers.withId(R.id.map))
                    .check(matches(isDisplayed()))
            }
        }
    }

}

