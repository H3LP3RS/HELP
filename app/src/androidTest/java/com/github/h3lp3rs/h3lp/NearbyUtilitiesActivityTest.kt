package com.github.h3lp3rs.h3lp

import android.Manifest
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.rule.GrantPermissionRule


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
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)


    @Test
    fun canLaunchMapWithPharmacyRequest() {
        val utility = R.string.nearby_phamacies
        val intent = Intent(ApplicationProvider.getApplicationContext(), NearbyUtilitiesActivity::class.java).apply {
            putExtra(EXTRA_NEARBY_UTILITIES, utility)
        }
        canLaunchMap(intent)
    }

    @Test
    fun canLaunchMapWithHospitalRequest() {
        val utility = R.string.nearby_hospitals
        val intent = Intent(ApplicationProvider.getApplicationContext(), NearbyUtilitiesActivity::class.java).apply {
            putExtra(EXTRA_NEARBY_UTILITIES, utility)
        }
        canLaunchMap(intent)
    }

    private fun canLaunchMap(intent: Intent) {
        launch<NearbyUtilitiesActivity>(intent).use {
            launch<NearbyUtilitiesActivity>(intent).use {
                onView(ViewMatchers.withId(R.id.map))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            }
        }
    }
}

