package com.github.h3lp3rs.h3lp.mainpage

import android.content.Intent
import android.view.Gravity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
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
import com.github.h3lp3rs.h3lp.view.mainpage.RatingActivity
import com.github.h3lp3rs.h3lp.view.profile.MedicalCardActivity
import com.github.h3lp3rs.h3lp.view.profile.SettingsActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.view.signin.presentation.PresArrivalActivity
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SideBarTest : H3lpAppTest<MainPageActivity>() {

    @Before
    fun setup() {
        userUid = USER_TEST_ID
        val intent = Intent(
            ApplicationProvider.getApplicationContext(), MainPageActivity::class.java
        )
        ActivityScenario.launch<MainPageActivity>(intent)
        init()
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

    @Test
    fun clickingOnSettingsIconSendsToSettingsPage() {
        openDrawerLayout()
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_settings))
        intended(allOf(hasComponent(SettingsActivity::class.java.name)))
    }

    @Test
    fun clickingOnAboutUsIconSendsToPresentationPage() {
        openDrawerLayout()
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_about_us))
        intended(allOf(hasComponent(PresArrivalActivity::class.java.name)))
    }

    @Test
    fun clickingOnLogOutIconSendsToSignInPage() {
        openDrawerLayout()
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_logout))
        intended(allOf(hasComponent(SignInActivity::class.java.name)))
    }

    @Test
    fun clickingOnRateUsIconSendsToRatingPage() {
        openDrawerLayout()
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_rate_us))
        intended(allOf(hasComponent(RatingActivity::class.java.name)))
    }

}
