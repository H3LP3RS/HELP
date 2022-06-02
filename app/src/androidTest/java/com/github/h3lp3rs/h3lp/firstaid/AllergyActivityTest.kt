package com.github.h3lp3rs.h3lp.firstaid

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
import com.github.h3lp3rs.h3lp.FirstAidActivity
import com.github.h3lp3rs.h3lp.R
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AllergyActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        AllergyActivity::class.java
    )

    /**
     * Check if a component is correctly displayed on the view
     *
     * @param id Id of the component
     */
    private fun checkIfDisplayed(id: Int){
        Espresso.onView(ViewMatchers.withId(id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Test
    fun tutorialVideoIsDisplayed(){
        checkIfDisplayed(R.id.epipenVideo)
    }

    @Test
    fun tutorialDescriptionIsDisplayed(){
        checkIfDisplayed(R.id.epipenTutorialStep1)
        checkIfDisplayed(R.id.epipenTutorialStep2)
        checkIfDisplayed(R.id.epipenTutorialStep3)
    }

    @Test
    fun backButtonWorks(){
        init()
        onView(withId(R.id.allergy_back_button))
            .perform(scrollTo(), click())
        intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(FirstAidActivity::class.java.name)
            )
        )
        release()
    }
}