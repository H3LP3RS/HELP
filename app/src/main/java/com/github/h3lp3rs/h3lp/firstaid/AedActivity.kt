package com.github.h3lp3rs.h3lp.firstaid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import com.github.h3lp3rs.h3lp.FirstAidActivity
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

        findViewById<ImageButton>(R.id.aed_back_button).setOnClickListener{
            val intent = Intent(this, FirstAidActivity::class.java)
            startActivity(intent)
        }
    }
}