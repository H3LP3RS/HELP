package com.github.h3lp3rs.h3lp.forum

import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.USER_TEST_ID
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import org.hamcrest.Matcher
import org.hamcrest.Matchers
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
        SignInActivity.globalContext = ApplicationProvider.getApplicationContext()
        SignInActivity.userUid = USER_TEST_ID
        Intents.init()
    }

    @After
    fun clean() {
        Intents.release()
    }

    private fun clickingOnButtonWorksAndSendsIntent(ActivityName: Class<*>?, id: Matcher<View>) {
        Espresso.onView(id).perform(ViewActions.scrollTo(),ViewActions.click())
        Intents.intended(
            Matchers.allOf(
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