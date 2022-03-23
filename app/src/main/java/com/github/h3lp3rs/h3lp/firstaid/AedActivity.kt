package com.github.h3lp3rs.h3lp.firstaid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import com.github.h3lp3rs.h3lp.R

class AedActivity : AppCompatActivity() {
    private val pathPrefix = "android.resource://"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aed)

        val aedVideo = findViewById<VideoView>(R.id.aedVideo)
        aedVideo.setVideoPath(pathPrefix + packageName + "/" + R.raw.aed)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(aedVideo)
        aedVideo.setMediaController(mediaController)

    }
}