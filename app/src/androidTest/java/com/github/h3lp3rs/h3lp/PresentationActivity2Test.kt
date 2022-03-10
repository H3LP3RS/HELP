package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation.*
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.core.AllOf.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PresentationActivity2Test {
    @get:Rule
    val testRule = ActivityScenarioRule(PresentationActivity2::class.java)

    /*CIRRUS not working
    @Test
    fun successfulDisplay() {
        onView(withId(R.id.pres2_textView1)).check(matches(isDisplayed()))
        onView(withId(R.id.pres2_textView2)).check(matches(isDisplayed()))
        onView(withId(R.id.pres2_textView3)).check(matches(isDisplayed()))
        onView(withId(R.id.pres2_textView4)).check(matches(isDisplayed()))
        onView(withId(R.id.pres2_textView5)).check(matches(isDisplayed()))
        onView(withId(R.id.pres2_textView6)).check(matches(isDisplayed()))
        onView(withId(R.id.pres2_imageView1)).check(matches(isDisplayed()))
        onView(withId(R.id.pres2_imageView2)).check(matches(isDisplayed()))
        onView(withId(R.id.pres2_imageView3)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulSlideLeft() {
        init()
        val intent = Intent()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)
        onView(withId(R.id.pres2_textView6)).perform(swipeLeft())
        intended(allOf(hasComponent(PresentationActivity3::class.java.name)))
        release()
    }

    @Test
    fun successfulSlideRight() {
        init()
        val intent = Intent()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)
        onView(withId(R.id.pres2_textView6)).perform(swipeRight())
        intended(allOf(hasComponent(PresentationActivity1::class.java.name)))
        release()
    }*/
}