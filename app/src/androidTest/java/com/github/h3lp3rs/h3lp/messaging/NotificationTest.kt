package com.github.h3lp3rs.h3lp.messaging

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.notifications.NotificationService
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest
import com.github.h3lp3rs.h3lp.view.profile.MySkillsActivity
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Should use uiDevice instead of espresso to be able to test element outside the scope of application
 */

class NotificationTest : H3lpAppTest(){

    private val ctx: Context = ApplicationProvider.getApplicationContext()
    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val TITLE : String = "Title"
    private val DESCRIPTION : String = "Description"

    @get:Rule
    val testRule = ActivityScenarioRule(
        SignInActivity::class.java
    )
    @Before
    fun setup() {
        NotificationService.createNotificationChannel(ctx)
    }


    @Test
    fun sendSimpleNotificationWork() {
        NotificationService.sendSimpleNotification(ctx,TITLE,DESCRIPTION)

        uiDevice.openNotification()
        uiDevice.wait(Until.hasObject(By.textStartsWith("H3LP")), TEST_TIMEOUT)

        val title : UiObject2=uiDevice.findObject(By.text(TITLE))
        val description : UiObject2=uiDevice.findObject(By.text(DESCRIPTION))
        assertEquals(title.text,TITLE)
        assertEquals(description.text,DESCRIPTION)
        // Cirrus AVD seems not to have a clear all button :(
        // clearAllNotifications()
    }

    @Test
    fun sendIntentNotificationWorkAndOpenDesiredActivity() {
        NotificationService.sendOpenActivityNotification(ctx,TITLE,DESCRIPTION, MySkillsActivity::class.java)

        uiDevice.openNotification()
        uiDevice.wait(Until.hasObject(By.textStartsWith("H3LP")),3000)

        val title : UiObject2=uiDevice.findObject(By.text(TITLE))
        val description : UiObject2=uiDevice.findObject(By.text(DESCRIPTION))

        assertEquals(title.text,TITLE)
        assertEquals(description.text,DESCRIPTION)

        title.click()
        uiDevice.findObject(By.textStartsWith(ctx.getString(R.string.my_helper_skills)))

        uiDevice.pressBack()
        //Cirus AVD seems to  not have a clear all button :(
        //clearAllNotifications()
    }


    /**
     * Use it to clear all notification after testing
     */
    private fun clearAllNotifications() {
        uiDevice.openNotification()
        uiDevice.wait(Until.hasObject(By.textStartsWith("H3LP")), TEST_TIMEOUT)

        val clearAll: UiObject2 = uiDevice.findObject(By.textStartsWith("Clear all"))
        clearAll.click()
    }


}