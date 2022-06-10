package com.github.h3lp3rs.h3lp.mainpage

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.ViewInteraction
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.view.mainpage.CprRateActivity

@RunWith(AndroidJUnit4::class)
class CprRateActivityTest {
    private val targetContext: Context = getApplicationContext()
    private val startText =  targetContext.resources.getString(R.string.cpr_rate_button_start)
    private val stopText =  targetContext.resources.getString(R.string.cpr_rate_button_stop)
    private val rateButton: ViewInteraction = onView(withId(R.id.startRateButton))

    @get:Rule
    val testRule = ActivityScenarioRule(CprRateActivity::class.java)

    /**
    * Checking that clicking the button once gets it to start the animation makes its text change to
     * "STOP"
     */
    @Test
    fun buttonHasRightTextValueAfterClick() {
        rateButton.check(matches(isDisplayed()))
        rateButton.perform(click())
        rateButton.check(matches(withText(stopText)))
    }

    /**
     * Checking that the button cycles through START and STOP after clicking on it
     */
    @Test
    fun buttonTextCycles() {
        rateButton.perform(click()).perform(click())
        rateButton.check(matches(withText(startText)))
    }

    /**
     * Checking that the cpr resets when removed from screen
     */
    @Test
    fun onPauseResetsCpr() {
        buttonHasRightTextValueAfterClick()
        // Pause and resume activity
        testRule.scenario.moveToState(Lifecycle.State.STARTED)
        testRule.scenario.moveToState(Lifecycle.State.RESUMED)
        rateButton.check(matches(isDisplayed()))
        rateButton.check(matches(withText(startText)))
    }
}