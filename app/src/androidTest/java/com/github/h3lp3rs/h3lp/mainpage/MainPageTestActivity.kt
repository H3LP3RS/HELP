package com.github.h3lp3rs.h3lp.mainpage

import android.Manifest
import android.content.Context
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
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Database
import com.github.h3lp3rs.h3lp.model.database.Databases.*
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.model.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.view.firstaid.FirstAidActivity
import com.github.h3lp3rs.h3lp.view.forum.ForumCategoriesActivity
import com.github.h3lp3rs.h3lp.view.map.NearbyUtilitiesActivity
import com.github.h3lp3rs.h3lp.view.signin.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.view.professional.ProMainActivity
import com.github.h3lp3rs.h3lp.model.professional.ProUser
import com.github.h3lp3rs.h3lp.view.professional.VerificationActivity
import com.github.h3lp3rs.h3lp.view.profile.MedicalCardActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.model.storage.Storages.*
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.helprequest.helpee.HelpeeSelectionActivity
import com.github.h3lp3rs.h3lp.view.mainpage.CprRateActivity
import com.github.h3lp3rs.h3lp.view.mainpage.GUIDE_KEY
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainPageTestActivity : H3lpAppTest<MainPageActivity>() {
    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)
    private lateinit var proUsersDb: Database

    @Before
    fun setup() {
        userUid = USER_TEST_ID

        setDatabase(PREFERENCES, MockDatabase())
        setDatabase(PRO_USERS, MockDatabase())

        proUsersDb = databaseOf(PRO_USERS, getApplicationContext())

        resetStorage()
        storageOf(USER_COOKIE, getApplicationContext()).setBoolean(GUIDE_KEY, true)

        mockLocationToCoordinates(SWISS_LONG, SWISS_LAT)
    }

    override fun launch(): ActivityScenario<MainPageActivity> {
        return launch(Intent(getApplicationContext(), MainPageActivity::class.java))
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
    fun pushingHelpButtonWithoutSignInShowsPopUp() {
        // Not signed in:
        userUid = null
        USER_COOKIE.setOnlineSync(false, getApplicationContext())

        launchAndDo {
            onView(withId(R.id.HELP_button)).perform(click())

            // We can close the popup => It's displayed :)
            onView(withId(R.id.close_popup_button)).perform(click())
        }
    }

    @Test
    fun pushingInfoButtonLaunchesPresentation() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(
                PresArrivalActivity::class.java,
                withId(R.id.button_tutorial),
                false
            )
        }
    }

    @Test
    fun clickingOnCPRButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(
                CprRateActivity::class.java,
                withId(R.id.button_cpr),
                true
            )
        }
    }

    @Test
    fun clickingOnForumButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(
                ForumCategoriesActivity::class.java,
                withId(R.id.button_forum),
                true
            )
        }
    }

    @Test
    fun clickingOnHelpButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(
                HelpeeSelectionActivity::class.java,
                withId(R.id.HELP_button),
                false
            )
        }
    }

    @Test
    fun clickingOnHospitalButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(
                NearbyUtilitiesActivity::class.java,
                withId(R.id.button_hospital),
                true
            )
        }
    }

    @Test
    fun clickingOnPharmacyButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(
                NearbyUtilitiesActivity::class.java,
                withId(R.id.button_pharmacy),
                true
            )
        }
    }

    @Test
    fun clickingOnDefibrillatorsButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(
                NearbyUtilitiesActivity::class.java,
                withId(R.id.button_defibrillator),
                true
            )
        }
    }

    @Test
    fun clickingOnFirstAidButtonWorksAndSendsIntent() {
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(
                FirstAidActivity::class.java,
                withId(R.id.button_first_aid),
                true
            )
        }
    }

//    @Test // For some magic reason these tests don't pass all the time on Cirrus :(
//    fun getsNotifiedWhenHelpNeededAndCloseEnough() {
//        launchEmergency(
//            {
//                // Mock close enough behaviour
//                When(locationManagerMock.distanceFrom(anyOrNull(), anyOrNull())).thenReturn(
//                    CompletableFuture.completedFuture(MAX_RESPONSE_DISTANCE)
//                )
//            }
//        ) {
//            val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//            // Should immediately receive a notification
//            uiDevice.wait(Until.hasObject(By.textStartsWith("H3LP")), 3000)
//            val notification =
//                uiDevice.findObject(By.text(globalContext.getString(R.string.emergency)))
//             assertNotNull(notification)
//             Get the notification box - CIRRUS DOESN'T LIKE THIS
//             val notification = uiDevice.findObject(By.text(globalContext.getString(R.string.emergency)))
//             notification.click()
//             notification.clear()
//             onView(withId(R.id.accept)).check(matches(isDisplayed()))
//        }
//    }

// Distance check was removed :/
//    @Test
//    fun notNotifiedWhenHelpNeededAndTooFarAway() {
//        launchEmergency(
//            {
//                // Mock too far away behaviour
//                When(locationManagerMock.distanceFrom(anyOrNull(), anyOrNull())).thenReturn(
//                    CompletableFuture.completedFuture(2 * MAX_RESPONSE_DISTANCE)
//                )
//            }
//        ) {
//            val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//            // Should immediately receive a notification
//            uiDevice.wait(Until.hasObject(By.textStartsWith("H3LP")), 3000)
//            val notification =
//                uiDevice.findObject(By.text(globalContext.getString(R.string.emergency)))
//            assertNull(notification)
//        }
//    }

    private fun launchEmergency(before: () -> Unit, check: () -> Unit) {
        before()
        // Mock an emergency
        val emergenciesDb = MockDatabase()
        emergenciesDb.setObject(TEST_EMERGENCY_ID, EmergencyInformation::class.java, EPIPEN_EMERGENCY_INFO)
        setDatabase(EMERGENCIES, emergenciesDb)

        val newEmergenciesDb = MockDatabase()
        newEmergenciesDb.setInt(getApplicationContext<Context>().getString(R.string.epipen), TEST_EMERGENCY_ID.toInt())

        setDatabase(NEW_EMERGENCIES, newEmergenciesDb)
        // Add to storage the skills
        storageOf(SKILLS, getApplicationContext()).setObject(
            getApplicationContext<Context>().getString(R.string.my_skills_key),
            HelperSkills::class.java, EPIPEN_SKILL)

        // To track notifications
        launchAndDo {
            check()
        }
    }

    @Test
    fun clickingOnProPortalButtonGoesToProPortalIfVerifiedUser() {
        val proUser = ProUser(USER_TEST_ID, "","","", "", "", "")
        proUsersDb.setObject(USER_TEST_ID, ProUser::class.java, proUser)
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(
                ProMainActivity::class.java,
                withId(R.id.button_pro),
                true
            )
        }
    }

    @Test
    fun clickingOnProPortalButtonGoesToVerificationIfNonVerifiedUser() {
        proUsersDb.delete(USER_TEST_ID)
        launchAndDo {
            clickingOnButtonWorksAndSendsIntent(
                VerificationActivity::class.java,
                withId(R.id.button_pro),
                true
            )
        }
    }
}