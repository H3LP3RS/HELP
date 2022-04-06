package com.github.h3lp3rs.h3lp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import com.github.h3lp3rs.h3lp.notification.NotificationService
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Should use uiDevice instead of espresso to be able to test element outside the scope of application
 */

class NotificationTest {

    private val ctx: Context = ApplicationProvider.getApplicationContext()
    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val TITLE : String = "Title"
    private val DESCRIPTION : String = "Description"

    @get:Rule
    val testRule = ActivityScenarioRule(
        MainPageActivity::class.java
    )


    @Test
    fun sendSimpleNotificationWork() {
        NotificationService.createNotificationChannel(ctx)
        NotificationService.sendSimpleNotification(ctx,TITLE,DESCRIPTION)

        uiDevice.openNotification()
        uiDevice.wait(Until.hasObject(By.textStartsWith("H3LP")),3000)

        val title : UiObject2=uiDevice.findObject(By.text(TITLE))
        val description : UiObject2=uiDevice.findObject(By.text(DESCRIPTION))
        assertEquals(title.text,TITLE)
        assertEquals(description.text,DESCRIPTION)
        //Cirus AVD seems to  not have a clear all button :(
        //clearAllNotifications()
    }

    @Test
    fun sendIntentNotificationWorkAndOpenDesiredActivity() {
        NotificationService.createNotificationChannel(ctx)
        NotificationService.sendOpenActivityNotification(ctx,TITLE,DESCRIPTION,MySkillsActivity::class.java)

        uiDevice.openNotification()
        uiDevice.wait(Until.hasObject(By.textStartsWith("H3LP")),3000)

        val title : UiObject2=uiDevice.findObject(By.text(TITLE))
        val description : UiObject2=uiDevice.findObject(By.text(DESCRIPTION))
        assertEquals(title.text,TITLE)
        assertEquals(description.text,DESCRIPTION)
        title.click()
        uiDevice.findObject(By.textStartsWith(ctx.getString(R.string.my_helper_skills)))

        //Cirus AVD seems to  not have a clear all button :(
        //clearAllNotifications()
    }


    /**
     * Use it to clear all notification after testing
     */
    private fun clearAllNotifications() {
        uiDevice.openNotification()
        uiDevice.wait(Until.hasObject(By.textStartsWith("H3LP")), 1000)
        val clearAll: UiObject2 = uiDevice.findObject(By.textStartsWith("Clear all"))
        clearAll.click()
    }
}