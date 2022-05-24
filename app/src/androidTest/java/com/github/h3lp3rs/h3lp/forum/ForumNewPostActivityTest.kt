package com.github.h3lp3rs.h3lp.forum

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.github.h3lp3rs.h3lp.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.ForumCategory.*
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.forumOf
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.mockForum
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.setName
import junit.framework.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

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
        forum = forumOf(TRAUMATOLOGY)
    }

    @Test
    fun addNewPostWorks() {
        // Create new post
        onView(withId(R.id.newPostCategoryDropdown))
            .perform(replaceText(CATEGORY_TEST_STRING))
        onView(withId(R.id.newPostTitleEditTxt))
            .perform(replaceText(QUESTION))
        onView(withId(R.id.newPostSaveButton)).perform(scrollTo(), click())

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

    @Test
    fun notifiedWhenPostReplyWorks() {
        // Create new post with notifications on
        onView(withId(R.id.newPostCategoryDropdown))
            .perform(replaceText(CATEGORY_TEST_STRING))
        onView(withId(R.id.newPostTitleEditTxt))
            .perform(replaceText(QUESTION))
        onView(withId(R.id.switch_enable_notifications))
            .perform(click())
        onView(withId(R.id.newPostSaveButton)).perform(scrollTo(), click())

        // Fetch the post
        // No other way to do this, since delayed futures are not supported by Kotlin
        // --> java.lang.NoSuchMethodError when using a delayed executor
        Thread.sleep(DELAY)
        val post = forum.getAll().thenApply { it[0].second[0] }.join()

        // Simulate a reply
        post.reply(REPLIER, REPLY).join()

        // See if notification popped
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.wait(Until.hasObject(By.textStartsWith(NOTIFICATION_HEADER)), DELAY)

        val notification = uiDevice.findObject(By.text(EXPECTED_NOTIFICATION_TITLE))
        assertNull(notification)
    }

    companion object {
        private const val DELAY = 2000L
        private const val REPLIER = "Replier"
        private const val REPLY = "Reply"
        private const val TIMEOUT_MSG = "Timeout has occurred in future"
        private const val NOTIFICATION_HEADER = "H3LP"
        private const val EXPECTED_NOTIFICATION_TITLE =
            "New reply to your post in $CATEGORY_TEST_STRING from: $REPLIER"
    }
}