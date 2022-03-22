package com.github.h3lp3rs.h3lp.firstaid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import com.github.h3lp3rs.h3lp.R

class AsthmaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asthma)

        val asthmaVideo = findViewById<VideoView>(R.id.asthmaVideo)
        asthmaVideo.setVideoPath("android.resource://" + packageName + "/" + R.raw.asthma)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(asthmaVideo)
        asthmaVideo.setMediaController(mediaController)
    }
}