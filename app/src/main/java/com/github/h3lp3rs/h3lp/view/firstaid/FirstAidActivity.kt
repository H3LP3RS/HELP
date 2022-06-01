package com.github.h3lp3rs.h3lp.firstaid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.firstaid.FirstAidHowTo
import com.github.h3lp3rs.h3lp.model.firstaid.FirstAidHowTo.*
import kotlinx.android.synthetic.main.activity_first_aid.*

class FirstAidActivity : AppCompatActivity() {
    // Maps the clicked button to the extra of the activity it should launch to avoid code duplication
    private lateinit var buttonToFirstAidExtra: Map<Button, FirstAidHowTo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_aid)
        buttonToFirstAidExtra =
            hashMapOf(
                allergy_expand_button to ALLERGY,
                heart_attack_expand_button to HEART_ATTACK,
                aed_expand_button to AED,
                asthma_expand_button to ASTHMA
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
        if (buttonToFirstAidExtra.containsKey(view)) {
            val intent = Intent(this, GeneralFirstAidActivity::class.java)
            intent.putExtra(EXTRA_FIRST_AID, buttonToFirstAidExtra[view])
            startActivity(intent)
        }
    }

    /** Called when the user taps the back button */
    fun goToMainActivity(view: View) {
        goToActivity(MainPageActivity::class.java)
    }
}