package com.github.h3lp3rs.h3lp


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

const val EXTRA_NEEDED_MEDICATION = "needed_meds_key"

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
     *  emergency number from the country the user is currently in dialed.
     */
    fun emergencyCall(view: View) {
        var emergencyNumber = LocalEmergencyCaller.DEFAULT_EMERGENCY_NUMBER
        // Checking if the location permissions have been granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // Retrieve current location and center camera around it
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val provider = locationManager.getBestProvider(Criteria(), true) // TODO : refactor

            val currentLocation = provider?.let { locationManager.getLastKnownLocation(it) }

            if (currentLocation != null) {
                val currentLong = currentLocation.longitude
                val currentLat = currentLocation.latitude
                emergencyNumber =
                    LocalEmergencyCaller.getLocalEmergencyNumber(currentLong, currentLat, this)
            }
        }
        val dial = "tel:$emergencyNumber"
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