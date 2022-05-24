package com.github.h3lp3rs.h3lp.professional

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.H3lpAppTest
import com.github.h3lp3rs.h3lp.MySkillsActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.dataclasses.MedicalType
import com.github.h3lp3rs.h3lp.forum.ForumCategory
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.google.android.gms.auth.api.signin.internal.Storage
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfessionalTypeSelectionTest : H3lpAppTest() {
    val ctx: Context = getApplicationContext()

    private fun launch(): ActivityScenario<MySkillsActivity> {
        return launch(Intent(getApplicationContext(), ProfessionalTypeSelection::class.java))
    }

    @Before
    fun dataInit() {
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()
        val storage = Storages.storageOf(Storages.FORUM_THEMES_NOTIFICATIONS)
        val categoriesList = emptyList<ForumCategory?>() +
                ForumCategory.GENERAL + ForumCategory.GYNECOLOGY
        val theme = MedicalType(categoriesList.filterNotNull())

        storage.setObject(ctx.getString(R.string.forum_theme_key), MedicalType::class.java, theme)
        storage.push()
    }

    @Test
    fun loadDataWork(){
        launch().use{
            onView(withId(R.id.generalSwitch)).check(matches(isChecked()))
            onView(withId(R.id.gynecologySwitch)).check(matches(isChecked()))
            onView(withId(R.id.pediatrySwitch)).check(matches(isNotChecked()))
        }

    }

    @Test
    fun backButtonWorks() {
        launch().use {
            initIntentAndCheckResponse()
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