package com.github.h3lp3rs.h3lp.firstaid

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
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
}