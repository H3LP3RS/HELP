package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.ForumCategory.*
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.forumOf
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.mockForum
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.setName
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val QUESTION = "question"

@RunWith(AndroidJUnit4::class)
class ForumNewPostActivityTest {

    private lateinit var forum: Forum

    @Before
    fun setup() {
        setName(USER_TEST_ID)
        mockForum()
        forum = forumOf(TRAUMATOLOGY)
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            NewPostActivity::class.java
        ).apply {
            putExtra(EXTRA_FORUM_CATEGORY, TRAUMATOLOGY)
        }

        ActivityScenario.launch<NewPostActivity>(intent)
    }

    @Test
    fun addNewPostWorks() {
        // Create new post
        onView(withId(R.id.newPostCategoryDropdown))
            .perform(ViewActions.replaceText(CATEGORY_TEST_STRING))
        onView(withId(R.id.newPostTitleEditTxt))
            .perform(ViewActions.replaceText(QUESTION))
        onView(withId(R.id.newPostSaveButton)).perform(ViewActions.scrollTo(), ViewActions.click())

        // No other way to do this, since delayed futures are not supported by Kotlin
        // --> java.lang.NoSuchMethodError when using a delayed executor
        Thread.sleep(DELAY)
        forum.getAll().thenApply { allPosts ->
            // There should be only one post
            val post = allPosts[0].second[0]
            // Answer should be in the first element of the list
            assertEquals(post.post.content, QUESTION)
        }.join() // Adding a timeout is not possible on Kotlin for the same silly reasons
    }

    companion object {
        private const val DELAY = 2000L
    }
}