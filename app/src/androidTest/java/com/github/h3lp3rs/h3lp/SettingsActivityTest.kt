package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.app.Instrumentation.*
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.NullPointerException

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {


    private fun launch(): ActivityScenario<SettingsActivity> {
        return launch(Intent(getApplicationContext(), SettingsActivity::class.java))
    }

    @Before
    fun dataInit() {
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        PREFERENCES.db = MockDatabase()
        resetStorage()
    }

    @Test
    fun backButtonWorks() {
        launch().use {
            init()
            val intent = Intent()
            val intentResult = ActivityResult(Activity.RESULT_OK, intent)
            intending(anyIntent()).respondWith(intentResult)
            onView(withId(R.id.settingsBackButton)).perform(click())
            intended(allOf(hasComponent(MainPageActivity::class.java.name)))
            release()
        }
    }

    @Test
    fun logoutButtonWorks() {
        launch().use {
            init()
            val intent = Intent()
            val intentResult = ActivityResult(Activity.RESULT_OK, intent)
            intending(anyIntent()).respondWith(intentResult)
            onView(withId(R.id.logoutSettingsButton)).perform(click())
            intended(allOf(hasComponent(SignInActivity::class.java.name)))
            release()
        }
    }
    @Test
    fun removeSyncButtonWorks() {
       launch().use {
           val db = MockDatabase()
           db.setString("SKILLS/$USER_TEST_ID","hi")
           db.setString("USER_COOKIE/$USER_TEST_ID","hi")
           db.setString("MEDICAL_INFO/$USER_TEST_ID","hi")
           PREFERENCES.db=db
           onView(withId(R.id.buttonRemoveSynch)).perform(click())

            assert(db.getString("SKILLS/$USER_TEST_ID").isCompletedExceptionally)
           assert(db.getString("USER_COOKIE/$USER_TEST_ID").isCompletedExceptionally)
           assert(db.getString("MEDICAL_INFO/$USER_TEST_ID").isCompletedExceptionally)

       }


    }

}