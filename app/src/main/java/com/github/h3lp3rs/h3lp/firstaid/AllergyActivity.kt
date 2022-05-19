package com.github.h3lp3rs.h3lp.firstaid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import com.github.h3lp3rs.h3lp.FirstAidActivity
import com.github.h3lp3rs.h3lp.R
import kotlinx.android.synthetic.main.activity_allergy.*

class AllergyActivity : AppCompatActivity() {
    private val pathPrefix = "android.resource://"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allergy)

        val epipenVideo = findViewById<VideoView>(R.id.epipenVideo)
        epipenVideo.setVideoPath(pathPrefix + packageName + "/" + R.raw.epipen_tuto)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(epipenVideo)
        epipenVideo.setMediaController(mediaController)

        allergy_back_button.setOnClickListener{
            val intent = Intent(this, FirstAidActivity::class.java)
            startActivity(intent)
        }
    }

}