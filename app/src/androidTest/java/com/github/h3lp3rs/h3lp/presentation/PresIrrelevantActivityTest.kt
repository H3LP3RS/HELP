package com.github.h3lp3rs.h3lp.presentation

import android.app.Activity
import android.app.Instrumentation.*
import android.content.Intent
import android.view.InputDevice
import android.view.MotionEvent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
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
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.ViewAction
import com.android.dx.command.Main
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.signin.ORIGIN
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf


@RunWith(AndroidJUnit4::class)
class PresIrrelevantActivityTest {

    private fun alreadyAccepted() {
        storageOf(USER_COOKIE).setBoolean(globalContext.getString(R.string.KEY_USER_AGREE), true)
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
            Press.FINGER,
            InputDevice.SOURCE_MOUSE,
            MotionEvent.BUTTON_PRIMARY)
    }

    private fun checkLaunchOnApprovalClick(activity: String) {
        init()
        val i = Intent()
        val intentResult = ActivityResult(Activity.RESULT_OK, i)
        intending(anyIntent()).respondWith(intentResult)
        onView(withId(R.id.pres3_button)).perform(click())
        intended(allOf(hasComponent(activity)))
        release()
    }

    @Before
    fun clearPreferences() {
        globalContext = ApplicationProvider.getApplicationContext()
        Databases.PREFERENCES.db = MockDatabase()
        resetStorage()
    }

    @Test
    fun successfulDisplay() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), PresIrrelevantActivity::class.java).apply {
            putExtra(ORIGIN, MainPageActivity::class.qualifiedName)
        }

        ActivityScenario.launch<PresIrrelevantActivity>(intent).use {
            onView(withId(R.id.pres3_textView1)).check(matches(isDisplayed()))
            onView(withId(R.id.pres3_textView2)).check(matches(isDisplayed()))
            onView(withId(R.id.pres3_textView3)).check(matches(isDisplayed()))
            onView(withId(R.id.pres3_textView4)).check(matches(isDisplayed()))
            onView(withId(R.id.pres3_textView5)).check(matches(isDisplayed()))
            onView(withId(R.id.pres3_textView6)).check(matches(isDisplayed()))
            onView(withId(R.id.pres3_imageView1)).check(matches(isDisplayed()))
            onView(withId(R.id.pres3_imageView2)).check(matches(isDisplayed()))
            onView(withId(R.id.pres3_imageView3)).check(matches(isDisplayed()))
            onView(withId(R.id.pres3_checkBox)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun boxDefaultNotChecked() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), PresIrrelevantActivity::class.java).apply {
            putExtra(ORIGIN, MainPageActivity::class.qualifiedName)
        }
        ActivityScenario.launch<PresIrrelevantActivity>(intent).use {
            onView(withId(R.id.pres3_checkBox)).check(matches(isNotChecked()))
        }
    }

    @Test
    fun boxCheckedIfDoneAlready() {
        alreadyAccepted()
        val intent = Intent(ApplicationProvider.getApplicationContext(), PresIrrelevantActivity::class.java).apply {
            putExtra(ORIGIN, MainPageActivity::class.qualifiedName)
        }
        ActivityScenario.launch<PresIrrelevantActivity>(intent).use {
            onView(withId(R.id.pres3_checkBox)).check(matches(isChecked()))
        }
    }

    @Test
    fun unsuccessfulApprovalButtonNeverApproved() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), PresIrrelevantActivity::class.java).apply {
            putExtra(ORIGIN, MainPageActivity::class.qualifiedName)
        }
        ActivityScenario.launch<PresIrrelevantActivity>(intent).use {
            init()
            val i = Intent()
            val intentResult = ActivityResult(Activity.RESULT_OK, i)
            intending(anyIntent()).respondWith(intentResult)
            onView(withId(R.id.pres3_button)).perform(click())
            assertThat(getIntents().size, `is`(0))
            release()
        }
    }

    @Test
    fun successfulApprovalButtonAlreadyApprovedFromMain() {
        alreadyAccepted()
        val intent = Intent(ApplicationProvider.getApplicationContext(), PresIrrelevantActivity::class.java).apply {
            putExtra(ORIGIN, MainPageActivity::class.qualifiedName)
        }
        ActivityScenario.launch<PresIrrelevantActivity>(intent).use {
            checkLaunchOnApprovalClick(MainPageActivity::class.java.name)
        }
    }

    @Test
    fun successfulApprovalButtonAlreadyApprovedFromSignIn() {
        alreadyAccepted()
        val intent = Intent(ApplicationProvider.getApplicationContext(), PresIrrelevantActivity::class.java).apply {
            putExtra(ORIGIN, SignInActivity::class.qualifiedName)
        }
        ActivityScenario.launch<PresIrrelevantActivity>(intent).use {
            checkLaunchOnApprovalClick(MainPageActivity::class.java.name)
        }
    }

    @Test
    fun tickAndAcceptGoToSignIn() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), PresIrrelevantActivity::class.java).apply {
            putExtra(ORIGIN, SignInActivity::class.qualifiedName)
        }
        ActivityScenario.launch<PresIrrelevantActivity>(intent).use {
            onView(withId(R.id.pres3_checkBox)).perform(click())
            checkLaunchOnApprovalClick(MainPageActivity::class.java.name)
        }
    }

    @Test
    fun tickAndAcceptGoToMain() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), PresIrrelevantActivity::class.java).apply {
            putExtra(ORIGIN, MainPageActivity::class.qualifiedName)
        }
        ActivityScenario.launch<PresIrrelevantActivity>(intent).use {
            onView(withId(R.id.pres3_checkBox)).perform(click())
            checkLaunchOnApprovalClick(MainPageActivity::class.java.name)
        }
    }

    @Test
    fun tosAreLaunched() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), PresIrrelevantActivity::class.java).apply {
            putExtra(ORIGIN, MainPageActivity::class.qualifiedName)
        }
        ActivityScenario.launch<PresIrrelevantActivity>(intent).use {
            init()
            val i = Intent()
            val intentResult = ActivityResult(Activity.RESULT_OK, i)
            intending(anyIntent()).respondWith(intentResult)
            // Kind of ugly but Espresso being Espresso
            onView(withId(R.id.pres3_checkBox)).perform(clickPercent(0.9f, 0.5f))
            intended(allOf(hasComponent(ToSActivity::class.java.name)))
            release()
        }
    }

    /*@Test
    fun successfulSlideRight() {
        // init()
        // val intent = Intent()
        // val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        // intending(anyIntent()).respondWith(intentResult)
        onView(withId(R.id.pres3_textView5)).perform(swipeRight())
        // intended(allOf(hasComponent(PresRelevantActivity::class.java.name))) Cirrus broken
        // release()
    }*/
}