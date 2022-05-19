package com.github.h3lp3rs.h3lp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.h3lp3rs.h3lp.firstaid.AedActivity
import com.github.h3lp3rs.h3lp.firstaid.AllergyActivity
import com.github.h3lp3rs.h3lp.firstaid.AsthmaActivity
import com.github.h3lp3rs.h3lp.firstaid.HeartAttackActivity

class FirstAidActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid)
    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    /** Called when the user taps the allergy expand button */
    fun goToAllergyActivity(view: View) {
        goToActivity(AllergyActivity::class.java)
    }

    /** Called when the user taps the heart attack expand button */
    fun goToHeartAttackActivity(view: View) {
        goToActivity(HeartAttackActivity::class.java)
    }

    /** Called when the user taps the AED expand button */
    fun goToAedActivity(view: View) {
        goToActivity(AedActivity::class.java)
    }

    /** Called when the user taps the asthma expand button */
    fun goToAsthmaActivity(view: View) {
        goToActivity(AsthmaActivity::class.java)
    }

    /** Called when the user taps the back button */
    fun goToMainActivity(view: View) {
        goToActivity(MainPageActivity::class.java)
    }
}