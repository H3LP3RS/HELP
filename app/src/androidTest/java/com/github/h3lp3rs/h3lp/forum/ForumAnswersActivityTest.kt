package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Database
import com.github.h3lp3rs.h3lp.model.database.Databases.PRO_USERS
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.Companion.forumOf
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.Companion.mockForum
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.TRAUMATOLOGY
import com.github.h3lp3rs.h3lp.view.forum.ForumPostsActivity.Companion.selectedPost
import com.github.h3lp3rs.h3lp.model.forum.data.Forum
import com.github.h3lp3rs.h3lp.model.professional.ProUser
import com.github.h3lp3rs.h3lp.view.forum.EXTRA_FORUM_CATEGORY
import com.github.h3lp3rs.h3lp.view.forum.ForumAnswersActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.setName
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

const val CATEGORY_TEST_STRING = "TRAUMATOLOGY"
const val QUESTION_TEST = "question"
const val ANSWER_TEST = "answer"

@RunWith(AndroidJUnit4::class)
class ForumAnswersActivityTest {
    private lateinit var forum: Forum
    private lateinit var proUsersDb: Database

    private val launchIntent = Intent(
        getApplicationContext(), ForumAnswersActivity::class.java
    ).apply {
        putExtra(EXTRA_FORUM_CATEGORY, CATEGORY_TEST_STRING)
    }

    @Before
    fun setup() {
        setName(USER_TEST_ID)
        mockForum(getApplicationContext())
        forum = forumOf(TRAUMATOLOGY, getApplicationContext())
        userUid = USER_TEST_ID
        setDatabase(PRO_USERS, MockDatabase())
        proUsersDb = databaseOf(PRO_USERS, getApplicationContext())
    }

    @Test
    fun addNewAnswerWorks() {
        val proUser = ProUser(USER_TEST_ID, USER_TEST_ID, "", "", "", "", "")
        proUsersDb.setObject(USER_TEST_ID, ProUser::class.java, proUser)

        forum.newPost("", QUESTION_TEST, isPost = false).thenAccept { post ->
            selectedPost = post

            launch<ForumAnswersActivity>(launchIntent).use {
                // Add an answer to post
                onView(withId(R.id.text_view_enter_answer)).perform(replaceText(ANSWER_TEST))
                onView(withId(R.id.add_answer_button)).perform(click())

                // No other way to do this, since delayed futures are not supported by Kotlin
                // --> java.lang.NoSuchMethodError when using a delayed executor
                Thread.sleep(DELAY)
                post.refresh().thenApply { newPost ->
                    // Answer should be in the first element of the list
                    assertEquals(newPost.replies[0].content, ANSWER_TEST)
                }.join()
            }
        }.join()
    }

    @Test
    fun simpleUserCantAnswerPost() {
        proUsersDb.delete(USER_TEST_ID)
        forum.newPost(USER_TEST_ID, QUESTION_TEST, isPost = false).thenAccept { post ->
            selectedPost = post

            launch<ForumAnswersActivity>(launchIntent).use {

                onView(withId(R.id.text_view_enter_answer)).check(
                    matches(
                        not(
                            isDisplayed()
                        )
                    )
                )
                onView(withId(R.id.add_answer_button)).check(
                    matches(
                        not(
                            isDisplayed()
                        )
                    )
                )

            }
        }
    }

    companion object {
        private const val DELAY = 2000L
    }
}