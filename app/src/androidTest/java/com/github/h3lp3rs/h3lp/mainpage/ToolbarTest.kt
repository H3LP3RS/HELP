package com.github.h3lp3rs.h3lp.mainpage

import android.view.Gravity
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.DrawerActions.*
import androidx.test.espresso.contrib.DrawerMatchers.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import com.github.h3lp3rs.h3lp.view.signin.presentation.PresArrivalActivity
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToolbarTest : H3lpAppTest<MainPageActivity>() {

    @get:Rule
    val testRule = ActivityScenarioRule(
        MainPageActivity::class.java
    )

    @Before
    fun setup() {
        initIntentAndCheckResponse()
    }

    @After
    fun release() {
        Intents.release()
    }

    private val drawerLayout : ViewInteraction? = onView(withId(R.id.drawer_layout))

    private fun closeDrawerLayout() {
        drawerLayout?.perform(close())
    }

    private fun openDrawerLayout() {
        drawerLayout?.perform(open())
    }

    @Test
    fun openingDrawerLayoutWorks() {
        openDrawerLayout()
        drawerLayout?.check(matches(isOpen(Gravity.START)))
    }

    @Test
    fun closingDrawerLayoutWorks() {
        openDrawerLayout()
        closeDrawerLayout()
        drawerLayout?.check(matches(isClosed(Gravity.LEFT)))
    }

    /**
     * dummy function for now, will be replaced once we have a settings activity
     */
    @Test
    fun clickingOnSettingsDoesNothing() {
        onView(withId(R.id.toolbar_settings)).perform(click())
    }

    @Test
    fun clickingOnTutorialLaunchesPresentation() {
        clickingOnToolbarItemsWorks(
            PresArrivalActivity::class.java, withId(R.id.button_tutorial)
        )
    }

    private fun clickingOnToolbarItemsWorks(ActivityName : Class<*>?, id : Matcher<View>) {
        onView(id).perform(click())
        intended(allOf(hasComponent(ActivityName!!.name)))
    }
}