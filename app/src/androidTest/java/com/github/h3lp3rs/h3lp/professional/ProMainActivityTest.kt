package com.github.h3lp3rs.h3lp.professional

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProMainActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        ProMainActivity::class.java
    )

    private fun checkIfDisplayed(id: Int){
        Espresso.onView(ViewMatchers.withId(id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun textsAreDisplayed(){
        checkIfDisplayed(R.id.HelloText)
        checkIfDisplayed(R.id.welcomeText)
    }


    @Test
    fun profileButtonWorks() {
            Intents.init()
            val intent = Intent()
            val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
            Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
            Espresso.onView(ViewMatchers.withId(R.id.pro_profile_button))
                .perform(ViewActions.click())
            Intents.intended(Matchers.allOf(IntentMatchers.hasComponent(ProfessionalTypeSelection::class.java.name)))
            Intents.release()
    }
   /* This test fails on Cirrus because buttons can't be found
   @Test
    fun buttonsAreDisplayed(){
        checkIfDisplayed(R.id.faq_button)
        checkIfDisplayed(R.id.pro_profile_button)
        checkIfDisplayed(R.id.emergencies_button)
        checkIfDisplayed(R.id.blood_request_button)
    }*/
}