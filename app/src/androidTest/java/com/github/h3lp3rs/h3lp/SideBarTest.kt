package com.github.h3lp3rs.h3lp


import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SideBarTest {

    @get:Rule
    val testRule = ActivityScenarioRule(
        MainPageActivity::class.java
    )

    private val drawerLayout: ViewInteraction? = onView(withId(R.id.drawer_layout))

    private fun closeDrawerLayout() {
        drawerLayout?.perform(DrawerActions.close())
    }

    private fun openDrawerLayout() {
        drawerLayout?.perform(DrawerActions.open())
    }

    @Test
    fun openingDrawerLayoutWorks() {
        openDrawerLayout()
        drawerLayout?.check(matches(isOpen(Gravity.START)))
    }

    @Test
    fun closingDrawerLayoutWorks() {
        openDrawerLayout()
        closeDrawerLayout()
        drawerLayout?.check(matches(isClosed(Gravity.START)))
    }



}