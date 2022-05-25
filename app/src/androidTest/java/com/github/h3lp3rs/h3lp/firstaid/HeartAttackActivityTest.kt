package com.github.h3lp3rs.h3lp.firstaid

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.FirstAidActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.firstaid.FirstAidHowTo.*
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeartAttackActivityTest {

    fun launch(): ActivityScenario<GeneralFirstAidActivity> {
        // Forge the right intent
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            GeneralFirstAidActivity::class.java
        ).apply {
            putExtra(EXTRA_FIRST_AID, HEART_ATTACK)
        }

        return ActivityScenario.launch(intent)
    }

    /**
     * Check if a component is correctly displayed on the view
     *
     * @param id Id of the component
     */
    private fun checkIfDisplayed(id: Int) {
        Espresso.onView(ViewMatchers.withId(id))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun tutorialVideoIsDisplayed() {
        launchAndDo {
            checkIfDisplayed(R.id.heartAttackVideo)
        }
    }

    @Test
    fun tutorialDescriptionIsDisplayed() {
        launchAndDo {
            checkIfDisplayed(R.id.heartAttackTutorialStep1)
            checkIfDisplayed(R.id.heartAttackTutorialStep2)
            checkIfDisplayed(R.id.heartAttackTutorialStep3)
        }
    }

    @Test
    fun backButtonWorks() {
        launchAndDo {
            Espresso.onView(ViewMatchers.withId(R.id.heart_attack_back_button))
                .perform(ViewActions.scrollTo(), ViewActions.click())
            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(FirstAidActivity::class.java.name)
                )
            )
        }
    }


    private fun launchAndDo(action: () -> Unit) {
        launch().use {
            Intents.init()
            action()
            Intents.release()
        }
    }
}