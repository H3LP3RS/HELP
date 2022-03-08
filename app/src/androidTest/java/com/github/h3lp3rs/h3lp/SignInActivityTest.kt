package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers
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
    fun loginButtonIsPresentAndClickable() {
        onView(withId(R.id.signInButton))
            .check(matches(isDisplayed()));

        onView(withId(R.id.signInButton)).perform(ViewActions.click()).check(matches((isEnabled())));
    }

}