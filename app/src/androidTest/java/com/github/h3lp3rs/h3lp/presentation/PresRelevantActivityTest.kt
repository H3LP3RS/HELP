package com.github.h3lp3rs.h3lp.presentation

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
import com.github.h3lp3rs.h3lp.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PresRelevantActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(PresRelevantActivity::class.java)

    private fun checkIsDisplayed(id: Int) {
        onView(withId(id)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulDisplay() {
        checkIsDisplayed(R.id.pres2_textView1)
        checkIsDisplayed(R.id.pres2_textView2)
        checkIsDisplayed(R.id.pres2_textView3)
        checkIsDisplayed(R.id.pres2_textView4)
        checkIsDisplayed(R.id.pres2_textView5)
        checkIsDisplayed(R.id.pres2_textView6)
        checkIsDisplayed(R.id.pres2_imageView1)
        checkIsDisplayed(R.id.pres2_imageView2)
        checkIsDisplayed(R.id.pres2_imageView3)
    }
}