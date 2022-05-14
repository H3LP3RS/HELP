package com.github.h3lp3rs.h3lp.signin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.forum.CATEGORY_TEST_STRING
import com.github.h3lp3rs.h3lp.forum.EXTRA_FORUM_CATEGORY
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.google.firebase.auth.AuthResult
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when` as When

@RunWith(AndroidJUnit4::class)
class SignedInUserTest : H3lpAppTest() {

    private fun setUp(tosAccepted: Boolean){
        val intent = Intent(
            getApplicationContext(), SignInActivity::class.java
        ).apply {
            putExtra(EXTRA_FORUM_CATEGORY, CATEGORY_TEST_STRING)
        }

        globalContext = getApplicationContext()
        userUid = USER_TEST_ID

        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()

        val signInMock = Mockito.mock(SignInInterface::class.java)
        When(signInMock.isSignedIn()).thenReturn(true)
        val userSignIn = storageOf(SIGN_IN)
        userSignIn.setBoolean(globalContext.getString(R.string.KEY_USER_SIGNED_IN), true)
        userSignIn.setString(globalContext.getString(R.string.KEY_USER_UID), "")
        userSignIn.setString(globalContext.getString(R.string.KEY_USER_NAME), "")

        storageOf(USER_COOKIE).setBoolean(globalContext.getString(R.string.KEY_USER_AGREE), tosAccepted)

        SignIn.set(signInMock as SignInInterface<AuthResult>)
        init()
        ActivityScenario.launch<SignInActivity>(intent)
    }

    @Test // TODO: Need authentication mocking
    fun signedInUserMovesToMainPageIfToSAccepted() {
        setUp(true)
        intended(hasComponent(MainPageActivity::class.java.name))
    }

    @Test
    fun signedInUserMovesToPresentationIfToSNotAccepted() {
        setUp(false)
        intended(hasComponent(PresArrivalActivity::class.java.name))
    }

    @After
    fun cleanUp() {
        release()
    }
}
