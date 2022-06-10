package com.github.h3lp3rs.h3lp.signin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases.*
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.view.signin.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.model.storage.Storages.*
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.model.signin.SignIn
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity
import com.github.h3lp3rs.h3lp.model.signin.SignInInterface
import com.google.firebase.auth.AuthResult
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when` as When

@RunWith(AndroidJUnit4::class)
class SignedInUserTest : H3lpAppTest<SignInActivity>() {

    private fun setUp(tosAccepted: Boolean) {
        val intent = Intent(
            getApplicationContext(), SignInActivity::class.java
        )

        globalContext = getApplicationContext()
        userUid = USER_TEST_ID

        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()

        val signInMock = Mockito.mock(SignInInterface::class.java)
        When(signInMock.isSignedIn()).thenReturn(true)
        val userSignIn = storageOf(SIGN_IN)
        userSignIn.setBoolean(globalContext.getString(R.string.KEY_USER_SIGNED_IN), true)
        userSignIn.setString(globalContext.getString(R.string.KEY_USER_UID), USER_TEST_ID)
        userSignIn.setString(globalContext.getString(R.string.KEY_USER_NAME), USER_TEST_NAME)

        storageOf(USER_COOKIE).setBoolean(
            globalContext.getString(R.string.KEY_USER_AGREE),
            tosAccepted
        )

        SignIn.set(signInMock as SignInInterface<AuthResult>)
        init()
        ActivityScenario.launch<SignInActivity>(intent)
    }

    @Test
    fun signedInUserMovesToMainPageIfToSAccepted() {
        setUp(true)
        intended(hasComponent(MainPageActivity::class.java.name))
    }

    @Test
    fun signedInUserMovesToPresentationIfToSNotAccepted() {
        setUp(false)
        intended(hasComponent(PresArrivalActivity::class.java.name))
    }

    @Test
    fun guestUserMovesToPresentation() {
        setUp(false)
        userUid = null

        intended(hasComponent(PresArrivalActivity::class.java.name))
    }

    @After
    fun cleanUp() {
        release()
    }
}
