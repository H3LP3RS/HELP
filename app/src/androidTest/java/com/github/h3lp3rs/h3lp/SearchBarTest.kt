package com.github.h3lp3rs.h3lp


import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import junit.framework.TestCase
import org.hamcrest.Matchers
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val NONEXISTENT_ITEM = "Nonexistent item"

@RunWith(AndroidJUnit4::class)
class SearchBarTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        MainPageActivity::class.java
    )
    private val targetContext: Context = ApplicationProvider.getApplicationContext()


    private fun clearPreferences() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(targetContext)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }

    @Test
    fun checkThatGuideIsInitiallyNotLaunched() {
        clearPreferences()
        val prefManager = PreferenceManager.getDefaultSharedPreferences(targetContext)
        TestCase.assertFalse(prefManager.getBoolean("didShowGuide", false))
    }

    @Test
    fun checkThatGuideIsLaunched() {
        val prefManager = PreferenceManager.getDefaultSharedPreferences(targetContext)
        TestCase.assertTrue(prefManager.getBoolean("didShowGuide", false))
    }

    @Test
    fun finishingAppDemoDisplaysMessage() {
        clearPreferences()
        var i = 0
        // +1 for the search bar
        val nbButtons = mainPageButtons.size + scrollViewButtons.size + 1
        while (i < nbButtons) {
            onView(withId(R.id.HelloText)).perform(click())
            i++
        }
        onView(ViewMatchers.withText(R.string.AppGuideFinished))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }
    @Before
    fun setup() {
        init()
        val intent = Intent()
        val intentResult = Instrumentation.ActivityResult(RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)
    }

    @After
    fun release() {
        Intents.release()
    }

    @Test
    fun searchingForCprRateAndClickingOnFistSuggestionLaunchesCorrectActivity() {
        lookUpAndSelectItem(CPR_RATE)
        checkActivityOnSuccess(CprRateActivity::class.java)
    }

    @Test
    fun searchingForProfileAndClickingOnFistSuggestionLaunchesCorrectActivity() {
        lookUpAndSelectItem(PROFILE)
        checkActivityOnSuccess(MedicalCardActivity::class.java)
    }

    @Test
    fun searchingForPharmaciesAndClickingOnFistSuggestionLaunchesCorrectActivity() {
        lookUpAndSelectItem(PHARMACIES)
        checkActivityOnSuccess(NearbyUtilitiesActivity::class.java)
    }

    @Test
    fun searchingForHospitalsAndClickingOnFistSuggestionLaunchesCorrectActivity() {
        lookUpAndSelectItem(HOSPITALS)
        checkActivityOnSuccess(NearbyUtilitiesActivity::class.java)
    }

    @Test
    fun searchingForTutorialAndClickingOnFistSuggestionLaunchesCorrectActivity() {
        lookUpAndSelectItem(TUTORIAL)
        checkActivityOnSuccess(PresArrivalActivity::class.java)
    }

    private fun checkActivityOnSuccess(ActivityName: Class<*>?) {
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(ActivityName!!.name)
            )
        )
    }

    private fun lookUp(listItem: String) {
        onView(withId(R.id.searchBar)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText(listItem))
            .perform(pressKey(KeyEvent.KEYCODE_ENTER))

    }

    private fun lookUpAndSelectItem(listItem: String) {
        lookUp(listItem)
        onData(anything()).inAdapterView(withId(R.id.listView)).atPosition(0).perform(click())
    }


    @Test
    fun searchingForNonexistentItemStaysOnActivity() {
        lookUp(NONEXISTENT_ITEM)
        Matchers.allOf(
            IntentMatchers.hasComponent(MainPageActivity::class.java.name)
        )
    }

    @Test
    fun listViewIsNotDisplayedWhenTextIsEmpty() {
        onView(withId(R.id.searchBar)).perform(click())

        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText(PROFILE))
            .perform(
                clearText()
            )
        onView(withId(R.id.listView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun listViewIsNotDisplayedWhenItemIsNonexistent() {
        onView(withId(R.id.searchBar)).perform(click())

        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText(NONEXISTENT_ITEM))
        onView(withId(R.id.listView)).check(matches(not(isDisplayed())))
    }


    @Test
    fun searchingForNonexistentItemShowsErrorMessage() {

        lookUp(NONEXISTENT_ITEM)

        onView(withText(R.string.matchNotFound))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }

    private fun searchingForCorrectItemShowsSelectedItemMessage(listItem: String) {

        lookUpAndSelectItem(listItem)
        onView(withText("Selected item : $listItem"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

    }

    @Test
    fun searchingForProfileDisplaysCorrectMessage() {
        searchingForCorrectItemShowsSelectedItemMessage(PROFILE)

    }

    @Test
    fun searchingForPharmaciesDisplaysCorrectMessage() {
        searchingForCorrectItemShowsSelectedItemMessage(PHARMACIES)

    }

    @Test
    fun searchingForHospitalsDisplaysCorrectMessage() {
        searchingForCorrectItemShowsSelectedItemMessage(HOSPITALS)

    }

    @Test
    fun searchingForTutorialDisplaysCorrectMessage() {
        searchingForCorrectItemShowsSelectedItemMessage(TUTORIAL)

    }


    @Test
    fun searchingForCprRateDisplaysCorrectMessage() {
        searchingForCorrectItemShowsSelectedItemMessage(CPR_RATE)

    }

    @Test
    fun listViewIsInitiallyHidden(){
        onView(withId(R.id.listView)).check(matches(not(isDisplayed())))
    }


}