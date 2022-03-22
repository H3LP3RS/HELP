package com.github.h3lp3rs.h3lp.firstaid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import com.github.h3lp3rs.h3lp.R

class AllergyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allergy)

        val epipenVideo = findViewById<VideoView>(R.id.epipenVideo)
        epipenVideo.setVideoPath("android.resource://" + packageName + "/" + R.raw.epipen_tuto)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(epipenVideo)
        epipenVideo.setMediaController(mediaController)
    }
}