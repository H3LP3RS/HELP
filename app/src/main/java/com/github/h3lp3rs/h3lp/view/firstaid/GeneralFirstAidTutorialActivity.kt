package com.github.h3lp3rs.h3lp.view.firstaid

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.dataclasses.FirstAidHowTo
import com.github.h3lp3rs.h3lp.model.dataclasses.FirstAidHowTo.*


const val EXTRA_FIRST_AID = "first_aid"

/**
 * Generic activity used for all tutorial pages
 */
class GeneralFirstAidActivity : AppCompatActivity() {
    private val pathPrefix = "android.resource://"

    // The map is only initialized in the onCreate since we need access to the resources which are
    // only guaranteed to be initialized when in the onCreate method of the activity
    private lateinit var firstAidToParameters: Map<FirstAidHowTo, FirstAidHowToParameters>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialising firstAidToParameters with all the required parameters to generate the
        // specific activity
        firstAidToParameters = mapOf(
            AED to FirstAidHowToParameters(
                R.layout.activity_aed,
                R.id.aedVideo,
                R.raw.aed,
                R.id.aed_back_button
            ),
            ALLERGY to FirstAidHowToParameters(
                R.layout.activity_allergy,
                R.id.epipenVideo,
                R.raw.epipen_tuto,
                R.id.allergy_back_button
            ),
            ASTHMA to FirstAidHowToParameters(
                R.layout.activity_asthma,
                R.id.asthmaVideo,
                R.raw.asthma,
                R.id.asthma_back_button
            ),
            HEART_ATTACK to FirstAidHowToParameters(
                R.layout.activity_heart_attack,
                R.id.heartAttackVideo,
                R.raw.heart_attack,
                R.id.heart_attack_back_button
            )
        )

        val firstAidText = intent.getSerializableExtra(EXTRA_FIRST_AID)
        val firstAid = firstAidText?.let { it as FirstAidHowTo }

        firstAidToParameters[firstAid]?.let {
            setContentView(it.layoutId)

            // Setting up the video
            val video = findViewById<VideoView>(it.videoViewId)
            video.setVideoPath(pathPrefix + packageName + "/" + it.videoId)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(video)
            video.setMediaController(mediaController)

            // Setting up the back button
            val backButton = findViewById<ImageButton>(it.backButtonId)
            backButton.setOnClickListener {
                val intent = Intent(this, FirstAidActivity::class.java)
                startActivity(intent)
            }
        } ?: run {
            // If the given intent was invalid, go back to the main page
            val intent = Intent(applicationContext, MainPageActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * @param layoutId If of the tutorial's layout
     * @param videoViewId Id of the view in which to display the video
     * @param videoId Id of the video
     * @param backButtonId Id of the button to go back to the previous activity
     */
    private data class FirstAidHowToParameters(
        val layoutId: Int,
        val videoViewId: Int,
        val videoId: Int,
        val backButtonId: Int
    )
}