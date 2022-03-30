package com.github.h3lp3rs.h3lp.firstaid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import com.github.h3lp3rs.h3lp.R

class HeartAttackActivity : AppCompatActivity() {
    private val pathPrefix = "android.resource://"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_attack)

        val heartAttackVideo = findViewById<VideoView>(R.id.heartAttackVideo)
        heartAttackVideo.setVideoPath(pathPrefix + packageName + "/" + R.raw.heart_attack)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(heartAttackVideo)
        heartAttackVideo.setMediaController(mediaController)

    }
}