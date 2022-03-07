package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity


class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //show the activity in full screen
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_page)
    }

    /** Called when the user taps the cpr rate button */
    fun goToCprActivity(view: View) {
        val intent = Intent(this, CprRateActivity::class.java)
        startActivity(intent)
    }
}