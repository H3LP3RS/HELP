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
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.setName
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when` as When
import org.mockito.Mockito.mock
import org.mockito.kotlin.any

private const val QUESTION = "question"

@RunWith(AndroidJUnit4::class)
class ForumNewPostActivityTest {
    private val forumPosts : MutableMap<String, List<String>> = mutableMapOf()

    @get:Rule
    val testRule = ActivityScenarioRule(
        NewPostActivity::class.java
    )

    @Before
    fun setup() {
        init()
        setName(USER_TEST_ID)

        val forum = mock(Forum::class.java)
        ForumCategory.setForum(CATEGORY_TEST,forum)
        When(forum.newPost(any(), any())).then {
            val content = it.getArgument<String>(1)
            forumPosts[content] = emptyList()
            return@then any()
        }
    }

    @After
    fun clean() {
        release()
    }

    @Test
    fun addNewPostWorks(){
        onView(withId(R.id.newPostCategoryDropdown))
            .perform(ViewActions.replaceText(CATEGORY_TEST_STRING))

        onView(withId(R.id.newPostTitleEditTxt))
            .perform(ViewActions.replaceText(QUESTION))

        onView(withId(R.id.newPostSaveButton)).perform(ViewActions.scrollTo(),ViewActions.click())

        assertEquals(forumPosts[QUESTION], emptyList<String>())
    }

}