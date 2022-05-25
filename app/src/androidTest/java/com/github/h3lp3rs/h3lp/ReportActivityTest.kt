package com.github.h3lp3rs.h3lp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.ReportActivity.Companion.bug
import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.dataclasses.Report
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportActivityTest {
    private lateinit var reportsDb: Database

    @Before
    fun setUp(){
        val intent = Intent(
            ApplicationProvider.getApplicationContext(), ReportActivity::class.java
        ).putExtra(EXTRA_REPORT_CATEGORY, bug)

        SignInActivity.userUid = H3lpAppTest.USER_TEST_ID
        Databases.setDatabase(Databases.REPORTS, MockDatabase())
        reportsDb = Databases.databaseOf(Databases.REPORTS)

        Intents.init()
        ActivityScenario.launch<ReportActivity>(intent)
    }

    @Test
    fun sendReportButtonWorks(){
        Espresso.onView(ViewMatchers.withId(R.id.reportCategoryDropdown))
            .perform(ViewActions.replaceText(bug))

        Espresso.onView(ViewMatchers.withId(R.id.reportEditTxt))
            .perform(ViewActions.replaceText(""))

        Espresso.onView(ViewMatchers.withId(R.id.reportSaveButton))
            .perform(click())

        val report  = reportsDb.getObject(H3lpAppTest.USER_TEST_ID, Report::class.java).get()

        assertEquals(report.category, bug)
        assertEquals(report.content, "")

        release()
    }
}