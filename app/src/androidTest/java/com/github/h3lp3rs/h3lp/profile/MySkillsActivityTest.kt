package com.github.h3lp3rs.h3lp.profile

import android.content.Context
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
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases.*
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.model.storage.Storages
import com.github.h3lp3rs.h3lp.view.profile.MySkillsActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MySkillsActivityTest : H3lpAppTest<MySkillsActivity>() {
    private val ctx: Context = getApplicationContext()

    override fun launch(): ActivityScenario<MySkillsActivity> {
        return launch(Intent(getApplicationContext(), MySkillsActivity::class.java))
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

            //notification hide the button so just make sure that the notification is gone to click on it
            Thread.sleep(2000)

            onView(withId(R.id.mySkillsBackButton)).perform(click())

            intended(allOf(hasComponent(MainPageActivity::class.java.name)))

            release()
        }
    }


    @Test
    fun loadDataWorks(){
        val skills = HelperSkills(true,true,false,false,false,false)
        val storage = storageOf(Storages.SKILLS, ctx)

        storage.setObject(ctx.getString(R.string.my_skills_key), HelperSkills::class.java, skills)
        storage.push()
        launch().use{
            onView(withId(R.id.epipenSwitch)).check(ViewAssertions.matches(isChecked()))
            onView(withId(R.id.ventolinSwitch)).check(ViewAssertions.matches(isChecked()))
            onView(withId(R.id.insulinSwitch)).check(ViewAssertions.matches(isNotChecked()))
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