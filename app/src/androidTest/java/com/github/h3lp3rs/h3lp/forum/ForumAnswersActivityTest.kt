package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.forumOf
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.mockForum
import com.github.h3lp3rs.h3lp.forum.ForumPostsActivity.Companion.selectedPost
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.setName
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.github.h3lp3rs.h3lp.forum.ForumCategory.GENERAL
import com.github.h3lp3rs.h3lp.forum.ForumCategory.TRAUMATOLOGY

const val CATEGORY_TEST_STRING = "GENERAL"
val CATEGORY_TEST = GENERAL
const val QUESTION_TEST = "question"
const val ANSWER_TEST = "answer"

@RunWith(AndroidJUnit4::class)
class ForumAnswersActivityTest {
    private lateinit var forum: Forum

    private val launchIntent = Intent(
        ApplicationProvider.getApplicationContext(), ForumAnswersActivity::class.java
    ).apply {
        putExtra(EXTRA_FORUM_CATEGORY, CATEGORY_TEST_STRING)
    }

    @Before
    fun setup() {
        setName(USER_TEST_ID)
        mockForum()
        forum = forumOf(TRAUMATOLOGY)
    }

    @Test
    fun addNewAnswerWorks() {
        forum.newPost("", QUESTION_TEST).thenAccept { post ->
            selectedPost = post

            launch<ForumAnswersActivity>(launchIntent).use {
                // Listen to all replies
                post.listen { reply ->
                    assertEquals(reply.content, ANSWER_TEST) // TODO Not triggering for some reason
                }

                onView(withId(R.id.text_view_enter_answer)).perform(replaceText(ANSWER_TEST))
                onView(withId(R.id.add_answer_button)).perform(click())
            }
        }.join()
    }
}