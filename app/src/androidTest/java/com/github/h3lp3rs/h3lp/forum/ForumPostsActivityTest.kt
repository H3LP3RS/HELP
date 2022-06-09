package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.view.forum.EXTRA_FORUM_CATEGORY
import com.github.h3lp3rs.h3lp.view.forum.ForumPostsActivity
import com.github.h3lp3rs.h3lp.view.forum.NewPostActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForumPostsActivityTest {
    @Before
    fun setup() {
        userUid = USER_TEST_ID
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ForumPostsActivity::class.java
        ).apply {
            putExtra(EXTRA_FORUM_CATEGORY, CATEGORY_TEST_STRING)
        }

        ActivityScenario.launch<ForumPostsActivity>(intent)
        init()
    }

    @After
    fun clean() {
        release()
    }

    @Test
    fun addNewPostButtonWorks() {
        onView(withId(R.id.add_post_button)).perform(click())
        intended(
            allOf(
                hasComponent(NewPostActivity::class.java.name)
            )
        )
    }
}