package com.github.h3lp3rs.h3lp.professional

import android.view.View
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.ForumCategoriesActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProMainActivityTest : H3lpAppTest() {

    @get:Rule
    val testRule = ActivityScenarioRule(
        ProMainActivity::class.java
    )

    private fun checkIfDisplayed(id: Int){
        onView(withId(id))
            .check(matches(isDisplayed()))
    }

    @Before
    fun setup() {
        userUid = USER_TEST_ID
        globalContext = getApplicationContext()
        init()
    }

    @After
    fun clean() {
        release()
    }

    private fun clickingOnButtonWorksAndSendsIntent(ActivityName: Class<*>?, id: Matcher<View>) {
        onView(id).perform(click())
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ActivityName!!.name)
            )
        )
    }


    @Test
    fun textsAreDisplayed(){
        checkIfDisplayed(R.id.HelloText)
        checkIfDisplayed(R.id.welcomeText)
    }


    @Test
    fun profileButtonWorks() {
        clickingOnButtonWorksAndSendsIntent(ProProfileActivity::class.java, withId(R.id.pro_profile_button))
    }

    @Test
    fun forumButtonWorks() {
        clickingOnButtonWorksAndSendsIntent(ForumCategoriesActivity::class.java, withId(R.id.faq_button))
    }

    @Test
    fun basicPortalButtonWorks() {
        clickingOnButtonWorksAndSendsIntent(MainPageActivity::class.java, withId(R.id.basic_portal_button))
    }

    @Test
    fun notificationsButtonWorks() {
        clickingOnButtonWorksAndSendsIntent(ProfessionalTypeSelection::class.java, withId(R.id.categories_selection_button))
    }

}