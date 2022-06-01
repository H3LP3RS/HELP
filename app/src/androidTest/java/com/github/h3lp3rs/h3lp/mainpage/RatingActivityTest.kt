package com.github.h3lp3rs.h3lp.mainpage

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.dataclasses.Rating
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.mainpage.RatingActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import org.junit.Assert.assertEquals
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
        setDatabase(Databases.RATINGS, MockDatabase())
        val ratingDb = databaseOf(Databases.RATINGS)

        onView(withId(R.id.comment))
            .perform(replaceText(TEST_COMMENT))

        onView(withId(R.id.send_feedback_button))
            .perform(click())

        ratingDb.getObject(USER_TEST_ID, Rating::class.java).thenAccept {
            assertEquals(it.comment, TEST_COMMENT)
        }
    }
}