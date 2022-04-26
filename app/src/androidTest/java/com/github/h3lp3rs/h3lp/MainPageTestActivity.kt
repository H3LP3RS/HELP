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
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainPageTestActivity {
    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()
        storageOf(Storages.USER_COOKIE).setBoolean(GUIDE_KEY, true)
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
}