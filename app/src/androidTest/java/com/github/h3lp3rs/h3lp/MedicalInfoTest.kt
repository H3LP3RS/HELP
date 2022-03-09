package com.github.h3lp3rs.h3lp


import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
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


@RunWith(AndroidJUnit4::class)
class MedicalInfoTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        MedicalInfo::class.java
    )
    @Test
    fun oldYearNumberLeadToError() {
        onView(withId(R.id.medicalInfoBirthEditTxt))
            .perform(typeText("200"))
        onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(
            hasTextInputLayoutError("Invalid year : You seem too old to be alive")
        ))
    }
    @Test
    fun futureYearNumberLeadToError() {
        onView(withId(R.id.medicalInfoBirthEditTxt))
            .perform(typeText("2024"))
        onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(
            hasTextInputLayoutError("Invalid year : You can not be born in the future")
        ))
    }
    @Test
    fun validYearNumberDontLeadToError() {
        onView(withId(R.id.medicalInfoBirthEditTxt))
            .perform(typeText("2000"))
        onView(withId(R.id.medicalInfoBirthTxtLayout)).check(matches(
            not(hasInputLayoutError())
        ))
    }

    @Test
    fun tooHeavyWeightLeadToError() {
        onView(withId(R.id.medicalInfoWeightEditTxt))
            .perform(typeText("800"))
        onView(withId(R.id.medicalInfoWeightTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoWeightTxtLayout)).check(matches(
            hasTextInputLayoutError("Invalid weight : You seem too heavy")
        ))
    }
    @Test
    fun tooLightWeightLeadToError() {
        onView(withId(R.id.medicalInfoWeightEditTxt))
            .perform(typeText("10"))
        onView(withId(R.id.medicalInfoWeightTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoWeightTxtLayout)).check(matches(
            hasTextInputLayoutError("Invalid weight : You seem too light")
        ))
    }
    @Test
    fun appropriateWeightWeightDontLeadToError() {
        onView(withId(R.id.medicalInfoWeightEditTxt))
            .perform(typeText("80"))
        onView(withId(R.id.medicalInfoWeightTxtLayout)).check(matches(
            not(hasInputLayoutError())
        ))
    }
    @Test
    fun tooBigHeightLeadToError() {
        onView(withId(R.id.medicalInfoHeightEditTxt))
            .perform(typeText("300"))
        onView(withId(R.id.medicalInfoHeightTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoHeightTxtLayout)).check(matches(
            hasTextInputLayoutError("Invalid height : You seem too big")
        ))
    }
    @Test
    fun tooSmallHeightLeadToError() {
        onView(withId(R.id.medicalInfoHeightEditTxt))
            .perform(typeText("30"))
        onView(withId(R.id.medicalInfoHeightTxtLayout)).check(matches(
            hasInputLayoutError()
        ))
        onView(withId(R.id.medicalInfoHeightTxtLayout)).check(matches(
            hasTextInputLayoutError("Invalid height : You seem too short")
        ))
    }
    @Test
    fun appropriateHeightDontLeadToError() {
        onView(withId(R.id.medicalInfoHeightEditTxt))
            .perform(typeText("180"))
        onView(withId(R.id.medicalInfoHeightTxtLayout)).check(matches(
            not(hasInputLayoutError())
        ))
    }

    @Test
    fun backButtonWork(){

        Intents.init()
        val intent = Intent()
        Instrumentation.ActivityResult(Activity.RESULT_OK, intent)
        onView(withId(R.id.medicalInfoBackButton)).perform(click())
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(MainActivity::class.java.name),
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

