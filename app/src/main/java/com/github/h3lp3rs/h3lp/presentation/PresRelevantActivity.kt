package com.github.h3lp3rs.h3lp.presentation

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.View
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.listeners.SwipeListener
import com.github.h3lp3rs.h3lp.listeners.SwipeListener.Companion.SlideDirection.*
import com.github.h3lp3rs.h3lp.listeners.SwipeListener.Companion.swipeToNextActivity

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
            swipeToNextActivity(this, LEFT, PresIrrelevantActivity::class.java), {}, {}))
        findViewById<View>(R.id.pres2_textView6).setOnTouchListener { view, event ->
            view.performClick()
            gestureDetector.onTouchEvent(event)
        }
    }
}