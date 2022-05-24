package com.github.h3lp3rs.h3lp.signin

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When
import com.github.h3lp3rs.h3lp.storage.Storages.SIGN_IN


@RunWith(AndroidJUnit4::class)
class NewUserSignInTest : H3lpAppTest() {

    private lateinit var intent: Intent
    private var authenticationStarted = false
    private val correctUsername = "username"

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

        val userSignIn = Storages.storageOf(SIGN_IN)
        userSignIn.setBoolean(globalContext.getString(R.string.KEY_USER_SIGNED_IN), false)

        testRule.scenario.onActivity { activity ->
            intent = Intent(getApplicationContext(), activity.javaClass)
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
    fun newUserSignInLaunchesCorrectIntent() {
        inputCorrectUsername()
        clickSignInButton()
        intended(hasComponent(SignInActivity::class.java.name))
    }

    @Test
    fun newUserSignInLaunchesAuthenticationProcess() {
        inputCorrectUsername()
        clickSignInButton()

        testRule.scenario.onActivity { activity ->
            activity.authenticateUser(
                ActivityResult(
                    Activity.RESULT_OK,
                    intent
                ), activity
            )
        }

        assertEquals(authenticationStarted, true)
    }

    private fun clickSignInButton() {
        inputCorrectUsername()
        onView(withId(R.id.signInButton)).perform(click())
    }

    @After
    fun cleanUp() {
        release()
    }

    private fun inputCorrectUsername(){
        onView(withId(R.id.text_field_username))
            .perform(ViewActions.replaceText((correctUsername)))
    }
}
