package com.github.h3lp3rs.h3lp

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalEmergencyCallTest : TestCase() {
    private val targetContext: Context = ApplicationProvider.getApplicationContext()
    private var localEmergencyCall = LocalEmergencyCall(0.0, 0.0, targetContext)

    @get:Rule
    val testRule = ActivityScenarioRule(
        HelpParametersActivity::class.java
    )


    @Test
    fun getPhoneNumberReturnsCorrect() {
    }
}