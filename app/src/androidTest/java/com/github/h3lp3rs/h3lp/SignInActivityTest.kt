package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat.startActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.dx.command.Main
import com.github.h3lp3rs.h3lp.signIn.GoogleSignInAdaptor
import com.github.h3lp3rs.h3lp.signIn.SignIn
import com.github.h3lp3rs.h3lp.signIn.SignInActivity
import com.github.h3lp3rs.h3lp.signIn.SignInInterface
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import junit.framework.Assert.*
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnit.rule


@RunWith(AndroidJUnit4::class)
class SignInActivityTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )

    @Before
    fun setUp(){
        Intents.init()

        val signInMock = mock(SignInInterface::class.java)
        `when`(signInMock.isSignedIn()).thenReturn(false)

        testRule.scenario.onActivity { activity ->
            val intent  = GoogleSignInAdaptor.signIn(activity)
            val activityResult = ActivityResult(Activity.RESULT_OK, intent)
            val taskMock = mock(Task::class.java)
            `when`(taskMock.isSuccessful).thenReturn(true)
            `when`(signInMock.signIn(activity)).thenReturn(intent)
            `when`(signInMock.authenticate(activityResult, activity)).thenReturn(taskMock)
        }

        SignIn.set(signInMock as SignInInterface<AuthResult>)
    }

    @Test
    fun signInButtonLaunchesCorrectIntent(){
        onView(withId(R.id.signInButton)).perform(click())
        Intents.intended(hasPackage("com.github.h3lp3rs.h3lp"))
        assertNotNull(GoogleSignInAdaptor.gso)
    }

    @After
    fun cleanUp(){
        Intents.release()
    }
}




