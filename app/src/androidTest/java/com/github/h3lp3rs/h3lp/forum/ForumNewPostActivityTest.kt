package com.github.h3lp3rs.h3lp.forum

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.USER_TEST_ID
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class ForumNewPostActivityTest {
    private val forumPosts : MutableMap<String, List<String>> = mutableMapOf()

    @get:Rule
    val testRule = ActivityScenarioRule(
        NewPostActivity::class.java
    )

    @Before
    fun setup() {
        Intents.init()
        SignInActivity.setName(USER_TEST_ID)

        val forum = Mockito.mock(Forum::class.java)
        ForumWrapper.set(forum)
        Mockito.`when`(forum.newPost(any(), any())).then {
            val content = it.getArgument<String>(1)
            forumPosts[content] = emptyList()
            return@then any()
        }
    }

    @After
    fun clean() {
        Intents.release()
    }

    @Test
    fun addNewPostWorks() {
        onView(withId(R.id.newPostTitleEditTxt)).perform(ViewActions.replaceText("question"))

        onView(withId(R.id.newPostSaveButton)).perform(ViewActions.click())

        assertEquals(forumPosts["question"], emptyList<String>())
    }

}