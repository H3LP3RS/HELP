package com.github.h3lp3rs.h3lp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest : H3lpAppTest() {

    private fun launch(): ActivityScenario<SettingsActivity> {
        return launch(Intent(getApplicationContext(), SettingsActivity::class.java))
    }

    @Before
    fun dataInit() {
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()
    }

    @Test
    fun backButtonWorks() {
        launch().use {
            initIntentAndCheckResponse()
            onView(withId(R.id.settingsBackButton)).perform(click())
            intended(allOf(hasComponent(MainPageActivity::class.java.name)))
            release()
        }
    }

    @Test
    fun logoutButtonWorks() {
        launch().use {
            initIntentAndCheckResponse()
            onView(withId(R.id.logoutSettingsButton)).perform(click())
            intended(allOf(hasComponent(SignInActivity::class.java.name)))
            release()
        }
    }

    @Test
    fun removeSyncButtonWorks() {
        launch().use {
            val db = MockDatabase()
            val txt = "txt"
            db.setString("SKILLS/$USER_TEST_ID", txt)
            db.setString("USER_COOKIE/$USER_TEST_ID", txt)
            db.setString("MEDICAL_INFO/$USER_TEST_ID", txt)
            setDatabase(PREFERENCES, db)
            onView(withId(R.id.buttonRemoveSynch)).perform(click())

            assert(db.getString("SKILLS/$USER_TEST_ID").isCompletedExceptionally)
            assert(db.getString("USER_COOKIE/$USER_TEST_ID").isCompletedExceptionally)
            assert(db.getString("MEDICAL_INFO/$USER_TEST_ID").isCompletedExceptionally)

        }


    }

}