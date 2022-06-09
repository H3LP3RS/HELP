package com.github.h3lp3rs.h3lp.firstaid

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.dataclasses.FirstAidHowTo
import com.github.h3lp3rs.h3lp.model.dataclasses.FirstAidHowTo.*
import com.github.h3lp3rs.h3lp.view.firstaid.EXTRA_FIRST_AID
import com.github.h3lp3rs.h3lp.view.firstaid.FirstAidActivity
import com.github.h3lp3rs.h3lp.view.firstaid.GeneralFirstAidActivity
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirstAidActivityTest : H3lpAppTest<FirstAidActivity>() {

    @get:Rule
    val testRule = ActivityScenarioRule(
        FirstAidActivity::class.java
    )

    @Before
    fun setup() {
        SignInActivity.userUid = USER_TEST_ID
        initIntentAndCheckResponse()
    }

    @After
    fun release() {
        Intents.release()
    }

    private fun clickingOnButtonWorksAndSendsIntentWithExtra(
        ActivityName: Class<*>?,
        id: Matcher<View>,
        extraName: String,
        firstAidExtra: FirstAidHowTo
    ) {
        onView(id).perform(scrollTo(), click())
        intended(
            allOf(
                hasComponent(ActivityName!!.name),
                hasExtra(extraName, firstAidExtra)
            )
        )
    }

    @Test
    fun clickAllergyExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntentWithExtra(
            GeneralFirstAidActivity::class.java,
            withId(R.id.allergy_expand_button),
            EXTRA_FIRST_AID,
            ALLERGY
        )
    }

    @Test
    fun clickHeartAttackExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntentWithExtra(
            GeneralFirstAidActivity::class.java,
            withId(R.id.heart_attack_expand_button),
            EXTRA_FIRST_AID,
            HEART_ATTACK
        )
    }

    @Test
    fun clickAedExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntentWithExtra(
            GeneralFirstAidActivity::class.java,
            withId(R.id.aed_expand_button),
            EXTRA_FIRST_AID,
            AED
        )
    }

    @Test
    fun clickAsthmaExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntentWithExtra(
            GeneralFirstAidActivity::class.java,
            withId(R.id.asthma_expand_button),
            EXTRA_FIRST_AID,
            ASTHMA
        )
    }

    @Test
    fun clickBackButtonWorksAndSendsIntent() {
        onView(withId(R.id.first_aid_back_button)).perform(scrollTo(), click())
        assertEquals(testRule.scenario.state, Lifecycle.State.RESUMED)
    }
}