package com.github.h3lp3rs.h3lp

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.presentation.ToSActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToSActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(ToSActivity::class.java)

    @Test
    fun successfulDisplay() {
        onView(withId(R.id.tos_textView)).check(matches(isDisplayed()))
    }
}