package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.firstaid.AedActivity
import com.github.h3lp3rs.h3lp.firstaid.AllergyActivity
import com.github.h3lp3rs.h3lp.firstaid.AsthmaActivity
import com.github.h3lp3rs.h3lp.firstaid.HeartAttackActivity
import kotlinx.android.synthetic.main.activity_first_aid.*

class FirstAidActivity : AppCompatActivity() {
    // Maps the clicked button to the activity it should launch to avoid code duplication
    private val buttonToActivity =
        hashMapOf<Button, Class<*>>(
            allergy_expand_button to AllergyActivity::class.java,
            heart_attack_expand_button to HeartAttackActivity::class.java,
            aed_expand_button to AedActivity::class.java,
            asthma_expand_button to AsthmaActivity::class.java
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid)
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
        buttonToActivity[view]?.let { goToActivity(it) }
    }
}