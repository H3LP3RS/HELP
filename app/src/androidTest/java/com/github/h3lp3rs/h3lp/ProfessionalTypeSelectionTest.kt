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
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.professional.ProMainActivity
import com.github.h3lp3rs.h3lp.professional.ProfessionalTypeSelection
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfessionalTypeSelectionTest {

    private fun launch(): ActivityScenario<MySkillsActivity> {
        return launch(Intent(getApplicationContext(), ProfessionalTypeSelection::class.java))
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
            init()
            val intent = Intent()
            val intentResult = ActivityResult(Activity.RESULT_OK, intent)
            intending(anyIntent()).respondWith(intentResult)
            onView(withId(R.id.myProTypeBackButton)).perform(click())
            intended(allOf(hasComponent(ProMainActivity::class.java.name)))
            release()
        }
    }

    @Test
    fun clickingOnHelpDisplayDialogue() {
        launch().use {
            onView(withId(R.id.ForumTypeHelp))
                .perform(click())
            onView(withText(R.string.forum_themes))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }
}