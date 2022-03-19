package com.github.h3lp3rs.h3lp

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import org.hamcrest.Matchers
import org.hamcrest.Matchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Rule


import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.not


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
        lookUp(CPR_RATE)
        checkActivityOnSuccess( CprRateActivity::class.java)
    }

    @Test
    fun searchingForProfileAndPressingEnterWorksAndSendsIntent() {
        lookUp(PROFILE)
        checkActivityOnSuccess( MedicalCardActivity::class.java)
    }

    @Test
    fun searchingForTutorialAndPressingEnterWorksAndDisplaysTutorial() {
        lookUp(TUTORIAL)
        checkActivityOnSuccess( PresArrivalActivity::class.java)
    }

    private fun checkActivityOnSuccess(ActivityName: Class<*>?){
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
        //The element should be at position 0 since the list view is filtered
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
    fun listViewIsNotDisplayedWhenTextIsEmpty(){
        onView(withId(R.id.searchBar)).perform(click())

        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText(PROFILE)).perform(
            clearText())
        onView(withId(R.id.listView)).check(matches(not(isDisplayed())))
    }

    @Test
    fun listViewIsNotDisplayedWhenItemIsNonexistent(){
        onView(withId(R.id.searchBar)).perform(click())

        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText(NONEXISTENT_ITEM))
        onView(withId(R.id.listView)).check(matches(not(isDisplayed())))
    }


    @Test
    fun searchingForNonexistentItemShowsMessageAndStaysOnActivity() {

        onView(withId(R.id.searchBar)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText(NONEXISTENT_ITEM))
            .perform(
                pressKey(KeyEvent.KEYCODE_ENTER)
            )
    }

}