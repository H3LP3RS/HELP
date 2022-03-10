package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation.*
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun pushingInfoButtonLaunchesPresentation() {
        init()
        val intent = Intent()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)
        onView(withId(R.id.main_imageButton)).perform(click())
        intended(allOf(hasComponent(PresentationActivity1::class.java.name)))
        release()
    }
}