package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.firstaid.AedActivity
import com.github.h3lp3rs.h3lp.firstaid.AllergyActivity
import com.github.h3lp3rs.h3lp.firstaid.AsthmaActivity
import com.github.h3lp3rs.h3lp.firstaid.HeartAttackActivity
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirstAidActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        FirstAidActivity::class.java
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

    private fun clickingOnButtonWorksAndSendsIntent(ActivityName: Class<*>?, id: Matcher<View>) {
        Espresso.onView(id).perform(ViewActions.scrollTo(), ViewActions.click())
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ActivityName!!.name)
            )
        )
    }

    @Test
    fun clickAllergyExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(AllergyActivity::class.java, ViewMatchers.withId(R.id.allergy_expand_button))
    }

    @Test
    fun clickHeartAttackExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(HeartAttackActivity::class.java, ViewMatchers.withId(R.id.heart_attack_expand_button))
    }

    @Test
    fun clickAedExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(AedActivity::class.java, ViewMatchers.withId(R.id.aed_expand_button))
    }

    @Test
    fun clickAsthmaExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(AsthmaActivity::class.java, ViewMatchers.withId(R.id.asthma_expand_button))
    }
}