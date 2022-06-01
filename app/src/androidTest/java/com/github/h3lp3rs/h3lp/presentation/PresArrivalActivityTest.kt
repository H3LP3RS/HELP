package com.github.h3lp3rs.h3lp.presentation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.view.signin.presentation.PresArrivalActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PresArrivalActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(PresArrivalActivity::class.java)

    private fun checkIsDisplayed(id: Int) {
        onView(withId(id)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulDisplay() {
        checkIsDisplayed(R.id.pres1_textView1)
        checkIsDisplayed(R.id.pres1_textView2)
        checkIsDisplayed(R.id.pres1_textView3)
        checkIsDisplayed(R.id.pres1_textView4)
        checkIsDisplayed(R.id.pres1_imageView1)
    }
}