package com.github.h3lp3rs.h3lp.professional

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.USER_TEST_ID
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProProfileActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        ProProfileActivity::class.java
    )

    @Before
    fun setup() {
        Intents.init()
        VerificationActivity.currentUserId = USER_TEST_ID
        VerificationActivity.currentUserName = ""
        setDatabase(Databases.PRO_USERS,MockDatabase())
        val proUsersDb = databaseOf(Databases.PRO_USERS)
        val proUser = ProUser(USER_TEST_ID, "","","", "", "", 0)
        proUsersDb.setObject(USER_TEST_ID,ProUser::class.java, proUser)
    }

    @After
    fun clean() {
        Intents.release()
    }

    @Test
    fun clickingOnPolicyDisplaysDialogue() {
        onView(withId(R.id.ProProfilePrivacyCheck))
            .perform(scrollTo(), click())
        onView(withText(R.string.pro_profile_privacy_policy))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun updateProfileWorks(){
        onView(withId(R.id.proProfileStatusEditTxt))
            .perform(ViewActions.replaceText("doctor"))
        onView(withId(R.id.proProfileDomainEditTxt))
            .perform(ViewActions.replaceText("humans"))
        onView(withId(R.id.ProProfileExperienceEditTxt))
            .perform(ViewActions.replaceText("3"))

        onView(withId(R.id.ProProfileSaveButton))
            .perform(click())

        val currentProUser = databaseOf(Databases.PRO_USERS).getObject(USER_TEST_ID,ProUser::class.java).get()

        assertEquals(currentProUser.id, USER_TEST_ID)
        assertEquals(currentProUser.name, "")
        assertEquals(currentProUser.proofName, "")
        assertEquals(currentProUser.proofUri, "")
        assertEquals(currentProUser.proStatus, "doctor")
        assertEquals(currentProUser.proDomain, "humans")
    }


}