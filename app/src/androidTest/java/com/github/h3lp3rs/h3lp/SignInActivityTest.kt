package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.signIn.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as When

@RunWith(AndroidJUnit4::class)
class SignInActivityTest {
    private val serverClientId = "899579782202-t3orsbp6aov3i91c99r72kc854og8jad.apps.googleusercontent.com"

    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )

    @Test
    fun loginButtonLaunchesIntent() {
        Intents.init()
        onView(withId(R.id.signInButton)).check(matches(isDisplayed())).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasPackage("com.google.android.gms"))
        Intents.release()
    }

    @Test
    fun connectWithNonExistentAccount(){
        if(GoogleSignIn.getLastSignedInAccount(ApplicationProvider.getApplicationContext()) != null){
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverClientId)
                .requestEmail()
                .build()

            GoogleSignIn.getClient(ApplicationProvider.getApplicationContext(), gso).signOut()

        }
        Intents.init()
        val intent = Intent()
        val result: Instrumentation.ActivityResult = Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(result)
        onView(withId(R.id.signInButton)).perform(click())
        Intents.release()
    }

    @Test
    fun test(){
        val context : Context =  ApplicationProvider.getApplicationContext()
        val intent : Intent= Intent(context, SignInActivity::class.java)

        ActivityScenario.launch<SignInActivity>(intent).use { scenario ->

            val mockFirebaseAuth = mock(FirebaseAuth::class.java)
            val mockAuthResult = mock(AuthResult::class.java)
            When(mockFirebaseAuth.currentUser).thenReturn(null)
            Intents.init()
            scenario.onActivity { a ->
                a.firebaseAuthWithGoogle("")
            }
            Intents.release()
        }

    }


}