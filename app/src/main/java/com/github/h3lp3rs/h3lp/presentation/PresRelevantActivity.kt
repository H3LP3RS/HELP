package com.github.h3lp3rs.h3lp.presentation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.View
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.listeners.SwipeListener

/**
 * Class representing the second page of the app presentation
 * The purpose of this activity is to explain what H3LP is about
 */
class PresRelevantActivity : AppCompatActivity() {
    private val onSwipeLeft: () -> Unit = {
        // Launch next presentation page
        val i = Intent(this, PresIrrelevantActivity::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    private val onSwipeRight: () -> Unit = {
        // Go to previous page
        val i = Intent(this, PresArrivalActivity::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    /**
     * Creates the second presentation page activity
     * Nothing should be done when a click is detected, this is handled by the swipe listener
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentation2)
        // Set correct swipe listeners
        val gestureDetector = GestureDetector(this, SwipeListener(onSwipeRight, onSwipeLeft,
            {}, {}))
        findViewById<View>(R.id.pres2_textView6).setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }
}