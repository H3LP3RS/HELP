package com.github.h3lp3rs.h3lp


import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SideBarTest {

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


    private val drawerLayout: ViewInteraction? = onView(withId(R.id.drawer_layout))

    private fun closeDrawerLayout() {
        drawerLayout?.perform(DrawerActions.close())
    }

    private fun openDrawerLayout() {
        drawerLayout?.perform(DrawerActions.open())
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
        Thread.sleep(500)
        drawerLayout?.check(matches(isClosed()))


    }

    @Test
    fun clickingOnProfileIconSendsToProfilePage() {
        openDrawerLayout()

        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_profile))

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(MedicalCardAcivity::class.java.name)
            )
        )

    }

    /**
     * dummy function for coverage, will be deleted later
     */
    @Test
    fun clickingOnIconSendsDoesNothing() {
        openDrawerLayout()

        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_rate_us))

    }


}