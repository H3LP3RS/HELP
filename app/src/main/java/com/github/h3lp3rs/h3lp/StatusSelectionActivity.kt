package com.github.h3lp3rs.h3lp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.h3lp3rs.h3lp.professional.ProMainActivity
import com.github.h3lp3rs.h3lp.professional.VerificationActivity

class StatusSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_selection)
    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    /** Called when the user taps the allergy expand button */
    fun goToMainActivity(view: View) {
        goToActivity(MainPageActivity::class.java)
    }

    /** Called when the user taps the allergy expand button */
    fun goToProfessionalMainActivity(view: View) {
        goToActivity(VerificationActivity::class.java)
    }
}