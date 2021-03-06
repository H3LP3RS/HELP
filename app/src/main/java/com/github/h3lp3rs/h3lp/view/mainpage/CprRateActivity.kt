package com.github.h3lp3rs.h3lp.view.mainpage

import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R

class CprRateActivity : AppCompatActivity() {

    private var animationIsPlaying = false
    private lateinit var startButton: Button
    private lateinit var heartIcon: ImageView
    private lateinit var heartBeatAnimation: Animation
    private lateinit var beepSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cpr_rate)

        // Initialization of CPR activity
        startButton = findViewById(R.id.startRateButton)
        heartIcon = findViewById(R.id.heartIcon)
        heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heartbeat_animation)

        beepSound = MediaPlayer.create(this, R.raw.beep)
        beepSound.isLooping = true

        /**
         * Note: The animation of the heartbeat is currently commented out since
         *      espresso tests freeze if animations are running. I haven't yet
         *      found a way to only disable such animations at test time (these aren't the same
         *      as the ones that can be disabled in Android developer settings).
         */

        /**
         * The activity essentially has 2 states:
         * 1. Nothing is happening, the button text is set to "START"
         * 2. If the button is clicked, the animation of a heartbeat starts playing
         *    with an accompanying sound, the button text is set to "STOP"
         *
         * The heartbeat animation/sound run at 107 beats/beeps per minute since this was
         * found to be the optimal CPR rate
         * Source: Duval S, Pepe PE, Aufderheide TP, et al. Optimal Combination of Compression Rate
         * and Depth During Cardiopulmonary Resuscitation for Functionally Favorable Survival.
         * JAMA Cardiol. 2019;4(9):900???908. doi:10.1001/jamacardio.2019.2717
         */

        startButton.setOnClickListener {
            if (animationIsPlaying) {
                stop()
            } else {
                start()
            }
        }
    }

    /**
     * Stop all the animations when the cpr is no longer
     * in the foreground.
     */
    override fun onPause() {
        stop()
        super.onPause()
    }

    // Stops the CPR rate
    private fun stop() {
        // Commented since animations cause problems on Cirrus' emulator
        //heartIcon.clearAnimation()
        startButton.text = getString(R.string.cpr_rate_button_start)
        beepSound.pause()
        beepSound.seekTo(0)
        animationIsPlaying = false
    }

    // Starts the CPR rate
    private fun start() {
        //heartIcon.startAnimation(heartBeatAnimation)
        startButton.text = getString(R.string.cpr_rate_button_stop)
        beepSound.start()
        animationIsPlaying = true
    }
}
