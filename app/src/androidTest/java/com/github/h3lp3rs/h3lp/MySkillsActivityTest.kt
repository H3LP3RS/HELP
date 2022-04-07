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
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MySkillsActivityTest {

    private fun launch(): ActivityScenario<MySkillsActivity> {
        return launch(Intent(getApplicationContext(), MySkillsActivity::class.java))
    }

    @Before
    fun dataInit() {
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        PREFERENCES.db = MockDatabase()
        resetStorage()
    }

    @Test
    fun backButtonWork() {
        launch().use {
            init()
            val intent = Intent()
            val intentResult = ActivityResult(Activity.RESULT_OK, intent)
            intending(anyIntent()).respondWith(intentResult)
            onView(withId(R.id.mySkillsBackButton)).perform(click())
            intended(allOf(hasComponent(MainPageActivity::class.java.name)))
            release()
        }
    }

    @Test
    fun clickingOnHelpDisplayDialogue() {
        launch().use {
            onView(withId(R.id.mySkillsHelpButton))
                .perform(click())
            onView(withText(R.string.my_helper_skills))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(isDisplayed()))
        }
    }
}