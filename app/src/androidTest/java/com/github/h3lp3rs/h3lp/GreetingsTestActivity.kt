package com.github.h3lp3rs.h3lp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GreetingsTestActivity {
    val name = "Alexis"
    @Test
    fun nameIsCorrectlyDisplayed() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), GreetingActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, name)
        }
        ActivityScenario.launch<GreetingActivity>(intent).use {
            onView(withId(R.id.textView)).check(matches(isDisplayed()))
            onView(withId(R.id.textView)).check(matches(withText(name)))
        }
    }
} 