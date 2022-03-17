package com.github.h3lp3rs.h3lp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.View
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.listeners.SwipeListener
import com.github.h3lp3rs.h3lp.listeners.SwipeListener.Companion.SlideDirection.*
import com.github.h3lp3rs.h3lp.listeners.SwipeListener.Companion.swipeToNextActivity

/**
 * Class representing the first page of the app presentation
 * Contains attractive commercial information
 */
class PresArrivalActivity : AppCompatActivity() {
    /**
     * Creates the first presentation page activity
     * Nothing should be done when a click is detected, this is handled by the swipe listener
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentation_arrival)
        val gestureDetector = GestureDetector(this, SwipeListener({},
            swipeToNextActivity(this, LEFT, PresRelevantActivity::class.java), {}, {}))
        findViewById<View>(R.id.pres1_textView4).setOnTouchListener { view, event ->
            view.performClick()
            gestureDetector.onTouchEvent(event)
        }
    }
}