package com.github.h3lp3rs.h3lp.helprequest.helpee

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_DIAL
import android.os.Bundle
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isFocusable
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.GrantPermissionRule
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.Databases.EMERGENCIES
import com.github.h3lp3rs.h3lp.model.database.Databases.PREFERENCES
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.model.dataclasses.FirstAidHowTo
import com.github.h3lp3rs.h3lp.model.dataclasses.Helper
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.firstaid.EXTRA_FIRST_AID
import com.github.h3lp3rs.h3lp.view.firstaid.GeneralFirstAidActivity
import com.github.h3lp3rs.h3lp.view.helprequest.helpee.AwaitHelpActivity
import com.github.h3lp3rs.h3lp.view.helprequest.helpee.EXTRA_CALLED_EMERGENCIES
import com.github.h3lp3rs.h3lp.view.helprequest.helpee.EXTRA_EMERGENCY_KEY
import com.github.h3lp3rs.h3lp.view.helprequest.helpee.EXTRA_NEEDED_MEDICATION
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AwaitHelpActivityTest : H3lpAppTest<AwaitHelpActivity>() {
    private val ctx: Context = getApplicationContext()

    private val helpId = 1

    @get:Rule
    var mRuntimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        mockEmptyLocation()

        userUid = USER_TEST_ID

        setDatabase(PREFERENCES, MockDatabase())
        setDatabase(EMERGENCIES, MockDatabase())
        resetStorage()

        loadValidMedicalDataToStorage(ctx)
    }

    fun launch(popup: Boolean): ActivityScenario<AwaitHelpActivity> {
        // Initialising the activity with default values
        val bundle = Bundle()
        bundle.putInt(EXTRA_EMERGENCY_KEY, helpId)
        bundle.putBoolean(EXTRA_CALLED_EMERGENCIES, !popup)
        bundle.putStringArrayList(EXTRA_NEEDED_MEDICATION, arrayListOf(EPIPEN))

        val intent = Intent(
            getApplicationContext(),
            AwaitHelpActivity::class.java
        ).apply {
            putExtras(bundle)
        }
        return launch(intent)
    }

    fun launchAndDo(popup: Boolean, action: () -> Unit) {
        launch(popup).use {
            init()
            action()
            release()
        }
    }

    @Test
    fun callEmergenciesButtonWorksAndSendsIntent() {
        launchAndDo(false) {

            val phoneButton = onView(withId(R.id.await_help_call_button))

            phoneButton.inRoot(isFocusable()).perform(click())

            // Click the ambulance in the popup
            onView(withId(R.id.ambulance_call_button)).inRoot(isFocusable())
                .perform(click())

            intended(
                allOf(
                    hasAction(ACTION_DIAL)
                )
            )
        }
    }

    @Test
    fun showsPopUpIfNotCalledBeforeAndCanCallEmergencies() {
        launchAndDo(true) {
            val phonePopupButton = onView(withId(R.id.open_call_popup_button))
            phonePopupButton.inRoot(isFocusable()).perform(click())

            intended(
                allOf(
                    hasAction(ACTION_DIAL)
                )
            )
        }
    }


    @Test
    fun clickAllergyExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntentWithExtra(
            GeneralFirstAidActivity::class.java,
            withId(R.id.epipen_tuto_button),
            EXTRA_FIRST_AID,
            FirstAidHowTo.ALLERGY
        )
    }

    @Test
    fun clickHeartAttackExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntentWithExtra(
            GeneralFirstAidActivity::class.java,
            withId(R.id.heart_attack_tuto_button),
            EXTRA_FIRST_AID,
            FirstAidHowTo.HEART_ATTACK
        )
    }

    @Test
    fun clickAedExpandButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntentWithExtra(
            GeneralFirstAidActivity::class.java,
            withId(R.id.aed_tuto_button),
            EXTRA_FIRST_AID,
            FirstAidHowTo.AED
        )
    }

    @Test
    fun cancelButtonWorksAndSendsIntent() {
        clickingOnButtonWorksAndSendsIntent(
            MainPageActivity::class.java,
            withId(R.id.cancel_search_button)
        )
    }

    @Test
    fun samePersonComingToHelpTwiceNotifiesOnceOnly() {
        val emergency = EPIPEN_EMERGENCY_INFO
        val emergencyDb = databaseOf(EMERGENCIES, ctx)

        // Setup the database accordingly
        emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, emergency)

        // Simulate arrival on await page after calling for help
        launchAndDo(false) {
            // One person is coming
            val helper1 = Helper(USER_TEST_ID + 1, 2.0, 2.0)
            val withHelpers = emergency.copy(helpers = ArrayList(listOf(helper1)))

            emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, withHelpers)

            // The same person is coming again, should NOT add a helper to the list
            emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, withHelpers)
            onView(withId(R.id.incomingHelpersNumber)).check(
                matches(
                    withText(
                        ctx.resources.getQuantityString(R.plurals.number_of_helpers, 1, 1)
                    )
                )
            )

        }
    }

    @Test
    fun getsNotifiedWhenHelpIsComing() {
        val emergency = EPIPEN_EMERGENCY_INFO
        val emergencyDb = databaseOf(EMERGENCIES, ctx)

        // Setup the database accordingly
        emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, emergency)

        // Simulate arrival on await page after calling for help
        launchAndDo(false) {
            // Nobody coming
            onView(withId(R.id.incomingHelpersNumber)).check(matches(withText("")))

            // One person is coming
            val helper1 = Helper(USER_TEST_ID + 1, 2.0, 2.0)
            val withHelpers = emergency.copy(helpers = ArrayList(listOf(helper1)))

            emergencyDb.setObject(helpId.toString(), EmergencyInformation::class.java, withHelpers)

            onView(withId(R.id.incomingHelpersNumber)).check(
                matches(
                    withText(
                        ctx.resources.getQuantityString(R.plurals.number_of_helpers, 1, 1)
                    )
                )
            )

            // A second person is coming
            val helper2 = Helper(USER_TEST_ID + 2, 2.1, 2.1)
            val withMoreHelpers = emergency.copy(helpers = ArrayList(listOf(helper1, helper2)))

            emergencyDb.setObject(
                helpId.toString(),
                EmergencyInformation::class.java,
                withMoreHelpers
            )
            onView(withId(R.id.incomingHelpersNumber)).check(
                matches(
                    withText(
                        String.format(
                            ctx.resources.getQuantityString(
                                R.plurals.number_of_helpers,
                                2,
                                2
                            )
                        )
                    )
                )
            )
        }
    }

    /**
     * Auxiliary function that clicks on a button and checks that a specific
     * activity is launched as a result.
     */
    private fun clickingOnButtonWorksAndSendsIntent(
        ActivityName: Class<*>?,
        id: Matcher<View>
    ) {
        launchAndDo(false) {
            onView(id).inRoot(isFocusable()).perform(click())

            intended(
                allOf(
                    hasComponent(ActivityName!!.name)
                )
            )
        }
    }


    private fun clickingOnButtonWorksAndSendsIntentWithExtra(
        ActivityName: Class<*>?,
        id: Matcher<View>,
        extraName: String,
        firstAidExtra: FirstAidHowTo
    ) {
        launchAndDo(false) {
            onView(id).perform(click())
            intended(
                allOf(
                    hasComponent(ActivityName!!.name),
                    IntentMatchers.hasExtra(extraName, firstAidExtra)
                )
            )
        }
    }
}