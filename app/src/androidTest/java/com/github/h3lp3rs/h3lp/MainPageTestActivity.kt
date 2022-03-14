package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.EnumSet.allOf
import java.util.concurrent.CompletableFuture.anyOf


@RunWith(AndroidJUnit4::class)
class MainPageTestActivity {

    @get:Rule
    val testRule = ActivityScenarioRule(
        MainPageActivity::class.java
    )

    @Before
    fun setup() {
        Intents.init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        Intents.intending(IntentMatchers.anyIntent()).respondWith(intentResult)
    }

    @After
    fun release() {
        Intents.release()
    }

    private fun clickingOnButtonWorksAndSendsIntent(ActivityName: Class<*>?, id: Matcher<View>, isInScrollView: Boolean) {
        if (isInScrollView) {
            onView(id).perform(ViewActions.scrollTo(), click())
        } else {
            onView(id).perform(click())
        }
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ActivityName!!.name)
            )
        )
    }

    @Test
    fun pushingInfoButtonLaunchesPresentation() {
        clickingOnButtonWorksAndSendsIntent(PresentationActivity1::class.java, withId(R.id.tutorialButton), false)
    }

    @Test
    fun clickingOnCPRButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(CprRateActivity::class.java, withId(R.id.CPR_rate_button), true)
    }

    @Test
    fun clickingOnProfileButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(MedicalCardAcivity::class.java, withId(R.id.profile), false)
    }

    @Test
    fun clickingOnHelpButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(HelpParametersActivity::class.java, withId(R.id.HELP_button), false)

    }
}