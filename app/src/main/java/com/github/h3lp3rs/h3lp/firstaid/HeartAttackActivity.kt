package com.github.h3lp3rs.h3lp.firstaid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import com.github.h3lp3rs.h3lp.FirstAidActivity
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

        findViewById<ImageButton>(R.id.heart_attack_back_button).setOnClickListener{
            val intent = Intent(this, FirstAidActivity::class.java)
            startActivity(intent)
        }
    }
}