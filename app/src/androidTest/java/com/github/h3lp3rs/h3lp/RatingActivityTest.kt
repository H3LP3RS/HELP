package com.github.h3lp3rs.h3lp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.dataclasses.Rating
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.setName
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val USER_TEST_NAME = ""
private const val TEST_COMMENT = "comment"

@RunWith(AndroidJUnit4::class)
class RatingActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        RatingActivity::class.java
    )

    @Test
    fun sendFeedbackButtonWorks(){
        setName(USER_TEST_NAME)
        userUid = H3lpAppTest.USER_TEST_ID
        Databases.setDatabase(Databases.RATINGS,MockDatabase())
        val ratingDb = Databases.databaseOf(Databases.RATINGS)

        onView(withId(R.id.comment))
            .perform(replaceText(TEST_COMMENT))

        onView(withId(R.id.send_feedback_button))
            .perform(click())

        val feedback = ratingDb.getObject(USER_TEST_NAME,Rating::class.java).get()
        assertEquals(feedback.comment, TEST_COMMENT)
    }
}