package com.github.h3lp3rs.h3lp

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuideTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        MainPageActivity::class.java
    )

    private val targetContext : Context = ApplicationProvider.getApplicationContext()

    private fun clearPreferences() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(targetContext)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }

    @Test
    fun checkThatGuideIsInitiallyNotLaunched() {
        clearPreferences()
        val prefManager = PreferenceManager.getDefaultSharedPreferences(targetContext)
        assertFalse(prefManager.getBoolean("didShowGuide", false))
    }

    @Test
    fun checkThatGuideIsLaunched() {
        val prefManager = PreferenceManager.getDefaultSharedPreferences(targetContext)
        assertTrue(prefManager.getBoolean("didShowGuide", false))
    }


    @Test
    fun finishingAppDemoDisplaysMessage() {
        // Both ways work, but mysteriously fail on Cirrus.
        /*
        clearPreferences()
        var i = 0
        // +1 for the search bar
        val nbButtons = numberOfButtons + 1
        while (i++ < nbButtons) {
            // This works completely fine, but fails on Cirrus :(
            // onView(withId(R.id.HelloText)).perform(click())
            // Also fails on Cirrus
            // val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            // uiDevice.findObject(UiSelector().textContains("Help")).click()
        }
        onView(ViewMatchers.withText(R.string.AppGuideFinished)).check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        )
         */

    }

}