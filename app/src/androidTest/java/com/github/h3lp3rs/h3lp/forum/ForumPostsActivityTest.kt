package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.USER_TEST_ID
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class ForumPostsActivityTest {
    private val forumPosts: MutableMap<String, List<String>> = mutableMapOf()

    @Before
    fun setup() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ForumPostsActivity::class.java
        )

        ActivityScenario.launch<ForumPostsActivity>(intent)
        SignInActivity.userUid = USER_TEST_ID

        val forum = Mockito.mock(Forum::class.java)
        ForumWrapper.set(forum)
        Mockito.`when`(forum.newPost(any(), any())).then {
            val content = it.getArgument<String>(1)
            forumPosts[content] = emptyList()
            return@then any()
        }
        init()
    }

    @After
    fun clean() {
        release()
    }

    @Test
    fun addNewPostButtonWorks(){
        onView(withId(R.id.add_post_button)).perform(click())
        intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(NewPostActivity::class.java.name)
            )
        )
    }

}