package com.github.h3lp3rs.h3lp.firstaid

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.FirstAidActivity
import com.github.h3lp3rs.h3lp.R
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AedActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        AedActivity::class.java
    )

    /**
     * Check if a component is correctly displayed on the view
     *
     * @param id Id of the component
     */
    private fun checkIfDisplayed(id: Int){
        onView(withId(id))
            .check(matches(isDisplayed()))
    }
    @Test
    fun tutorialVideoIsDisplayed(){
        checkIfDisplayed(R.id.aedVideo)
    }

    @Test
    fun tutorialDescriptionIsDisplayed(){
        checkIfDisplayed(R.id.aedTutorialStep1)
        checkIfDisplayed(R.id.aedTutorialStep2)
        checkIfDisplayed(R.id.aedTutorialStep3)
    }

    @Test
    fun backButtonWorks(){
        init()
        onView(withId(R.id.aed_back_button)).perform(scrollTo(), click())
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(FirstAidActivity::class.java.name)
            )
        )
        release()
    }
}