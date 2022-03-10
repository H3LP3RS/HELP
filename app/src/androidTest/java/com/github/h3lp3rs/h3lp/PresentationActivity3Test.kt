package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PresentationActivity3Test {
    @get:Rule
    val testRule = ActivityScenarioRule(PresentationActivity3::class.java)

    @Test
    fun successfulDisplay() {
        onView(withId(R.id.pres3_textView1)).check(matches(isDisplayed()))
        onView(withId(R.id.pres3_textView2)).check(matches(isDisplayed()))
        onView(withId(R.id.pres3_textView3)).check(matches(isDisplayed()))
        onView(withId(R.id.pres3_textView4)).check(matches(isDisplayed()))
        onView(withId(R.id.pres3_textView5)).check(matches(isDisplayed()))
        onView(withId(R.id.pres3_textView6)).check(matches(isDisplayed()))
        onView(withId(R.id.pres3_imageView1)).check(matches(isDisplayed()))
        onView(withId(R.id.pres3_imageView2)).check(matches(isDisplayed()))
        onView(withId(R.id.pres3_imageView3)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulApprovalButton() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
        onView(withId(R.id.pres3_button)).perform(click())
        Intents.intended(AllOf.allOf(IntentMatchers.hasComponent(MainActivity::class.java.name)))
        Intents.release()
    }

    @Test
    fun successfulSlideRight() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
        onView(withId(R.id.pres3_textView5)).perform(ViewActions.swipeRight())
        Intents.intended(AllOf.allOf(IntentMatchers.hasComponent(PresentationActivity2::class.java.name)))
        Intents.release()
    }
}