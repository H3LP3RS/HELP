package com.github.h3lp3rs.h3lp.signin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.Databases.PREFERENCES
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.storage.Storages.SIGN_IN
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When


@RunWith(AndroidJUnit4::class)

class AnonymousSignInTest : H3lpAppTest() {

    @Before
    fun setUp() {

        globalContext = getApplicationContext()
        userUid = USER_TEST_ID

        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()
        storageOf(Storages.USER_COOKIE).setBoolean(
            globalContext.getString(R.string.KEY_USER_AGREE), false
        )
        val signInMock = mock(SignInInterface::class.java)
        When(signInMock.isSignedIn()).thenReturn(false)

        val userSignIn = storageOf(SIGN_IN)
        userSignIn.setBoolean(globalContext.getString(R.string.KEY_USER_SIGNED_IN), false)
        SignIn.set(signInMock as SignInInterface<AuthResult>)

        val taskMock = mock(Task::class.java)
        When(taskMock.isSuccessful).thenReturn(true)
        When(taskMock.isComplete).thenReturn(true)
        When(signInMock.getUid()).thenReturn(USER_TEST_ID)
        When(signInMock.authenticate(anyOrNull(), anyOrNull())).thenAnswer {
            taskMock
        }
    }

    private fun launchAndDo(action : () -> Unit) {
        launch().use {
            initIntentAndCheckResponse()
            action()
            end()
        }
    }

    private fun end() {
        release()
    }

    private fun launch() : ActivityScenario<SignInActivity> {
        return ActivityScenario.launch(
            Intent(
                getApplicationContext(), SignInActivity::class.java
            )
        )
    }

    @Test
    fun emptyUsernameLeadsToError() {
        launchAndDo {
            onView(withId(R.id.textview_anonymous_sign_in)).perform(click())
            onView(withText(R.string.username_error_field_msg)).check(
                matches(
                    withEffectiveVisibility(Visibility.VISIBLE)
                )
            )
        }
    }

    @Test
    fun signInAnonymouslyLaunchesTOS() {
        launchAndDo {
            onView(withId(R.id.text_field_username)).perform(replaceText((USER_TEST_NAME)))
            onView(withId(R.id.textview_anonymous_sign_in)).perform(click())

            intended(
                allOf(
                    hasComponent(PresArrivalActivity::class.java.name)
                )
            )
        }
    }

}






