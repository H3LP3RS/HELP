package com.github.h3lp3rs.h3lp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.View
import com.github.h3lp3rs.h3lp.listeners.SwipeListener

/**
 * Class representing the third page of the app presentation
 * The purpose of this activity is to explain what H3LP ought not to be confused with
 */
class PresentationActivity3 : AppCompatActivity() {
    private val onSwipeRight: () -> Unit = {
        // Go to previous page
        val i = Intent(this, PresentationActivity2::class.java)
        startActivity(i)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    /**
     * Creates the third presentation page activity
     * Nothing should be done when a click is detected, this is handled by the swipe listener
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presentation3)
        // Set correct swipe listeners
        val gestureDetector = GestureDetector(this, SwipeListener(onSwipeRight, {}, {}, {}))
        findViewById<View>(R.id.pres3_textView5).setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }

    /**
     * Function called when the user presses the approval button
     */
    fun sendApproval(view: View) {
        // Simply go back to the main activity for now
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}