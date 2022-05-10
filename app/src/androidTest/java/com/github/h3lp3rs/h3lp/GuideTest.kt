package com.github.h3lp3rs.h3lp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GuideTest : H3lpAppTest() {

    private fun launch(): ActivityScenario<MainPageActivity> {
        return launch(Intent(getApplicationContext(), MainPageActivity::class.java))
    }

    @Before
    fun setup() {
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()
    }

    @Test
    fun checkThatGuideIsLaunched() {
        launch().use { assertTrue(storageOf(USER_COOKIE).getBoolOrDefault(GUIDE_KEY, false)) }
    }

    @Test
    fun checkThatGuideIsInitiallyNotLaunched() {
        storageOf(USER_COOKIE).setBoolean(GUIDE_KEY, true)
        launch().use { assertTrue(storageOf(USER_COOKIE).getBoolOrDefault(GUIDE_KEY, false)) }
    }

    @Test
    fun finishingAppDemoDisplaysMessage() {
        // Both ways work, but mysteriously fail on Cirrus.
        /*
        clearPreferences()
        var i = 0
        // +1 for the search bar
        val nbButtons = numberOfButtons + 1
        while (i++ < nbButtons) {
            // This works completely fine, but fails on Cirrus :(
            // onView(withId(R.id.HelloText)).perform(click())
            // Also fails on Cirrus
            // val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            // uiDevice.findObject(UiSelector().textContains("Help")).click()
        }
        onView(ViewMatchers.withText(R.string.AppGuideFinished)).check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        )
        */
    }
}