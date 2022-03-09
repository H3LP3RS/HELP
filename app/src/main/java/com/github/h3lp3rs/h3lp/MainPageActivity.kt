package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity


class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * show the activity in full screen
         */
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main_page)
    }

    /** Called when the user taps the cpr rate button */
    fun goToCprActivity(view: View) {
        val intent = Intent(this, CprRateActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the help page button */
    fun goToHelpParametersActivity(view: View) {
        val intent = Intent(this, HelpParametersActivity::class.java)
        startActivity(intent)
    }

    /** Called when the user taps the profile page button */
    fun goToProfileActivity(view: View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
}