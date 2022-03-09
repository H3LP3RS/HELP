package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.*

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*


@RunWith(AndroidJUnit4::class)
class HelpParametersActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        HelpParametersActivity::class.java
    )

    @Test
    fun clickSearchHelpWithMedsWorksAndSendsIntent() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)

        // select one med
        val medButton0 = onView(withId(R.id.selectMedsButton0))

        medButton0.check(ViewAssertions.matches(isNotChecked()))

        // select the medication
        medButton0.perform(ViewActions.click())

        // click the search help button
        val searchHelpButton = onView(withId(R.id.help_params_search_button))

        searchHelpButton.check(ViewAssertions.matches(isDisplayed()))
        searchHelpButton.perform(ViewActions.click())

        Intents.intended(
            allOf(
                IntentMatchers.hasComponent(AwaitHelpActivity::class.java.name),
            )
        )

        Intents.release()
    }

    @Test
    fun clickPhoneButtonDialsEmergencyNumber() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)

        val phoneButton = onView(withId(R.id.help_params_call_button))

        phoneButton.check(ViewAssertions.matches(isDisplayed()))
        phoneButton.perform(ViewActions.click())

        val number = "tel:$EMERGENCY_NUMBER"

        // emergency number is dialed:
        Intents.intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_DIAL),
                IntentMatchers.hasData(Uri.parse(number))
            )
        )

        Intents.release()
    }

    @Test
    fun clickSearchHelpWithNoMedsDoesNotChangeActivity() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)

        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)

        val searchHelpButton = onView(withId(R.id.help_params_search_button))

        searchHelpButton.check(ViewAssertions.matches(isDisplayed()))
        searchHelpButton.perform(ViewActions.click())

        // no new intent:
        assertThat(Intents.getIntents().size, `is`(0))

        Intents.release()
    }
}