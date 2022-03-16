package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.signIn.SignIn
import com.github.h3lp3rs.h3lp.signIn.SignInActivity
import com.github.h3lp3rs.h3lp.signIn.SignInInterface
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class SignInActivityNewUserTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )

    @Before
    fun setUp(){
        Intents.init()

        val signInMock = Mockito.mock(SignInInterface::class.java)
        Mockito.`when`(signInMock.isSignedIn()).thenReturn(false)
        val intent  = Intent(ApplicationProvider.getApplicationContext(), MainPageActivity::class.java)
        val activityResult = ActivityResult(Activity.RESULT_OK, intent)
        val taskMock = Mockito.mock(Task::class.java)
        Mockito.`when`(taskMock.isSuccessful).thenReturn(true)

        testRule.scenario.onActivity { activity ->
            Mockito.`when`(signInMock.signIn(activity)).thenReturn(intent)
            Mockito.`when`(signInMock.authenticate(activityResult, activity)).thenReturn(taskMock)
        }

        SignIn.set(signInMock as SignInInterface<AuthResult>)
    }

    @Test
    fun newUserSignInCorrectly(){
        /*val resultData = Intent()
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        intending(anyIntent()).respondWith(result)
*/
        Espresso.onView(ViewMatchers.withId(R.id.signInButton)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MainPageActivity::class.java.name))
    }

    @After
    fun cleanUp(){
        Intents.release()
    }
}
