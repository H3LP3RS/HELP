package com.github.h3lp3rs.h3lp.professional

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProProfileActivityTest {
    @get:Rule
    val testRule = ActivityScenarioRule(
        VerificationActivity::class.java
    )


}