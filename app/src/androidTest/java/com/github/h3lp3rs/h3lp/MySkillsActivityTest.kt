package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MySkillsActivityTest {
    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @get:Rule
    val testRule = ActivityScenarioRule(
        MySkillsActivity::class.java
    )

    @Test
    fun backButtonWork(){
        Intents.init()
        val intent = Intent()
        Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        onView(ViewMatchers.withId(R.id.mySkillsBackButton))
            .perform(ViewActions.click())
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(MainPageActivity::class.java.name),
            )
        )
        Intents.release()
    }

    @Test
    fun clickingOnHelpDisplayDialogue() {

        onView(ViewMatchers.withId(R.id.mySkillsHelpButton))
            .perform(ViewActions.click())

        onView(ViewMatchers.withText(R.string.my_helper_skills))
            .inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


}