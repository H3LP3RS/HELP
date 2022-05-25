package com.github.h3lp3rs.h3lp

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.dataclasses.Rating
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.setName
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TEST_COMMENT = "comment"

@RunWith(AndroidJUnit4::class)
class RatingActivityTest: H3lpAppTest() {
    @get:Rule
    val testRule = ActivityScenarioRule(
        RatingActivity::class.java
    )

    @Test
    fun sendFeedbackButtonWorks(){
        userUid = USER_TEST_ID
        setDatabase(Databases.RATINGS,MockDatabase())
        val ratingDb = databaseOf(Databases.RATINGS, getApplicationContext())

        onView(withId(R.id.comment))
            .perform(replaceText(TEST_COMMENT))

        onView(withId(R.id.send_feedback_button))
            .perform(click())

        ratingDb.getObject(USER_TEST_ID,Rating::class.java).thenAccept {
            assertEquals(it.comment, TEST_COMMENT)
        }
    }
}