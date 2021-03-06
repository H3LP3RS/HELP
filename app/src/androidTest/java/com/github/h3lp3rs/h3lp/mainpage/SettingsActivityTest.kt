package com.github.h3lp3rs.h3lp.mainpage

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
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases.*
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.view.profile.SettingsActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import com.github.h3lp3rs.h3lp.view.mainpage.ReportActivity
import com.github.h3lp3rs.h3lp.view.mainpage.ReportActivity.Companion.bug
import com.github.h3lp3rs.h3lp.view.mainpage.ReportActivity.Companion.suggestion
import com.github.h3lp3rs.h3lp.view.profile.EXTRA_REPORT_CATEGORY
import com.github.h3lp3rs.h3lp.view.signin.presentation.PresArrivalActivity
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest : H3lpAppTest<SettingsActivity>() {

    override fun launch(): ActivityScenario<SettingsActivity> {
        return launch(Intent(getApplicationContext(), SettingsActivity::class.java))
    }

    @Before
    fun dataInit() {
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
    fun aboutUsButtonWorks() {
        launch().use {
            initIntentAndCheckResponse()
            onView(withId(R.id.buttonAboutHelp)).perform(scrollTo(), click())
            intended(allOf(hasComponent(PresArrivalActivity::class.java.name)))
            release()
        }
    }

    @Test
    fun bugButtonWorks() {
        launch().use {
            initIntentAndCheckResponse()
            onView(withId(R.id.buttonBugReport)).perform(scrollTo(), click())
            intended(
                allOf(
                    hasComponent(ReportActivity::class.java.name), hasExtra(
                        EXTRA_REPORT_CATEGORY, bug
                    )
                )
            )
            release()
        }
    }

    @Test
    fun suggestionButtonWorks() {
        launch().use {
            initIntentAndCheckResponse()
            onView(withId(R.id.buttonSuggestion)).perform(scrollTo(), click())
            intended(
                allOf(
                    hasComponent(ReportActivity::class.java.name), hasExtra(
                        EXTRA_REPORT_CATEGORY, suggestion
                    )
                )
            )
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

    @Test
    fun checkBoxWithoutSignInShowsPopUp() {
        // Not signed in
        userUid = null

        launch().use {
            initIntentAndCheckResponse()

            onView(withId(R.id.user_cookie_checkbox)).perform(click())

            // We can close the popup => It's displayed :)
            onView(withId(R.id.close_popup_button)).perform(click())

            release()
        }

    }

}