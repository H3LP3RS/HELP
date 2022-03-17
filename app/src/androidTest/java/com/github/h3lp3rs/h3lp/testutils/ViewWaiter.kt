package com.github.h3lp3rs.h3lp.testutils

import android.view.View
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.Matcher
import org.hamcrest.Matchers.any
import java.util.concurrent.TimeoutException

/**
 * Utility test class to wait for a view to appear in case of asynchronous events
 * TODO: This has not yet been tested
 */
class ViewWaiter(private val timeout: Long, private val targetViewId: Int) : ViewAction {

    override fun getConstraints(): Matcher<View> {
        return any(View::class.java)
    }

    override fun getDescription(): String {
        return "Wait up to $timeout milliseconds for the view $targetViewId to appear"
    }

    override fun perform(uiController: UiController, view: View) {
        val endTime = System.currentTimeMillis() + timeout
        val viewMatcher = withId(targetViewId)
        do {
            for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                // Found view with required ID
                if (viewMatcher.matches(child)) return
            }
            uiController.loopMainThreadForAtLeast(50)
        } while (System.currentTimeMillis() < endTime)

        throw PerformException.Builder()
            .withActionDescription(description)
            .withCause(TimeoutException("Waited $timeout milliseconds"))
            .withViewDescription(HumanReadables.describe(view))
            .build()
    }
}