package com.github.h3lp3rs.h3lp.forum

import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForumCategoriesActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        ForumCategoriesActivity::class.java
    )

    @Before
    fun setup() {
        userUid = USER_TEST_ID
        init()
    }

    @After
    fun clean() {
        release()
    }

    private fun clickingOnButtonWorksAndSendsIntent(ActivityName: Class<*>?, id: Matcher<View>) {
        onView(id).perform(scrollTo(), click())
        intended(
            allOf(
                IntentMatchers.hasComponent(ActivityName!!.name)
            )
        )
    }

    @Test
    fun generalistButtonWorks() {
        clickingOnButtonWorksAndSendsIntent(
            ForumPostsActivity::class.java,
            ViewMatchers.withId(R.id.generalist_expand_button)
        )
    }

    @Test
    fun cardiologyButtonWorks() {
        clickingOnButtonWorksAndSendsIntent(
            ForumPostsActivity::class.java,
            ViewMatchers.withId(R.id.cardio_expand_button)
        )
    }

    @Test
    fun traumaButtonWorks() {
        clickingOnButtonWorksAndSendsIntent(
            ForumPostsActivity::class.java,
            ViewMatchers.withId(R.id.traum_expand_button)
        )
    }

    @Test
    fun pediatryButtonWorks() {
        clickingOnButtonWorksAndSendsIntent(
            ForumPostsActivity::class.java,
            ViewMatchers.withId(R.id.pedia_expand_button)
        )
    }

    @Test
    fun neuroButtonWorks() {
        clickingOnButtonWorksAndSendsIntent(
            ForumPostsActivity::class.java,
            ViewMatchers.withId(R.id.neuro_expand_button)
        )
    }

    @Test
    fun gyneButtonWorks() {
        clickingOnButtonWorksAndSendsIntent(
            ForumPostsActivity::class.java,
            ViewMatchers.withId(R.id.gyne_expand_button)
        )
    }

}