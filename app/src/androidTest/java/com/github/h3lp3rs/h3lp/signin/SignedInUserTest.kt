package com.github.h3lp3rs.h3lp.signin

import android.content.Intent
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.USER_TEST_ID
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When

@RunWith(AndroidJUnit4::class)
class SignedInUserTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )

    @Before
    fun setUp() {
        init()
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        PREFERENCES.db = MockDatabase()
        resetStorage()

        val signInMock = Mockito.mock(SignInInterface::class.java)
        When(signInMock.isSignedIn()).thenReturn(true)

        testRule.scenario.onActivity { activity ->
            val intent = Intent(getApplicationContext(), activity.javaClass)
            val taskMock = Mockito.mock(Task::class.java)
            When(taskMock.isSuccessful).thenReturn(true)
            When(taskMock.isComplete).thenReturn(true)
            When(signInMock.signIn(activity)).thenReturn(intent)
            When(signInMock.authenticate(anyOrNull(), anyOrNull())).thenReturn(taskMock)
        }

        SignIn.set(signInMock as SignInInterface<AuthResult>)
    }

    @Test // TODO: Need authentication mocking
    fun signedInUserMovesToMainPageIfToSAccepted() {
        storageOf(USER_COOKIE).setBoolean(globalContext.getString(R.string.KEY_USER_AGREE), true)
        onView(withId(R.id.signInButton)).perform(click())
        intended(hasComponent(MainPageActivity::class.java.name))
    }

    @Test
    fun signedInUserMovesToPresentationIfToSNotAccepted() {
        storageOf(USER_COOKIE).setBoolean(globalContext.getString(R.string.KEY_USER_AGREE), false)
        onView(withId(R.id.signInButton)).perform(click())
        intended(hasComponent(PresArrivalActivity::class.java.name))
    }

    @After
    fun cleanUp() {
        release()
    }
}
