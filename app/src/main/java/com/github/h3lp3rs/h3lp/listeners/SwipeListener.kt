package com.github.h3lp3rs.h3lp.listeners

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

/**
 * Instances of this class can recognize common swipe gestures on a given activity
 */
class SwipeListener(val onSwipeRight: () -> Unit, val onSwipeLeft: () -> Unit,
                    val onSwipeTop: () -> Unit, val onSwipeBottom: () -> Unit)
                    : GestureDetector.SimpleOnGestureListener() {

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    /**
     * Must be set to true to enable fling detection
     */
    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    /**
     * What ought to be done when a random fling movement is detected
     */
    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        var result = false
        val diffY = e2.y - e1.y
        val diffX = e2.x - e1.x
        if (abs(diffX) > abs(diffY)) {
            if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                if (diffX > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
                result = true
            }
        } else if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
            if (diffY > 0) {
                onSwipeBottom()
            } else {
                onSwipeTop()
            }
            result = true
        }
        return result
    }
}