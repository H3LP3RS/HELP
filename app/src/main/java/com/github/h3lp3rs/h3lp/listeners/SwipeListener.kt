package com.github.h3lp3rs.h3lp.listeners

import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.ORIGIN
import com.github.h3lp3rs.h3lp.R
import kotlin.math.abs

/**
 * Instances of this class can recognize common swipe gestures on a given activity
 */
class SwipeListener(
    private val onSwipeRight: () -> Unit, private val onSwipeLeft: () -> Unit,
    private val onSwipeTop: () -> Unit, private val onSwipeBottom: () -> Unit)
                    : GestureDetector.SimpleOnGestureListener() {

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    private fun detectSwipeDirectionAndDo(diff: Float, velocity: Float, posAction: () -> Unit,
                                          negAction: () -> Unit): Boolean {
        if (abs(diff) > swipeThreshold && abs(velocity) > swipeVelocityThreshold) {
            if (diff > 0) {
                posAction()
            } else {
                negAction()
            }
            return true
        }
        return false
    }

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
        val diffY = e2.y - e1.y
        val diffX = e2.x - e1.x
        return if (abs(diffX) > abs(diffY)) {
            detectSwipeDirectionAndDo(diffX, velocityX, onSwipeRight, onSwipeLeft)
        } else {
            detectSwipeDirectionAndDo(diffY, velocityY, onSwipeBottom, onSwipeTop)
        }
    }

    /**
     * Allows to build swipe actions easier
     */
    companion object {
        enum class SlideDirection(val slideIn: Int, val slideOut: Int) {
            LEFT(R.anim.slide_in_right, R.anim.slide_out_left),
            RIGHT(R.anim.slide_in_left, R.anim.slide_out_right);
        }

        fun swipeToNextActivity(curr: AppCompatActivity, dir: SlideDirection,
                                ActivityName: Class<*>?, origin: String?): () -> Unit {
            return {
                val i = Intent(curr, ActivityName)
                if(origin != null) {
                    i.putExtra(ORIGIN, origin)
                }
                curr.startActivity(i)
                curr.overridePendingTransition(dir.slideIn, dir.slideOut)
            }
        }
    }
}