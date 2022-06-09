package com.github.h3lp3rs.h3lp.mainpage


import android.content.Intent
import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases.*
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.view.map.NearbyUtilitiesActivity
import com.github.h3lp3rs.h3lp.view.signin.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.view.profile.MedicalCardActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.model.storage.Storages.*
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.mainpage.*
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val NONEXISTENT_ITEM = "Nonexistent item"

@RunWith(AndroidJUnit4::class)
class SearchBarTest : H3lpAppTest<MainPageActivity>() {

    override fun launch(): ActivityScenario<MainPageActivity> {
        return launch(Intent(getApplicationContext(), MainPageActivity::class.java))
    }

    @Before
    fun setup() {
        userUid = USER_TEST_ID
        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()
        storageOf(USER_COOKIE, getApplicationContext()).setBoolean(GUIDE_KEY, true)
    }

    @Test
    fun searchingForCprRateAndClickingOnFistSuggestionLaunchesCorrectActivity() {
        launchAndDo {
            lookUpAndSelectItem(CPR_RATE)
            checkActivityOnSuccess(CprRateActivity::class.java)
        }
    }

    @Test
    fun searchingForProfileAndClickingOnFistSuggestionLaunchesCorrectActivity() {
        launchAndDo {
            lookUpAndSelectItem(PROFILE)
            checkActivityOnSuccess(MedicalCardActivity::class.java)
        }
    }

    @Test
    fun searchingForPharmaciesAndClickingOnFistSuggestionLaunchesCorrectActivity() {
        launchAndDo {
            lookUpAndSelectItem(PHARMACIES)
            checkActivityOnSuccess(NearbyUtilitiesActivity::class.java)
        }
    }

    @Test
    fun searchingForHospitalsAndClickingOnFistSuggestionLaunchesCorrectActivity() {
        launchAndDo {
            lookUpAndSelectItem(HOSPITALS)
            checkActivityOnSuccess(NearbyUtilitiesActivity::class.java)
        }
    }

    @Test
    fun searchingForTutorialAndClickingOnFistSuggestionLaunchesCorrectActivity() {
        launchAndDo {
            lookUpAndSelectItem(TUTORIAL)
            checkActivityOnSuccess(PresArrivalActivity::class.java)
        }
    }

    private fun checkActivityOnSuccess(ActivityName: Class<*>?) {
        intended(allOf(hasComponent(ActivityName!!.name)))
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
        launchAndDo {
            lookUp(NONEXISTENT_ITEM)
            allOf(hasComponent(MainPageActivity::class.java.name))
        }
    }

    @Test
    fun listViewIsNotDisplayedWhenTextIsEmpty() {
        launchAndDo {
            onView(withId(R.id.searchBar)).perform(click())
            onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText(PROFILE))
                .perform(clearText())
            onView(withId(R.id.listView)).check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun listViewIsNotDisplayedWhenItemIsNonexistent() {
        launchAndDo {
            onView(withId(R.id.searchBar)).perform(click())
            onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText(NONEXISTENT_ITEM))
            onView(withId(R.id.listView)).check(matches(not(isDisplayed())))
        }
    }


    @Test
    fun searchingForNonexistentItemShowsErrorMessage() {
        launchAndDo {
            lookUp(NONEXISTENT_ITEM)
            onView(withText(R.string.match_not_found))
                .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
    }

    private fun searchingForCorrectItemShowsSelectedItemMessage(listItem: String) {
        lookUpAndSelectItem(listItem)
        onView(withText("Selected item : $listItem"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun searchingForProfileDisplaysCorrectMessage() {
        launchAndDo {
            searchingForCorrectItemShowsSelectedItemMessage(PROFILE)
        }
    }

    @Test
    fun searchingForPharmaciesDisplaysCorrectMessage() {
        launchAndDo {
            searchingForCorrectItemShowsSelectedItemMessage(PHARMACIES)
        }
    }

    @Test
    fun searchingForHospitalsDisplaysCorrectMessage() {
        launchAndDo {
            searchingForCorrectItemShowsSelectedItemMessage(HOSPITALS)
        }
    }

    @Test
    fun searchingForTutorialDisplaysCorrectMessage() {
        launchAndDo {
            searchingForCorrectItemShowsSelectedItemMessage(TUTORIAL)
        }
    }

    @Test
    fun searchingForCprRateDisplaysCorrectMessage() {
        launchAndDo {
            searchingForCorrectItemShowsSelectedItemMessage(CPR_RATE)
        }
    }

    @Test
    fun listViewIsInitiallyHidden() {
        launchAndDo {
            onView(withId(R.id.listView)).check(matches(not(isDisplayed())))
        }
    }
}