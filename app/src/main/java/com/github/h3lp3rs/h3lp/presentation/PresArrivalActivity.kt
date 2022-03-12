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
 * Class representing the first page of the app presentation
 * Contains attractive commercial information
 */
class PresArrivalActivity : AppCompatActivity() {
    private val onSwipeLeft: () -> Unit = {
        // Launch next presentation page
        val i = Intent(this, PresRelevantActivity::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    /**
     * Creates the first presentation page activity
     * Nothing should be done when a click is detected, this is handled by the swipe listener
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentation1)
        val gestureDetector = GestureDetector(this, SwipeListener({}, onSwipeLeft, {}, {}))
        findViewById<View>(R.id.pres1_textView4).setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }
}