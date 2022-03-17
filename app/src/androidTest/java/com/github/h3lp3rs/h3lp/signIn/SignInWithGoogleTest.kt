package com.github.h3lp3rs.h3lp.signIn

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
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
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull


@RunWith(AndroidJUnit4::class)
class SignInWithGoogleTest {
    private lateinit var intent : Intent
    private val googleSignInPackageName = "com.google.android.gms"
    private var authenticationStarted = false

    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )

    @Before
    fun setUp(){
        Intents.init()

        val signInMock = Mockito.mock(SignInInterface::class.java)
        `when`(signInMock.isSignedIn()).thenReturn(false)

        testRule.scenario.onActivity { activity ->
            intent  = GoogleSignInAdaptor.signIn(activity)
            val taskG = GoogleSignInAdaptor.authenticate(ActivityResult(Activity.RESULT_OK,intent),activity)
            val taskMock = Mockito.mock(Task::class.java)
            `when`(taskMock.isSuccessful).thenReturn(true)
            `when`(taskMock.isComplete).thenReturn(true)
            `when`(signInMock.signIn(activity)).thenReturn(intent)
            `when`(signInMock.authenticate(anyOrNull(), anyOrNull())).thenAnswer{
                authenticationStarted = true
                taskMock
            }
        }

        SignIn.set(signInMock as SignInInterface<AuthResult>)
    }

    @Test
    fun signInWithGoogleLaunchesCorrectIntent(){
        clickSignInButton()
        Intents.intended(hasPackage(googleSignInPackageName))
    }

    @Test
    fun signInWithGoogleLaunchesAuthenticationProcess(){
        clickSignInButton()
        assertNotNull(GoogleSignInAdaptor.gso)
        testRule.scenario.onActivity { activity -> activity.authenticateUser(ActivityResult(Activity.RESULT_OK,intent),activity)}
        assert(authenticationStarted)
    }

    private fun clickSignInButton(){
        onView(withId(R.id.signInButton)).perform(click())
    }

    @After
    fun cleanUp(){
        Intents.release()
    }
}




