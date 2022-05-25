package com.github.h3lp3rs.h3lp.presentation

import android.content.Intent
import android.view.InputDevice.*
import android.view.MotionEvent.*
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.*
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.*
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.Press.*
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf

@RunWith(AndroidJUnit4::class)
class PresIrrelevantActivityTest : H3lpAppTest() {

    private fun alreadyAccepted() {
        storageOf(USER_COOKIE, getApplicationContext()).setBoolean(globalContext.getString(R.string.KEY_USER_AGREE), true)
    }

    // https://stackoverflow.com/questions/42390788/espresso-click-on-specific-words-of-text
    // Since this is not a textView, espresso cannot click on text in it. So we manually find the
    // right coordinates of the highlighted text.
    private fun clickPercent(pctX: Float, pctY: Float): ViewAction {
        return GeneralClickAction(
            Tap.SINGLE,
            { view ->
                val screenPos = IntArray(2)
                view.getLocationOnScreen(screenPos)
                val w = view.width
                val h = view.height
                val x = w * pctX
                val y = h * pctY
                val screenX = screenPos[0] + x
                val screenY = screenPos[1] + y
                floatArrayOf(screenX, screenY)
            },
            FINGER, SOURCE_MOUSE, BUTTON_PRIMARY)
    }

    private fun checkIsDisplayed(id: Int) {
        onView(withId(id)).check(matches(isDisplayed()))
    }

    private fun launch(): ActivityScenario<PresIrrelevantActivity> {
        return launch(Intent(getApplicationContext(), PresIrrelevantActivity::class.java))
    }

    private fun checkLaunchOnApprovalClick(activity: String) {
        initIntentAndCheckResponse()
        onView(withId(R.id.pres3_button)).perform(click())
        intended(allOf(hasComponent(activity)))
        release()
    }

    @Before
    fun dataInit() {
        globalContext = getApplicationContext()
        userUid = USER_TEST_ID
        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()
    }

    @Test
    fun successfulDisplay() {
        launch().use {
            checkIsDisplayed(R.id.pres3_textView1)
            checkIsDisplayed(R.id.pres3_textView2)
            checkIsDisplayed(R.id.pres3_textView3)
            checkIsDisplayed(R.id.pres3_textView4)
            checkIsDisplayed(R.id.pres3_textView5)
            checkIsDisplayed(R.id.pres3_textView6)
            checkIsDisplayed(R.id.pres3_imageView1)
            checkIsDisplayed(R.id.pres3_imageView2)
            checkIsDisplayed(R.id.pres3_imageView3)
            checkIsDisplayed(R.id.pres3_checkBox)
        }
    }

    @Test
    fun boxDefaultNotChecked() {
        launch().use {
            onView(withId(R.id.pres3_checkBox)).check(matches(isNotChecked()))
        }
    }

    @Test
    fun boxCheckedIfDoneAlready() {
        alreadyAccepted()
        launch().use {
            onView(withId(R.id.pres3_checkBox)).check(matches(isChecked()))
        }
    }

    @Test
    fun unsuccessfulApprovalButtonNeverApproved() {
        launch().use {
            initIntentAndCheckResponse()
            onView(withId(R.id.pres3_button)).perform(click())
            assertThat(getIntents().size, `is`(0))
            release()
        }
    }

    @Test
    fun successfulApprovalButtonAlreadyApprovedFromMain() {
        alreadyAccepted()
        launch().use {
            checkLaunchOnApprovalClick(MainPageActivity::class.java.name)
        }
    }

    @Test
    fun successfulApprovalButtonAlreadyApprovedFromSignIn() {
        alreadyAccepted()
        launch().use {
            checkLaunchOnApprovalClick(MainPageActivity::class.java.name)
        }
    }

    @Test
    fun tickAndAcceptGoToSignIn() {
        launch().use {
            onView(withId(R.id.pres3_checkBox)).perform(click())
            checkLaunchOnApprovalClick(MainPageActivity::class.java.name)
        }
    }

    @Test
    fun tickAndAcceptGoToMain() {
        launch().use {
            onView(withId(R.id.pres3_checkBox)).perform(click())
            checkLaunchOnApprovalClick(MainPageActivity::class.java.name)
        }
    }

    @Test
    fun tosAreLaunched() {
        launch().use {
            initIntentAndCheckResponse()
            // Kind of ugly but Espresso being Espresso
            onView(withId(R.id.pres3_checkBox)).perform(clickPercent(0.9f, 0.5f))
            intended(allOf(hasComponent(ToSActivity::class.java.name)))
            release()
        }
    }
}