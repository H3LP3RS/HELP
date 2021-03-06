package com.github.h3lp3rs.h3lp.profile

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases.*
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.dataclasses.BloodType
import com.github.h3lp3rs.h3lp.model.dataclasses.Gender
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation.Companion.ADULT_AGE
import com.github.h3lp3rs.h3lp.view.profile.MedicalCardActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.model.storage.Storages
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.mainpage.GUIDE_KEY
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class MedicalCardActivityTest : H3lpAppTest<MedicalCardActivity>() {
    private val ctx: Context = getApplicationContext()

    override fun launch(): ActivityScenario<MedicalCardActivity> {
        return launch(Intent(getApplicationContext(), MedicalCardActivity::class.java))
    }

    @Before
    fun setup() {
        userUid = USER_TEST_ID

        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()
        storageOf(Storages.SIGN_IN, getApplicationContext()).setBoolean(GUIDE_KEY, true)
    }

    @Test
    fun oldYearNumberLeadsToError() {
        launchAndDo {
            onView(withId(R.id.medicalInfoBirthEditTxt)).perform(
                replaceText(
                    (ctx.resources.getInteger(
                        R.integer.minYear
                    ) - 1).toString()
                )
            )
            onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(hasInputLayoutError))
            onView(withId(R.id.medicalInfoBirthTxtLayout))
                .check(matches(hasTextInputLayoutError(ctx.resources.getString(R.string.yearTooOld))))
        }
    }

    @Test
    fun futureYearNumberLeadsToError() {
        launchAndDo {
            onView(withId(R.id.medicalInfoBirthEditTxt))
                .perform(replaceText((Calendar.getInstance().get(Calendar.YEAR) + 1).toString()))
            onView(withId(R.id.medicalInfoBirthTxtLayout)).check(
                matches(
                    hasInputLayoutError
                )
            )
            onView(withId(R.id.medicalInfoBirthTxtLayout)).check(
                matches(
                    hasTextInputLayoutError(ctx.resources.getString(R.string.yearTooRecent))
                )
            )
        }
    }

    @Test
    fun validYearNumberDoesNotLeadToError() {
        // Testing with the current year minus the age to be an adult to make the user eligible to
        // use the app
        launchAndDo {
            onView(withId(R.id.medicalInfoBirthEditTxt))
                .perform(
                    replaceText(
                        (Calendar.getInstance().get(Calendar.YEAR) - ADULT_AGE).toString()
                    )
                )
            onView(withId(R.id.medicalInfoBirthTxtLayout)).check(
                matches(
                    not(hasInputLayoutError)
                )
            )
        }
    }

    @Test
    fun validPhoneNumberDoesNotLeadToError() {
        launchAndDo {
            for (validNumber in VALID_FORMAT_NUMBERS) {
                onView(withId(R.id.medicalInfoContactNumberEditTxt))
                    .perform(scrollTo(), replaceText(validNumber))
                onView(withId(R.id.medicalInfoContactNumberTxtLayout)).check(
                    matches(
                        not(hasInputLayoutError)
                    )
                )
            }
        }
    }

    @Test
    fun emergencyNumberAsContactNumberLeadToError() {
        val emergencyNumber = "144"
        launchAndDo {
            onView(withId(R.id.medicalInfoContactNumberEditTxt))
                .perform(scrollTo(), replaceText(emergencyNumber))
            onView(withId(R.id.medicalInfoContactNumberTxtLayout)).check(
                matches(
                    hasInputLayoutError
                )
            )
        }
    }

    @Test
    fun incorrectContactNumberLeadToError() {
        val wrongNumbers = arrayOf("118 912", "pizza number", "my mum", "02145566991")
        launchAndDo {
            for (wrongNumber in wrongNumbers) {
                onView(withId(R.id.medicalInfoContactNumberEditTxt))
                    .perform(scrollTo(), replaceText(wrongNumber))
                onView(withId(R.id.medicalInfoContactNumberTxtLayout)).check(
                    matches(
                        hasInputLayoutError
                    )
                )
            }
        }
    }

    @Test
    fun tooHeavyWeightLeadToError() {
        launchAndDo {
            onView(withId(R.id.medicalInfoWeightEditTxt))
                .perform(replaceText((ctx.resources.getInteger(R.integer.maxWeight) + 1).toString()))
            onView(withId(R.id.medicalInfoWeightTxtLayout)).check(
                matches(
                    hasInputLayoutError
                )
            )
            onView(withId(R.id.medicalInfoWeightTxtLayout)).check(
                matches(
                    hasTextInputLayoutError(ctx.resources.getString(R.string.weightTooHeavy))
                )
            )
        }
    }

    @Test
    fun tooLightWeightLeadsToError() {
        launchAndDo {
            onView(withId(R.id.medicalInfoWeightEditTxt))
                .perform(replaceText((ctx.resources.getInteger(R.integer.minWeight) - 1).toString()))
            onView(withId(R.id.medicalInfoWeightTxtLayout)).check(
                matches(
                    hasInputLayoutError
                )
            )
            onView(withId(R.id.medicalInfoWeightTxtLayout)).check(
                matches(
                    hasTextInputLayoutError(ctx.resources.getString(R.string.weightTooLight))
                )
            )
        }
    }

    @Test
    fun appropriateWeightWeightDoesNotLeadToError() {
        launchAndDo {
            onView(withId(R.id.medicalInfoWeightEditTxt))
                .perform(replaceText((ctx.resources.getInteger(R.integer.maxWeight) - 1).toString()))
            onView(withId(R.id.medicalInfoWeightTxtLayout)).check(
                matches(
                    not(hasInputLayoutError)
                )
            )
        }
    }

    @Test
    fun tooBigHeightLeadToError() {
        launchAndDo {
            onView(withId(R.id.medicalInfoHeightEditTxt))
                .perform(replaceText((ctx.resources.getInteger(R.integer.maxHeight) + 1).toString()))
            onView(withId(R.id.medicalInfoHeightTxtLayout)).check(
                matches(
                    hasInputLayoutError
                )
            )
            onView(withId(R.id.medicalInfoHeightTxtLayout)).check(
                matches(
                    hasTextInputLayoutError(ctx.resources.getString(R.string.heightTooBig))
                )
            )
        }
    }

    @Test
    fun tooSmallHeightLeadToError() {
        launchAndDo {
            onView(withId(R.id.medicalInfoHeightEditTxt))
                .perform(replaceText((ctx.resources.getInteger(R.integer.minWeight) - 1).toString()))
            onView(withId(R.id.medicalInfoHeightTxtLayout)).check(
                matches(
                    hasInputLayoutError
                )
            )
            onView(withId(R.id.medicalInfoHeightTxtLayout)).check(
                matches(
                    hasTextInputLayoutError(ctx.resources.getString(R.string.heightTooShort))
                )
            )
        }
    }

    @Test
    fun appropriateHeightDoesNotLeadToError() {
        launchAndDo {
            onView(withId(R.id.medicalInfoHeightEditTxt))
                .perform(replaceText((ctx.resources.getInteger(R.integer.maxHeight) - 1).toString()))
            onView(withId(R.id.medicalInfoHeightTxtLayout)).check(
                matches(
                    not(hasInputLayoutError)
                )
            )
        }
    }

    private fun fillCorrectInfo() {
        onView(withId(R.id.medicalInfoHeightEditTxt))
            .perform(replaceText((ctx.resources.getInteger(R.integer.maxHeight) - 1).toString()))
        onView(withId(R.id.medicalInfoWeightEditTxt))
            .perform(replaceText((ctx.resources.getInteger(R.integer.maxWeight) - 1).toString()))
        onView(withId(R.id.medicalInfoBirthEditTxt))
            .perform(replaceText((ctx.resources.getInteger(R.integer.minYear) + 1).toString()))
        onView(withId(R.id.medicalInfoBloodDropdown))
            .perform(replaceText(BloodType.ABn.type))
        onView(withId(R.id.medicalInfoGenderDropdown))
            .perform(replaceText(Gender.Male.name))
        onView(withId(R.id.medicalInfoContactNumberEditTxt))
            .perform(scrollTo(), replaceText(VALID_CONTACT_NUMBER))
    }

    @Test
    fun savingChangeWithErrorShowsSnack() {
        launchAndDo {
            fillCorrectInfo()
            onView(withId(R.id.medicalInfoHeightEditTxt))
                .perform(
                    scrollTo(),
                    replaceText((ctx.resources.getInteger(R.integer.maxHeight) + 1).toString())
                )
            onView(withId(R.id.medicalInfoSaveButton))
                .perform(scrollTo(), click())
            onView(withText(R.string.invalid_field_msg))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
    }

    @Test
    fun savingChangeWithoutAcceptingPrivacyShowsSnack() {
        launchAndDo {
            fillCorrectInfo()
            onView(withId(R.id.medicalInfoSaveButton))
                .perform(scrollTo(), click())
            onView(withText(R.string.privacy_policy_not_accepted))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
    }

    @Test
    fun clickingOnPolicyDisplayDialogue() {
        launchAndDo {
            onView(withId(R.id.medicalInfoSaveButton))
            onView(withId(R.id.medicalInfoPrivacyCheck))
                .perform(scrollTo(), click())
            onView(withText(R.string.privacy_policy))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun savingChangeWithoutErrorAndTickingPolicyWorks() {
        launchAndDo {
            fillCorrectInfo()
            onView(withId(R.id.medicalInfoPrivacyCheck))
                .perform(scrollTo(), click())
            onView(withText(R.string.privacy_policy_not_accepted))
            onView(withText(R.string.privacy_policy))
                .inRoot(isDialog())
                .perform(pressBack())
            onView(withId(R.id.medicalInfoSaveButton))
                .perform(scrollTo(), click())
            onView(withText(R.string.changes_saved))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
    }

    @Test
    fun backButtonWorks() {
        launchAndDo {
            onView(withId(R.id.medicalInfoBackButton)).perform(click())
            intended(
                Matchers.allOf(
                    hasComponent(MainPageActivity::class.java.name),
                )
            )
        }
    }
}