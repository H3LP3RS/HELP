package com.github.h3lp3rs.h3lp


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ToggleButton

const val EMERGENCY_NUMBER = 112

/**
 * Activity in which the user can select the medications they need urgently
 */
class HelpParametersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_parameters)

    }

    /**
     *  Called when the user presses the emergency call button. Opens the phone call app with the
     *  emergency number dialed.
     */
    fun emergencyCall(view: View) {
        val dial = "tel:$EMERGENCY_NUMBER"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
    }


    /**
     *  Called when the user presses the "search for help" button after selecting their need.
     */
    fun searchHelp(view: View) {
        val meds = retrieveSelectedMedication(view)

        if (meds.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "Please select at least one item", Toast.LENGTH_SHORT
            ).show()
        } else {

            val b = Bundle()
            b.putStringArrayList(EXTRA_NEEDED_MEDICATION, meds)
            val intent = Intent(this, AwaitHelpActivity::class.java)
            intent.putExtras(b)

            startActivity(intent)
        }
    }

    /**
     * Auxiliary function to retrieve the selected meds on the page
     */
    private fun retrieveSelectedMedication(view: View): ArrayList<String> {
        val viewGroup = view.parent as ViewGroup

        val meds = arrayListOf<String>()

        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i) as View
            if (child is ToggleButton) {
                if (child.isChecked) {
                    meds.add(child.text as String)
                }
            }
        }

        return meds
    }
}