package com.github.h3lp3rs.h3lp.mainpage

import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.*
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import com.github.h3lp3rs.h3lp.view.profile.MedicalCardActivity
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SideBarTest : H3lpAppTest<MainPageActivity>() {
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


    private val drawerLayout: ViewInteraction? = onView(withId(R.id.drawer_layout))

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


    @Test
    fun clickingOnHomeIconSendsToHome() {
        openDrawerLayout()
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_home))
        Thread.sleep(WAIT_UI)
        drawerLayout?.check(matches(isClosed()))
    }

    @Test
    fun clickingOnProfileIconSendsToProfilePage() {
        openDrawerLayout()
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_profile))
        intended(allOf(hasComponent(MedicalCardActivity::class.java.name)))
    }

    /**
     * dummy function for coverage, will be deleted later
     */
    @Test
    fun clickingOnIconDoesNothing() {
        openDrawerLayout()
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_rate_us))
    }
}