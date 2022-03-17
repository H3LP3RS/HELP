package com.github.h3lp3rs.h3lp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )

    @Test
    fun loginButtonLaunchesIntent() {
        Intents.init()
        onView(withId(R.id.signInButton)).check(matches(isDisplayed())).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasPackage("com.google.android.gms"))
        Intents.release()
    }
}