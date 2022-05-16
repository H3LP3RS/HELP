package com.github.h3lp3rs.h3lp.forum

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.forumOf
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.mockForum
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.setName
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.kotlin.any

private const val QUESTION = "question"

@RunWith(AndroidJUnit4::class)
class ForumNewPostActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        NewPostActivity::class.java
    )

    private lateinit var forum: Forum

    @Before
    fun setup() {
        setName(USER_TEST_ID)
        mockForum()
        forum = forumOf(CATEGORY_TEST)
    }

    @Test
    fun addNewPostWorks() {
        forum.listenToAll { post ->
            assertEquals(post.content, QUESTION) // TODO Not triggering for some reason
        }
        onView(withId(R.id.newPostCategoryDropdown))
            .perform(ViewActions.replaceText(CATEGORY_TEST_STRING))
        onView(withId(R.id.newPostTitleEditTxt))
            .perform(ViewActions.replaceText(QUESTION))
        onView(withId(R.id.newPostSaveButton)).perform(ViewActions.scrollTo(), ViewActions.click())
    }
}