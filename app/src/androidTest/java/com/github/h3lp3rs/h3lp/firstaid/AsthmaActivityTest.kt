package com.github.h3lp3rs.h3lp.firstaid

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.FirstAidActivity
import com.github.h3lp3rs.h3lp.R
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AsthmaActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        AsthmaActivity::class.java
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
        checkIfDisplayed(R.id.asthmaVideo)
    }

    @Test
    fun tutorialDescriptionIsDisplayed(){
        checkIfDisplayed(R.id.asthmaTutorialStep1)
        checkIfDisplayed(R.id.asthmaTutorialStep2)
        checkIfDisplayed(R.id.asthmaTutorialStep3)
    }

    @Test
    fun backButtonWorks(){
        init()
        onView(ViewMatchers.withId(R.id.asthma_back_button))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(FirstAidActivity::class.java.name)
            )
        )
        release()
    }
}