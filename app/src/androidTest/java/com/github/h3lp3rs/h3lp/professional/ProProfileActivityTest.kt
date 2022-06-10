package com.github.h3lp3rs.h3lp.professional

import androidx.test.core.app.ApplicationProvider.getApplicationContext
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
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.Databases.PRO_USERS
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.professional.ProUser
import com.github.h3lp3rs.h3lp.view.professional.ProProfileActivity
import com.github.h3lp3rs.h3lp.view.professional.VerificationActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val STATUS_TEST = "doctor"
private const val DOMAIN_TEST = "humans"
private const val EXPERIENCE_TEST = "3"

private val proUser = ProUser(USER_TEST_ID, "","","", "", "", "")

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
        setDatabase(PRO_USERS, MockDatabase())
        databaseOf(PRO_USERS, getApplicationContext()).setObject(
            USER_TEST_ID,
            ProUser::class.java,
            proUser
        )
    }

    @After
    fun clean() {
        Intents.release()
    }

    @Test
    fun clickingOnPolicyDisplaysDialogue() {
        onView(withId(R.id.proProfilePrivacyCheck))
            .perform(scrollTo(), click())
        onView(withText(R.string.pro_profile_privacy_policy))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun updateProfileWithCheckedPolicyWorks() {
        onView(withId(R.id.proProfileStatusEditTxt))
            .perform(ViewActions.replaceText(STATUS_TEST))
        onView(withId(R.id.proProfileDomainEditTxt))
            .perform(ViewActions.replaceText(DOMAIN_TEST))
        onView(withId(R.id.proProfileExperienceEditTxt))
            .perform(ViewActions.replaceText(EXPERIENCE_TEST))

        onView(withId(R.id.proProfilePrivacyCheck))
            .perform(click())

        onView(withText(R.string.pro_profile_privacy_policy))
            .inRoot(RootMatchers.isDialog())
            .perform(ViewActions.pressBack())

        onView(withId(R.id.proProfileUpdateButton))
            .perform(click())

        val currentProUser = databaseOf(PRO_USERS, getApplicationContext()).getObject(
            USER_TEST_ID,
            ProUser::class.java
        ).get()

        assertEquals(currentProUser.id, USER_TEST_ID)
        assertEquals(currentProUser.name, "")
        assertEquals(currentProUser.proofName, "")
        assertEquals(currentProUser.proofUri, "")
        assertEquals(currentProUser.proStatus, STATUS_TEST)
        assertEquals(currentProUser.proDomain, DOMAIN_TEST)
        assertEquals(currentProUser.proExperience, EXPERIENCE_TEST)

    }

    @Test
    fun updateProfileWithoutCheckedPolicyDisplaysError() {
        onView(withId(R.id.proProfileUpdateButton))
            .perform(click())

        onView(withText(R.string.privacy_policy_not_accepted))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }


}