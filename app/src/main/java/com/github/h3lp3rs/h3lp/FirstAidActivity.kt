package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.firstaid.AedActivity
import com.github.h3lp3rs.h3lp.firstaid.AllergyActivity
import com.github.h3lp3rs.h3lp.firstaid.AsthmaActivity
import com.github.h3lp3rs.h3lp.firstaid.HeartAttackActivity
import com.github.h3lp3rs.h3lp.professional.VerificationActivity
import kotlinx.android.synthetic.main.activity_first_aid.*

const val DELAY: Long = 1000

class FirstAidActivity : AppCompatActivity() {
    // Maps the clicked button to the activity it should launch to avoid code duplication
    private lateinit var buttonToActivity: Map<Button, Class<*>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid)
        buttonToActivity =
            hashMapOf(
                allergy_expand_button to AllergyActivity::class.java,
                heart_attack_expand_button to HeartAttackActivity::class.java,
                aed_expand_button to AedActivity::class.java,
                asthma_expand_button to AsthmaActivity::class.java
            )
    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    /**
     * Called whenever a user clicks a button in the activity, launches the button's corresponding
     * activity (as defined in buttonToActivity)
     * @param view The button that was clicked
     */
    fun goToButtonActivity(view: View) {
        // If the view isn't one of the buttons, don't do anything
        if (buttonToActivity.containsKey(view)) {
            goToActivity(buttonToActivity[view])
        }
    }

    /** Called when the user taps the back button */
    fun goToMainActivity(view: View) {
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, DELAY)
    }
}