package com.github.h3lp3rs.h3lp.firstaid

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.dataclasses.FirstAidHowTo.ALLERGY
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.firstaid.EXTRA_FIRST_AID
import com.github.h3lp3rs.h3lp.view.firstaid.FirstAidActivity
import com.github.h3lp3rs.h3lp.view.firstaid.GeneralFirstAidActivity
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllergyActivityTest: H3lpAppTest<GeneralFirstAidActivity>() {

    override fun launch(): ActivityScenario<GeneralFirstAidActivity> {
        // Forge the right intent
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            GeneralFirstAidActivity::class.java
        ).apply {
            putExtra(EXTRA_FIRST_AID, ALLERGY)
        }

        return ActivityScenario.launch(intent)
    }

    @Test
    fun tutorialVideoIsDisplayed() {
        launchAndDo {
            checkIfDisplayed(R.id.epipenVideo)
        }
    }

    @Test
    fun tutorialDescriptionIsDisplayed() {
        launchAndDo {
            checkIfDisplayed(R.id.epipenTutorialStep1)
            checkIfDisplayed(R.id.epipenTutorialStep2)
            checkIfDisplayed(R.id.epipenTutorialStep3)
        }
    }

    @Test
    fun backButtonWorks() {
        launchAndDo {
            Espresso.onView(ViewMatchers.withId(R.id.allergy_back_button))
                .perform(ViewActions.scrollTo(), ViewActions.click())
            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(FirstAidActivity::class.java.name)
                )
            )
        }
    }
}