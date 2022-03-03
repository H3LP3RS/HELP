package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Matchers.allOf

import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        MainActivity::class.java
    )
    @Test
    fun enterNameWorksAndSendsIntent() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)
        val textField = onView(withId(R.id.mainName))
        textField.check(matches(isDisplayed()))
        textField.perform(clearText())
        val name = "Alexis"
        textField.perform(typeText(name))
        closeSoftKeyboard()
        onView(withId(R.id.mainGoButton)).perform(click())
        intended(allOf(hasComponent(GreetingActivity::class.java.name), hasExtra(EXTRA_MESSAGE, name)))
        Intents.release()
    }
}