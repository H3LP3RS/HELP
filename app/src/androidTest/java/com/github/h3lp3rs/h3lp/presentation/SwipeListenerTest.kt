package com.github.h3lp3rs.h3lp.presentation

import android.view.MotionEvent
import androidx.test.core.view.MotionEventBuilder.*
import com.github.h3lp3rs.h3lp.listeners.SwipeListener
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * This test class only tests the swipe mechanism while isolating activities & Espresso since these
 * are unstable on Cirrus
 */
class SwipeListenerTest {

    private val swipeThreshold = 100f
    private val swipeVelocityThreshold = 100f

    private var counter = 0

    private fun motionEvent(x: Float, y: Float): MotionEvent {
        return newBuilder().setPointer(x, y).build()
    }

    @Before
    fun init() {
        counter = 0
    }

    @Test
    fun swipeRightIsTriggered() {
        val swiperListener = SwipeListener({++counter}, {}, {}, {})
        swiperListener.onFling(motionEvent(0f, 0f), motionEvent(swipeThreshold + 1, 0f),
            swipeVelocityThreshold + 1, 0f)
        assertEquals(1, counter)
    }

    @Test
    fun swipeLeftIsTriggered() {
        val swiperListener = SwipeListener({}, {++counter}, {}, {})
        swiperListener.onFling(motionEvent(swipeThreshold + 1, 0f), motionEvent(0f, 0f),
            swipeVelocityThreshold + 1, 0f)
        assertEquals(1, counter)
    }

    @Test
    fun swipeUpIsTriggered() {
        val swiperListener = SwipeListener({}, {}, {++counter}, {})
        swiperListener.onFling(motionEvent(0f, swipeThreshold + 1), motionEvent(0f, 0f),
            0f, swipeVelocityThreshold + 1)
        assertEquals(1, counter)
    }

    @Test
    fun swipeDownIsTriggered() {
        val swiperListener = SwipeListener({}, {}, {}, {++counter})
        swiperListener.onFling(motionEvent(0f, 0f), motionEvent(0f, swipeThreshold + 1),
            0f, swipeVelocityThreshold + 1)
        assertEquals(1, counter)
    }

    @Test
    fun noSwipeDoesNotTrigger() {
        val swiperListener = SwipeListener({++counter}, {++counter}, {++counter}, {++counter})
        swiperListener.onFling(motionEvent(0f, 0f), motionEvent(0f, 0f),
            0f, 0f)
        assertEquals(0, counter)
    }

    @Test
    fun onDownReturnsTrue() {
        val swiperListener = SwipeListener({}, {}, {}, {})
        assertTrue(swiperListener.onDown(motionEvent(0f, 0f)))
    }
}