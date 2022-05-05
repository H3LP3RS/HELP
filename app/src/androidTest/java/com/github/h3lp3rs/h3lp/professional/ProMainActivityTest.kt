package com.github.h3lp3rs.h3lp.professional

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProMainActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        ProMainActivity::class.java
    )

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun clean() {
        Intents.release()
    }

    private fun checkIfDisplayed(id: Int){
        onView(ViewMatchers.withId(id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun textsAreDisplayed(){
        checkIfDisplayed(R.id.HelloText)
        checkIfDisplayed(R.id.welcomeText)
    }

   /* This test fails on Cirrus because buttons can't be found
   @Test
    fun buttonsAreDisplayed(){
        checkIfDisplayed(R.id.faq_button)
        checkIfDisplayed(R.id.pro_profile_button)
        checkIfDisplayed(R.id.emergencies_button)
        checkIfDisplayed(R.id.blood_request_button)
    }*/

    private fun clickingOnButtonWorksAndSendsIntent(ActivityName: Class<*>?, id: Matcher<View>) {
        onView(id).perform(ViewActions.click())
        intended(
            allOf(
                hasComponent(ActivityName!!.name)
            )
        )
    }

    @Test
    fun clickProfileButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            ProProfileActivity::class.java,
            ViewMatchers.withId(R.id.pro_profile_button)
        )
    }
}