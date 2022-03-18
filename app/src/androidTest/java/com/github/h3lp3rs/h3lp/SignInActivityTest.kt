package com.github.h3lp3rs.h3lp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.preferences.Preferences
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.Files.*
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.USER_AGREE
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInActivityTest {
    @Before
    fun clearPreferences() {
        Preferences.clearAllPreferences(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun loginButtonLaunchesIntent() {
        Preferences(PRESENTATION, ApplicationProvider.getApplicationContext()).setBool(USER_AGREE, true)
        val intent = Intent(ApplicationProvider.getApplicationContext(), SignInActivity::class.java)
        ActivityScenario.launch<SignInActivity>(intent).use {
            init()
            onView(withId(R.id.signInButton)).check(matches(isDisplayed())).perform(ViewActions.click())
            intended(hasPackage("com.google.android.gms"))
            release()
        }
    }
}