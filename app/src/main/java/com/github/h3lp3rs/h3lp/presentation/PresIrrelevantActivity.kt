package com.github.h3lp3rs.h3lp.presentation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.View
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.listeners.SwipeListener
import com.github.h3lp3rs.h3lp.listeners.SwipeListener.Companion.SlideDirection.*
import com.github.h3lp3rs.h3lp.listeners.SwipeListener.Companion.swipeToNextActivity

/**
 * Class representing the third page of the app presentation
 * The purpose of this activity is to explain what H3LP ought not to be confused with
 */
class PresIrrelevantActivity : AppCompatActivity() {
    /**
     * Creates the third presentation page activity
     * Nothing should be done when a click is detected, this is handled by the swipe listener
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentation3)
        // Set correct swipe listeners
        val gestureDetector = GestureDetector(this, SwipeListener(
            swipeToNextActivity(this, RIGHT, PresRelevantActivity::class.java), {}, {}, {}))
        findViewById<View>(R.id.pres3_textView5).setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }

    /**
     * Function called when the user presses the approval button
     */
    fun sendApproval(view: View) {
        // Simply go back to the main activity for now
        val i = Intent(this, MainPageActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}