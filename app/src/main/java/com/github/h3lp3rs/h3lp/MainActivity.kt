package com.github.h3lp3rs.h3lp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

const val EXTRA_NEEDED_MEDICATION = "needed_meds_key"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * Called when the user taps on the info button
     * Starts the presentation of the app
     */
    fun viewPresentation(view: View) {
        val i = Intent(this, PresentationActivity1::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     * Called when the user taps the help page button
     */
    fun goHelp(view: View) {
        val intent = Intent(this, HelpParametersActivity::class.java)
        startActivity(intent)
    }
}