package com.github.h3lp3rs.h3lp


import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
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
    fun searchingForCprRateAndPressingEnterWorksAndSendsIntent() {
        lookUpAndSelectItem(CPR_RATE)
        checkActivityOnSuccess(CprRateActivity::class.java)
    }

    @Test
    fun searchingForProfileAndPressingEnterWorksAndSendsIntent() {
        lookUpAndSelectItem(PROFILE)
        checkActivityOnSuccess(MedicalCardActivity::class.java)
    }

    @Test
    fun searchingForTutorialAndPressingEnterWorksAndDisplaysTutorial() {
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