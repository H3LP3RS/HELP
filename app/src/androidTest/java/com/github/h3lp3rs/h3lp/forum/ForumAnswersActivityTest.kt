package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.USER_TEST_ID
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import java.time.ZonedDateTime

const val CATEGORY_TEST = "GENERAL"
const val QUESTION_TEST = "question"
const val ANSWER_TEST = "answer"

@RunWith(AndroidJUnit4::class)
class ForumAnswersActivityTest {
    private val forumPosts: MutableMap<String, List<String>> = mutableMapOf()

    @Before
    fun setup() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ForumAnswersActivity::class.java
        ).apply {
            putExtra(EXTRA_FORUM_CATEGORY, CATEGORY_TEST)
        }

        SignInActivity.username = USER_TEST_ID

        val forum = Mockito.mock(Forum::class.java)
        ForumWrapper.set(forum)
        Mockito.`when`(forum.newPost(any(), any())).then {
            val content = it.getArgument<String>(1)
            forumPosts[QUESTION_TEST] = listOf(content)
            return@then any()
        }
        Mockito.`when`(forum.child(any() as String)).thenReturn(forum)
        Mockito.`when`(forum.child(any() as Path)).thenReturn(forum)

        ForumPostsActivity.selectedPost = ForumPost(
            forum, ForumPostData(
                "",
                QUESTION_TEST, ZonedDateTime.now(), "", "", ForumCategory.GENERAL
            ),
            emptyList()
        )
        ActivityScenario.launch<ForumAnswersActivity>(intent)

        init()
    }

    @After
    fun clean() {
        release()
    }

    @Test
    fun addNewAnswerWorks() {
        onView(withId(R.id.text_view_enter_answer))
            .perform(replaceText(ANSWER_TEST))

        onView(withId(R.id.add_answer_button))
            .perform(click())

        assertEquals(forumPosts[QUESTION_TEST], listOf(ANSWER_TEST))
    }
}