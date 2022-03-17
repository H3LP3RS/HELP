package com.github.h3lp3rs.h3lp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.signIn.GoogleSignInAdaptor
import com.github.h3lp3rs.h3lp.signIn.SignIn
import com.github.h3lp3rs.h3lp.signIn.SignInActivity
import com.github.h3lp3rs.h3lp.signIn.SignInInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull


@RunWith(AndroidJUnit4::class)
class SignInActivityTest {
    private val googleSignInPackageName = "com.google.android.gms"

    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )

    @Before
    fun setUp(){
        Intents.init()

        val signInMock = Mockito.mock(SignInInterface::class.java)
        Mockito.`when`(signInMock.isSignedIn()).thenReturn(false)

        testRule.scenario.onActivity { activity ->
            val intent  = GoogleSignInAdaptor.signIn(activity)
            val taskMock = Mockito.mock(Task::class.java)
            Mockito.`when`(taskMock.isSuccessful).thenReturn(true)
            Mockito.`when`(signInMock.signIn(activity)).thenReturn(intent)
            Mockito.`when`(signInMock.authenticate(anyOrNull(), anyOrNull())).thenReturn(taskMock)
        }

        SignIn.set(signInMock as SignInInterface<AuthResult>)
    }

    @Test
    fun signInWithGoogleLaunchesCorrectIntent(){
        onView(withId(R.id.signInButton)).perform(click())
        Intents.intended(hasPackage(googleSignInPackageName))
        assertNotNull(GoogleSignInAdaptor.gso)
    }

    @After
    fun cleanUp(){
        Intents.release()
    }
}




