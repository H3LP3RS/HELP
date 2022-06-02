package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.provider.Settings.Global.getString
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.ReportActivity.Companion.bug
import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
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
    fun setUp() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(), ReportActivity::class.java
        ).putExtra(EXTRA_REPORT_CATEGORY, bug)

        SignInActivity.userUid = H3lpAppTest.USER_TEST_ID
        setDatabase(Databases.REPORTS, MockDatabase())
        reportsDb = databaseOf(Databases.REPORTS)

        ActivityScenario.launch<ReportActivity>(intent)
        init()

    }

    @Test
    fun sendReportButtonWorks() {
        onView(withId(R.id.reportCategoryDropdown))
            .perform(replaceText("bug"))

        onView(withId(R.id.reportEditTxt))
            .perform(replaceText(""))

        onView(withId(R.id.reportSaveButton))
            .perform(click())

        val report = reportsDb.getObject(H3lpAppTest.USER_TEST_ID, Report::class.java).get()

        assertEquals(report.category, "bug")
        assertEquals(report.content, "")

        release()
    }
}