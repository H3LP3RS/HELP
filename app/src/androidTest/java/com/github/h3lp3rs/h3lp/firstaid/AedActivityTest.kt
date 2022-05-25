package com.github.h3lp3rs.h3lp.firstaid

import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.*
import com.github.h3lp3rs.h3lp.firstaid.FirstAidHowTo.*
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AedActivityTest {

    fun launch(): ActivityScenario<GeneralFirstAidActivity> {
        // Forge the right intent
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            GeneralFirstAidActivity::class.java
        ).apply {
            putExtra(EXTRA_FIRST_AID, AED)
        }

        return ActivityScenario.launch(intent)
    }

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
        launchAndDo {
            checkIfDisplayed(R.id.aedVideo)
        }
    }

    @Test
    fun tutorialDescriptionIsDisplayed(){
        launchAndDo {
            checkIfDisplayed(R.id.aedTutorialStep1)
            checkIfDisplayed(R.id.aedTutorialStep2)
            checkIfDisplayed(R.id.aedTutorialStep3)
        }
    }

    @Test
    fun backButtonWorks() {
        launchAndDo {
            onView(withId(R.id.aed_back_button)).perform(scrollTo(), ViewActions.click())
            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(FirstAidActivity::class.java.name)
                )
            )
        }
    }


    private fun launchAndDo(action: () -> Unit) {
        launch().use {
            init()
            action()
            release()
        }
    }
}