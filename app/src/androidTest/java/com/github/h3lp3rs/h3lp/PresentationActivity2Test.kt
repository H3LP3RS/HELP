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
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.core.AllOf
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PresentationActivity2Test {
    @Test
    fun successfulDisplay() {
        val i = Intent(ApplicationProvider.getApplicationContext(), PresentationActivity2::class.java)
        ActivityScenario.launch<PresentationActivity1>(i).use {
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
    }

    @Test
    fun successfulSlideLeft() {
        val i = Intent(ApplicationProvider.getApplicationContext(), PresentationActivity2::class.java)
        ActivityScenario.launch<PresentationActivity2>(i).use {
            Intents.init()
            val intent = Intent()
            val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
            Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
            onView(withId(R.id.pres2_textView6)).perform(ViewActions.swipeLeft())
            Intents.intended(AllOf.allOf(IntentMatchers.hasComponent(PresentationActivity3::class.java.name)))
            Intents.release()
        }
    }

    @Test
    fun successfulSlideRight() {
        val i = Intent(ApplicationProvider.getApplicationContext(), PresentationActivity2::class.java)
        ActivityScenario.launch<PresentationActivity2>(i).use { activityScenario ->
            Intents.init()
            val intent = Intent()
            val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
            Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
            onView(withId(R.id.pres2_textView6)).perform(ViewActions.swipeRight())
            Intents.intended(AllOf.allOf(IntentMatchers.hasComponent(PresentationActivity1::class.java.name)))
            Intents.release()
        }
    }
}