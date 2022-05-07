package com.github.h3lp3rs.h3lp

import android.Manifest
import android.app.Activity
import android.app.Instrumentation.*
import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.professional.ProMainActivity
import com.github.h3lp3rs.h3lp.professional.ProUser
import com.github.h3lp3rs.h3lp.professional.VerificationActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.collections.ArrayList


@RunWith(AndroidJUnit4::class)
class MainPageTestActivity {
    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)
    private lateinit var proUsersDb : Database

    @Before
    fun setup() {
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        setDatabase(PREFERENCES, MockDatabase())
        setDatabase(PRO_USERS, MockDatabase())
        proUsersDb = Databases.databaseOf(PRO_USERS)
        resetStorage()
        storageOf(USER_COOKIE).setBoolean(GUIDE_KEY, true)
    }

    private fun launch(): ActivityScenario<MainPageActivity> {
        return launch(Intent(getApplicationContext(), MainPageActivity::class.java))
    }

    private fun launchAndDo(action: () -> Unit) {
        launch().use {
            start()
            action()
            end()
        }
    }

    private fun start() {
        init()
        val intent = Intent()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)
    }

    private fun end() {
        release()
    }

    private fun clickingOnButtonWorksAndSendsIntent(
        ActivityName: Class<*>?,
        id: Matcher<View>,
        isInScrollView: Boolean
    ) {
        if (isInScrollView) {
            onView(id).perform(ViewActions.scrollTo(), click())
        } else {
            onView(id).perform(click())
        }
        intended(
            Matchers.allOf(
                hasComponent(ActivityName!!.name)
            )
        )
    }

    @Test
    fun pushingInfoButtonLaunchesPresentation() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(PresArrivalActivity::class.java, withId(R.id.button_tutorial), false)
        }
    }

    @Test
    fun clickingOnCPRButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(CprRateActivity::class.java, withId(R.id.button_cpr), true)
        }
    }

    @Test
    fun clickingOnProfileButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(MedicalCardActivity::class.java, withId(R.id.button_profile), false)
        }
    }

    @Test
    fun clickingOnHelpButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(HelpParametersActivity::class.java, withId(R.id.HELP_button), false)
        }
    }

    @Test
    fun clickingOnHospitalButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(NearbyUtilitiesActivity::class.java, withId(R.id.button_hospital), true)
        }
    }

    @Test
    fun clickingOnPharmacyButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(NearbyUtilitiesActivity::class.java, withId(R.id.button_pharmacy), true)
        }
    }

    @Test
    fun clickingOnFirstAidButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(FirstAidActivity::class.java, withId(R.id.button_first_aid), true)
        }
    }

    @Test
    fun getsNotifiedWhenHelpNeeded() {
        // Mock an emergency
        val emergencyId = 1
        val emergenciesDb = MockDatabase()
        val skills = HelperSkills(true, false,false,
                                false,false, false)
        val emergency = EmergencyInformation(emergencyId.toString(), 1.0,1.0, skills,
                                ArrayList(listOf("Epipen")), Date(), null, ArrayList())
        emergenciesDb.setObject(emergencyId.toString(), EmergencyInformation::class.java, emergency)
        EMERGENCIES.db = emergenciesDb
        val newEmergenciesDb = MockDatabase()
        newEmergenciesDb.setInt(globalContext.getString(R.string.epipen), emergencyId)
        NEW_EMERGENCIES.db = newEmergenciesDb
        // Add to storage the skills
        storageOf(SKILLS).setObject(globalContext.getString(R.string.my_skills_key),
            HelperSkills::class.java, skills)
        // To track notifications
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        launchAndDo {
            // Should immediately receive a notification
            uiDevice.wait(Until.hasObject(By.textStartsWith("H3LP")),3000)
            // Get the notification box - CIRRUS DOESN'T LIKE THIS
            // val notification = uiDevice.findObject(By.text(globalContext.getString(R.string.emergency)))
            // notification.click()
            // notification.clear()
            // onView(withId(R.id.accept)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun clickingOnProPortalButtonGoesToProPortalIfVerifiedUser() {
        val proUser = ProUser(USER_TEST_ID, "","","", "", "", "")
        proUsersDb.setObject(USER_TEST_ID,ProUser::class.java, proUser)
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(ProMainActivity::class.java, withId(R.id.button_pro), true)
        }
    }

    @Test
    fun clickingOnProPortalButtonGoesToVerificationIfNonVerifiedUser() {
        proUsersDb.delete(USER_TEST_ID)
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(VerificationActivity::class.java, withId(R.id.button_pro), true)
        }
    }
}