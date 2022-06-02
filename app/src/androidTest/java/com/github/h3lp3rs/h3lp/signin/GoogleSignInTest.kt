package com.github.h3lp3rs.h3lp.signin

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasPackage
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.Databases.PREFERENCES
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.storage.Storages.SIGN_IN
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
class GoogleSignInTest : H3lpAppTest() {

    private lateinit var intent : Intent
    private val googleSignInPackageName = "com.google.android.gms"
    private var authenticationStarted = false

    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )

    @Before
    fun setUp() {
        init()

        globalContext = getApplicationContext()
        userUid = USER_TEST_ID

        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()

        val signInMock = mock(SignInInterface::class.java)
        When(signInMock.isSignedIn()).thenReturn(false)

        val userSignIn = storageOf(SIGN_IN)
        userSignIn.setBoolean(globalContext.getString(R.string.KEY_USER_SIGNED_IN), false)

        testRule.scenario.onActivity { activity ->
            intent = GoogleSignInAdapter.signIn(activity)
            val taskMock = mock(Task::class.java)
            When(taskMock.isSuccessful).thenReturn(true)
            When(taskMock.isComplete).thenReturn(true)
            When(signInMock.signIn(activity)).thenReturn(intent)
            When(signInMock.getUid()).thenReturn(USER_TEST_ID)
            When(signInMock.authenticate(anyOrNull(), anyOrNull())).thenAnswer {
                authenticationStarted = true
                taskMock
            }
        }

        SignIn.set(signInMock as SignInInterface<AuthResult>)
    }

    @Test
    fun signInWithGoogleLaunchesCorrectIntent() {
        inputCorrectUsername()
        clickSignInButton()
        intended(hasPackage(googleSignInPackageName))
    }

    @Test
    fun signInWithGoogleLaunchesAuthenticationProcess() {
        inputCorrectUsername()
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
        inputCorrectUsername()
        onView(withId(R.id.signInButton)).perform(click())
    }

    private fun inputCorrectUsername() {
        onView(withId(R.id.text_field_username)).perform(ViewActions.replaceText((USER_TEST_NAME)))
    }

    @Test
    fun emptyUsernameLeadsToError() {
        onView(withId(R.id.signInButton)).perform(click())

        onView(withText(R.string.username_error_field_msg)).check(
            matches(
                withEffectiveVisibility(Visibility.VISIBLE)
            )
        )
    }

    @After
    fun cleanUp() {
        release()
    }
}




