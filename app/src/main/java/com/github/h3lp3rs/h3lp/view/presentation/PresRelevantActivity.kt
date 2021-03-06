package com.github.h3lp3rs.h3lp.view.signin.presentation

import android.os.Bundle
import android.view.GestureDetector
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.view.signin.presentation.SwipeListener.Companion.SlideDirection.LEFT
import com.github.h3lp3rs.h3lp.view.signin.presentation.SwipeListener.Companion.SlideDirection.RIGHT
import com.github.h3lp3rs.h3lp.view.signin.presentation.SwipeListener.Companion.swipeToNextActivity

/**
 * Class representing the second page of the app presentation
 * The purpose of this activity is to explain what H3LP is about
 */
class PresRelevantActivity : AppCompatActivity() {
    /**
     * Creates the second presentation page activity
     * Nothing should be done when a click is detected, this is handled by the swipe listener
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentation_relevant)
        // Set correct swipe listeners
        val gestureDetector = GestureDetector(this, SwipeListener(
            swipeToNextActivity(this, RIGHT, PresArrivalActivity::class.java),
            swipeToNextActivity(this, LEFT, PresIrrelevantActivity::class.java), {}, {})
        )
        findViewById<View>(R.id.pres2_textView6).setOnTouchListener { view, event ->
            view.performClick()
            gestureDetector.onTouchEvent(event)
        }
    }
}