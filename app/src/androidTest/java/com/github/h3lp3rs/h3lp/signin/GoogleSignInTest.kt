package com.github.h3lp3rs.h3lp.signin

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasPackage
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.preferences.Preferences
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.Files.*
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.USER_AGREE
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.clearAllPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When


@RunWith(AndroidJUnit4::class)
class GoogleSignInTest {
    private lateinit var intent: Intent
    private val googleSignInPackageName = "com.google.android.gms"
    private var authenticationStarted = false

    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )

    @Before
    fun setUp() {
        init()
        clearAllPreferences(getApplicationContext())
        Preferences(PRESENTATION, getApplicationContext()).setBool(USER_AGREE, true)

        val signInMock = mock(SignInInterface::class.java)
        When(signInMock.isSignedIn()).thenReturn(false)

        testRule.scenario.onActivity { activity ->
            intent = GoogleSignInAdapter.signIn(activity)
            val taskMock = mock(Task::class.java)
            When(taskMock.isSuccessful).thenReturn(true)
            When(taskMock.isComplete).thenReturn(true)
            When(signInMock.signIn(activity)).thenReturn(intent)
            When(signInMock.authenticate(anyOrNull(), anyOrNull())).thenAnswer {
                authenticationStarted = true
                taskMock
            }
        }

        SignIn.set(signInMock as SignInInterface<AuthResult>)
    }

    @Test
    fun signInWithGoogleLaunchesCorrectIntent() {
        clickSignInButton()
        intended(hasPackage(googleSignInPackageName))
    }

    @Test
    fun signInWithGoogleLaunchesAuthenticationProcess() {
        clickSignInButton()
        assertNotNull(GoogleSignInAdapter.gso)
        testRule.scenario.onActivity { activity ->
            activity.authenticateUser(
                ActivityResult(Activity.RESULT_OK, intent), activity
            )
        }
        assertEquals(authenticationStarted, true)
    }

    private fun clickSignInButton() {
        onView(withId(R.id.signInButton)).perform(click())
    }

    @After
    fun cleanUp() {
        release()
    }
}




