package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.Databases.PRO_USERS
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.setForum
import com.github.h3lp3rs.h3lp.forum.ForumCategory.GENERAL
import com.github.h3lp3rs.h3lp.forum.ForumPostsActivity.Companion.selectedPost
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import com.github.h3lp3rs.h3lp.professional.ProUser
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.setName
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import junit.framework.Assert.assertEquals
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import java.time.ZonedDateTime
import org.mockito.Mockito.`when` as When

const val CATEGORY_TEST_STRING = "GENERAL"
val CATEGORY_TEST = GENERAL
const val QUESTION_TEST = "question"
const val ANSWER_TEST = "answer"
const val USER_NAME_TEST = "Jane Doe"

@RunWith(AndroidJUnit4::class)
class ForumAnswersActivityTest {
    private val forumPosts : MutableMap<String, List<String>> = mutableMapOf()
    private lateinit var proUsersDb : Database

    @Before
    fun setup() {
        val forum = mock(Forum::class.java)
        setForum(CATEGORY_TEST, forum)

        When(forum.newPost(any(), any())).then {
            val content = it.getArgument<String>(1)
            forumPosts[QUESTION_TEST] = listOf(content)
            any()
        }
        When(forum.child(any() as String)).thenReturn(forum)
        When(forum.child(any() as Path)).thenReturn(forum)

        selectedPost = ForumPost(
            forum, ForumPostData(
                "", QUESTION_TEST, ZonedDateTime.now(), "", "", GENERAL
            ), emptyList()
        )

        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        setName(USER_NAME_TEST)
        setDatabase(PRO_USERS, MockDatabase())
        proUsersDb = databaseOf(PRO_USERS)
    }

    private fun launchActivity() {
        val intent = Intent(
            getApplicationContext(), ForumAnswersActivity::class.java
        ).apply {
            putExtra(EXTRA_FORUM_CATEGORY, CATEGORY_TEST_STRING)
        }
        ActivityScenario.launch<ForumAnswersActivity>(intent)
        init()
    }

    @After
    fun clean() {
        release()
    }

    @Test
    fun addNewAnswerWorksForVerifiedProUser() {
        val proUser = ProUser(USER_TEST_ID, USER_NAME_TEST, "", "", "", "", "")
        proUsersDb.setObject(USER_TEST_ID, ProUser::class.java, proUser)

        launchActivity()

        onView(withId(R.id.text_view_enter_answer)).perform(replaceText(ANSWER_TEST))
        onView(withId(R.id.add_answer_button)).perform(click())
        assertEquals(forumPosts[QUESTION_TEST], listOf(ANSWER_TEST))
    }

    @Test
    fun simpleUserCantAnswerPost() {
        proUsersDb.delete(USER_TEST_ID)

        launchActivity()

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