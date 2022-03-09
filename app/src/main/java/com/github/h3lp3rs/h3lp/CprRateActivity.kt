package com.github.h3lp3rs.h3lp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.media.MediaPlayer

class CprRateActivity : AppCompatActivity() {
    private var animationIsPlaying = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cpr_rate)

        val startButton = findViewById<Button>(R.id.startRateButton)
        val heartIcon = findViewById<ImageView>(R.id.heartIcon)
        val heartBeatAnimation = AnimationUtils.loadAnimation(this, R.anim.heartbeat_animation)


        val beepSound = MediaPlayer.create(this, R.raw.beep)
        beepSound.isLooping = true

        startButton.setOnClickListener {
            if (animationIsPlaying) {
//                heartIcon.clearAnimation()
                startButton.text = getString(R.string.cpr_rate_button_start)
                beepSound.pause()
                beepSound.seekTo(0)
                animationIsPlaying = false
            } else {
//                heartIcon.startAnimation(heartBeatAnimation)
                startButton.text = getString(R.string.cpr_rate_button_stop)
                beepSound.start()
                animationIsPlaying = true
            }

        }
    }
}