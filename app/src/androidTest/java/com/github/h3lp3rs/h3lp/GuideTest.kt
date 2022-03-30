package com.github.h3lp3rs.h3lp

import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuideTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        MainPageActivity::class.java
    )

    private val targetContext: Context = ApplicationProvider.getApplicationContext()

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
        val intent = Intent(ApplicationProvider.getApplicationContext(), MainPageActivity::class.java)
        val scenario = ActivityScenario.launch<MainPageActivity>(intent)

        scenario.use {
            var i = 0
            // +1 for the search bar
            val nbButtons = mainPageButtons.size + scrollViewButtons.size + 1
            while (i++ <= nbButtons) {
                onView(withId(R.id.HelloText)).perform(click())
            }
            onView(ViewMatchers.withText(R.string.AppGuideFinished))
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        }
        // This cleans up the activity's associated resources and improves the stability of the tests
        scenario.close()

        // This works completely fine, but fails on Cirrus :(
        /*
        clearPreferences()
        var i = 0
        // +1 for the search bar
        val nbButtons = mainPageButtons.size + scrollViewButtons.size + 1
        while (i++ <= nbButtons) {
        onView(withId(R.id.HelloText)).perform(click())
        }
        onView(ViewMatchers.withText(R.string.AppGuideFinished))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

         */
    }

}