package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToolbarTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        MainPageActivity::class.java
    )

    @Before
    fun setup() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
    }

    @After
    fun release() {
        Intents.release()
    }

    private val drawerLayout : ViewInteraction? = onView(ViewMatchers.withId(R.id.drawer_layout))

    private fun closeDrawerLayout() {
        drawerLayout?.perform(DrawerActions.close())
    }

    private fun openDrawerLayout() {
        drawerLayout?.perform(DrawerActions.open())
    }

    @Test
    fun openingDrawerLayoutWorks() {
        openDrawerLayout()
        drawerLayout?.check(ViewAssertions.matches(DrawerMatchers.isOpen(Gravity.START)))
    }

    @Test
    fun closingDrawerLayoutWorks() {
        openDrawerLayout()
        closeDrawerLayout()
        drawerLayout?.check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT)))
    }

    /**
     * dummy function for now, will be replaced once we have a settings activity
     */
    @Test
    fun clickingOnSettingsDoesNothing() {
        onView(ViewMatchers.withId(R.id.toolbar_settings)).perform(click())
    }

    @Test
    fun clickingOnTutorialLaunchesPresentation() {
        clickingOnToolbarItemsWorks(
            PresArrivalActivity::class.java, ViewMatchers.withId(R.id.button_tutorial)
        )
    }

    private fun clickingOnToolbarItemsWorks(
        ActivityName : Class<*>?, id : Matcher<View>
    ) {
        onView(id).perform(click())
        intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ActivityName!!.name)
            )
        )
    }

}