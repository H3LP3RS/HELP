package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.*
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Test

class AwaitHelpActivityTest {

    private val selectedMeds = arrayListOf("Epipen")

    @Test
    fun nameIsCorrectlyDisplayed() {
        val b = Bundle()
        b.putStringArrayList(EXTRA_NEEDED_MEDICATION, selectedMeds)

        val intent = Intent(getApplicationContext(), AwaitHelpActivity::class.java).apply {
            putExtras(b)
        }

        ActivityScenario.launch<AwaitHelpActivity>(intent).use {
            onView(withId(R.id.selected_items_text))
                .check(matches(isDisplayed()))
            onView(withId(R.id.selected_items_text))
                .check(matches(withText("Selected: $selectedMeds")))
        }
    }
}