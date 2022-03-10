package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
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
class PresentationActivity1Test {
    @get:Rule
    val testRule = ActivityScenarioRule(PresentationActivity1::class.java)

    @Test
    fun successfulDisplay() {
        onView(withId(R.id.pres1_textView1)).check(matches(isDisplayed()))
        onView(withId(R.id.pres1_textView2)).check(matches(isDisplayed()))
        onView(withId(R.id.pres1_textView3)).check(matches(isDisplayed()))
        onView(withId(R.id.pres1_textView4)).check(matches(isDisplayed()))
        onView(withId(R.id.pres1_imageView1)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulSlideLeft() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
        onView(withId(R.id.pres1_textView4)).perform(ViewActions.swipeLeft())
        Intents.intended(AllOf.allOf(IntentMatchers.hasComponent(PresentationActivity2::class.java.name)))
        Intents.release()
    }
}