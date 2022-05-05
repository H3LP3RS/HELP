package com.github.h3lp3rs.h3lp.professional

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.h3lp3rs.h3lp.R

/**
 * Main activity of the professional portal
 */
class ProMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professional_main)
    }

    fun goToForumTheme(view: View) {
        val intent = Intent(this, ProfessionalTypeSelection::class.java)
        startActivity(intent)
    }

}