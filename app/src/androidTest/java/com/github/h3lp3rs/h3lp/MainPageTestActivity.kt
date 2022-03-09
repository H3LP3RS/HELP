package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainPageTestActivity {
    @get:Rule
    val testRule = ActivityScenarioRule(
        MainPageActivity::class.java
    )
    @Test
    fun clickingOnCPRButtonWorksAndSendsIntent() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)

        onView(withId(R.id.CPR_rate_button)).perform(ViewActions.scrollTo(), click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(CprRateActivity::class.java.name))
            )
        Intents.release()
    }

    @Test
    fun clickingOnProfileButtonWorksAndSendsIntent() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)

        onView(withId(R.id.profile)).perform(click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ProfileActivity::class.java.name))
        )
        Intents.release()
    }

   

}