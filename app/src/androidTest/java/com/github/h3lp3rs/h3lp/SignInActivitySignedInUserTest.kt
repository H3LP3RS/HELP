package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.signIn.SignIn
import com.github.h3lp3rs.h3lp.signIn.SignInActivity
import com.github.h3lp3rs.h3lp.signIn.SignInInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull

@RunWith(AndroidJUnit4::class)
class SignInActivitySignedInUserTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )

    @Before
    fun setUp(){
        Intents.init()

        val signInMock = Mockito.mock(SignInInterface::class.java)
        Mockito.`when`(signInMock.isSignedIn()).thenReturn(true)

        testRule.scenario.onActivity { activity ->
            val intent  = Intent(ApplicationProvider.getApplicationContext(), activity.javaClass)
            val taskMock = Mockito.mock(Task::class.java)
            Mockito.`when`(taskMock.isSuccessful).thenReturn(true)
            Mockito.`when`(signInMock.signIn(activity)).thenReturn(intent)
            Mockito.`when`(signInMock.authenticate(anyOrNull(), anyOrNull())).thenReturn(taskMock)
        }

        SignIn.set(signInMock as SignInInterface<AuthResult>)
    }

    @Test
    fun signedInUserMoveToMainPageDirectly(){
        Espresso.onView(ViewMatchers.withId(R.id.signInButton)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MainPageActivity::class.java.name))
    }

    @After
    fun cleanUp(){
        Intents.release()
    }
}
