package com.github.h3lp3rs.h3lp.presentation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PresArrivalActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(PresArrivalActivity::class.java)

    @Test
    fun successfulDisplay() {
        onView(withId(R.id.pres1_textView1)).check(matches(isDisplayed()))
        onView(withId(R.id.pres1_textView2)).check(matches(isDisplayed()))
        onView(withId(R.id.pres1_textView3)).check(matches(isDisplayed()))
        onView(withId(R.id.pres1_textView4)).check(matches(isDisplayed()))
        onView(withId(R.id.pres1_imageView1)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulSwipeLeft() {
        // init()
        // val intent = Intent()
        // val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        // intending(anyIntent()).respondWith(intentResult)
        onView(withId(R.id.pres1_textView4)).perform(swipeLeft())
        // intended(allOf(hasComponent(PresRelevantActivity::class.java.name))) Cirrus broken
        // release()
    }
}