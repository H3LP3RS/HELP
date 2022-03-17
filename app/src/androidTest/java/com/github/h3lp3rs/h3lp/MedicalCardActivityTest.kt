package com.github.h3lp3rs.h3lp


import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


@RunWith(AndroidJUnit4::class)
class MedicalCardActivityTest {

    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @get:Rule
    val testRule = ActivityScenarioRule(
        MedicalCardActivity::class.java
    )
    @Test
    fun oldYearNumberLeadToError() {
        onView(withId(R.id.medicalInfoBirthEditTxt))
            .perform(replaceText( (ctx.resources.getInteger(R.integer.minYear) - 1).toString()))
        onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(
            hasTextInputLayoutError(ctx.resources.getString(R.string.yearTooOld))
        ))
    }
    @Test
    fun futureYearNumberLeadToError() {
        onView(withId(R.id.medicalInfoBirthEditTxt))
            .perform(replaceText((Calendar.getInstance().get(Calendar.YEAR)+1).toString()))
        onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(
            hasTextInputLayoutError(ctx.resources.getString(R.string.yearTooRecent))
        ))
    }
    @Test
    fun validYearNumberDontLeadToError() {
        onView(withId(R.id.medicalInfoBirthEditTxt))
            .perform(replaceText(Calendar.getInstance().get(Calendar.YEAR).toString()))
        onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(
            not(hasInputLayoutError())
        ))
    }

    @Test
    fun tooHeavyWeightLeadToError() {
        onView(withId(R.id.medicalInfoWeightEditTxt))
            .perform(replaceText((ctx.resources.getInteger(R.integer.maxWeight) + 1).toString()))
        onView(withId(R.id.medicalInfoWeightTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoWeightTxtLayout)).check(matches(
            hasTextInputLayoutError(ctx.resources.getString(R.string.weightTooHeavy))
        ))
    }
    @Test
    fun tooLightWeightLeadToError() {
        onView(withId(R.id.medicalInfoWeightEditTxt))
            .perform(replaceText((ctx.resources.getInteger(R.integer.minWeight) - 1).toString()))
        onView(withId(R.id.medicalInfoWeightTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoWeightTxtLayout)).check(matches(
            hasTextInputLayoutError(ctx.resources.getString(R.string.weightTooLight))
        ))
    }
    @Test
    fun appropriateWeightWeightDontLeadToError() {
        onView(withId(R.id.medicalInfoWeightEditTxt))
            .perform(replaceText((ctx.resources.getInteger(R.integer.maxWeight) - 1).toString()))
        onView(withId(R.id.medicalInfoWeightTxtLayout)).check(matches(
            not(hasInputLayoutError())
        ))
    }
    @Test
    fun tooBigHeightLeadToError() {
        onView(withId(R.id.medicalInfoHeightEditTxt))
            .perform(replaceText((ctx.resources.getInteger(R.integer.maxHeight) + 1).toString()))
        onView(withId(R.id.medicalInfoHeightTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoHeightTxtLayout)).check(matches(
            hasTextInputLayoutError(ctx.resources.getString(R.string.heightTooBig))
        ))
    }
    @Test
    fun tooSmallHeightLeadToError() {
        onView(withId(R.id.medicalInfoHeightEditTxt))
            .perform(replaceText((ctx.resources.getInteger(R.integer.minWeight) - 1).toString()))
        onView(withId(R.id.medicalInfoHeightTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoHeightTxtLayout)).check(matches(
            hasTextInputLayoutError(ctx.resources.getString(R.string.heightTooShort))
        ))
    }
    @Test
    fun appropriateHeightDontLeadToError() {
        onView(withId(R.id.medicalInfoHeightEditTxt))
            .perform(replaceText((ctx.resources.getInteger(R.integer.maxHeight) - 1).toString()))
        onView(withId(R.id.medicalInfoHeightTxtLayout)).check(matches(
            not(hasInputLayoutError())
        ))
    }
    private fun fillCorrectInfo(){
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
    }



    @Test
    fun savingChangeWithErrorshowSnack() {
        fillCorrectInfo()
        onView(withId(R.id.medicalInfoHeightEditTxt))
            .perform(replaceText((ctx.resources.getInteger(R.integer.maxHeight) + 1).toString()))

        onView(withId(R.id.medicalInfoSaveButton))
            .perform(scrollTo(), click())

       onView(withText(R.string.invalid_field_msg))
           .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }
    @Test
    fun savingChangeWithoutAccetptingPrivacyshowSnack() {
        fillCorrectInfo()

        onView(withId(R.id.medicalInfoSaveButton))
            .perform(scrollTo(), click())



        onView(withText(R.string.privacy_policy_not_acceptes))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }

    @Test
    fun clickingOnPolicyDisplayDialogue() {

        onView(withId(R.id.medicalInfoPrivacyCheck))
            .perform(scrollTo(), click())

        onView(withText(R.string.privacy_policy))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun savingChangeWitouErrorAndTickingPolicyWork() {
        fillCorrectInfo()

        onView(withId(R.id.medicalInfoPrivacyCheck))
            .perform(scrollTo(), click())

        onView(withText(R.string.privacy_policy))
            .inRoot(isDialog())
            .perform(pressBack())

        onView(withId(R.id.medicalInfoSaveButton))
            .perform(scrollTo(), click())

        onView(withText(R.string.changes_saved))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))


    }


    @Test
    fun backButtonWork(){

        Intents.init()
        val intent = Intent()
        Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        onView(withId(R.id.medicalInfoBackButton)).perform(click())
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(MainPageActivity::class.java.name),
            )
        )
        Intents.release()
    }

    /**
     * Custom matcher to test error on TextInputLayout.
     * See : https://stackoverflow.com/questions/38842034/how-to-test-textinputlayout-values-hint-error-etc-using-android-espresso
     */
    private fun hasInputLayoutError(): Matcher<View> = object : TypeSafeMatcher<View>() {

        override fun describeTo(description: Description?) { }

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            item.error ?: return false
            return true
        }
    }

    /**
     * Custom matcher to test error message on TextInputLayout.
     * See : https://stackoverflow.com/questions/38842034/how-to-test-textinputlayout-values-hint-error-etc-using-android-espresso
     */
    private fun hasTextInputLayoutError(msg : String): Matcher<View> = object : TypeSafeMatcher<View>() {

        override fun describeTo(description: Description?) { }

        override fun matchesSafely(item: View?): Boolean {
            if (item !is TextInputLayout) return false
            val error = item.error ?: return false
            return error.toString()==msg
        }
    }
}

