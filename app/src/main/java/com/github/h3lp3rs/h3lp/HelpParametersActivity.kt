package com.github.h3lp3rs.h3lp


import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.models.EmergencyInformation
import com.github.h3lp3rs.h3lp.database.repositories.emergencyInfoRepository
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList


const val EXTRA_NEEDED_MEDICATION = "needed_meds_key"
const val EXTRA_CALLED_EMERGENCIES = "has_called_emergencies"

/**
 * Activity in which the user can select the medications they need urgently
 */
class HelpParametersActivity : AppCompatActivity() {
    // userLocation contains the user's current coordinates (is initialized to null since we could
    // encounter an error while getting the user's location)
    private var userLocation: Location? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var meds: ArrayList<String> = ArrayList()
    private val currentTime: Date = Calendar.getInstance().time;
    private var calledEmergencies = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_parameters)


        // Get the coordinates and display them on the screen to enable the user to give their exact
        // location to the emergency services
        updateCoordinates()
        val locationInformation: TextView = findViewById(R.id.location_information)
        val coordinatesText = getString(R.string.current_location)
        if (userLocation != null) {
            latitude =  userLocation!!.latitude
            longitude = userLocation!!.longitude
            locationInformation.text = String.format(
                    "%s latitude: %.4f longitude: %.4f",
            coordinatesText,
            userLocation!!.latitude,
            userLocation!!.longitude
            )
        } else {
            // If the user didn't allow location permissions, they won't be able to see their
            // current location
            locationInformation.text = getString(R.string.error_retrieving_location)
        }

    }


    /**
     *  Called when the user presses the emergency call button. Opens a pop-up
     *  asking the user to choose whether they want to call local emergency
     *  services or their emergency contact, and dials the correct number.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun emergencyCall(view: View) {
        val builder = AlertDialog.Builder(this)
        val emergencyCallPopup = layoutInflater.inflate(R.layout.emergency_call_options, null)

        builder.setCancelable(false)
        builder.setView(emergencyCallPopup)

        val alertDialog = builder.create()

        // ambulance button
        emergencyCallPopup.findViewById<ImageButton>(R.id.ambulance_call_button).setOnClickListener {
            // In case the getCurrentLocation failed (for example if the location services aren't
            // activated, currentLocation is still null and the returned phone number will be the
            // default emergency phone number
            calledEmergencies = true
            updateCoordinates()
            val emergencyNumber =
                LocalEmergencyCaller.getLocalEmergencyNumber(
                    userLocation?.longitude,
                    userLocation?.latitude,
                    this
                )

            val dial = "tel:$emergencyNumber"
            alertDialog.cancel()
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
        }

        // contact button
        emergencyCallPopup.findViewById<ImageButton>(R.id.contact_call_button).setOnClickListener {
            val medicalInfo = LocalStorage(getString(R.string.medical_info_prefs),this,false)
                .getObjectOrDefault(getString(R.string.medical_info_key), MedicalInformation::class.java, null)

            val dial = "tel:${medicalInfo?.emergencyContactNumber}"
            alertDialog.cancel()
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
        }

        alertDialog.show()
    }


    /**
     *  Called when the user presses the "search for help" button after selecting their need.
     */
    fun searchHelp(view: View) {
        meds = retrieveSelectedMedication(view)

        if (meds.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "Please select at least one item", Toast.LENGTH_SHORT
            ).show()
        } else {

            val b = Bundle()
            b.putStringArrayList(EXTRA_NEEDED_MEDICATION, meds)
            b.putBoolean(EXTRA_CALLED_EMERGENCIES, calledEmergencies)
            val intent = Intent(this, AwaitHelpActivity::class.java)
            intent.putExtras(b)
            sendInfoToDB()
            startActivity(intent)
        }
    }

    /**
     * Stores the emergency information in the database for further use
     */
    private fun sendInfoToDB(){
        if(latitude != null && longitude != null){
            val medicalInfo = LocalStorage(getString(R.string.medical_info_prefs),this,false).getStringOrDefault(getString(R.string.medical_info_key),"")
            val emergencyInfo = EmergencyInformation(latitude = latitude!!, longitude = longitude!!, meds =  meds, time = currentTime, medicalInfo = medicalInfo!!)
            emergencyInfoRepository.insert(emergencyInfo)
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
                    meds.add(child.textOff as String)
                }
            }
        }

        return meds

    }

    /**
     * Function that updates the user's current coordinates
     */
    private fun updateCoordinates() {
        val updatedCoordinates = GeneralLocationManager.get().getCurrentLocation(this)

        if (updatedCoordinates != null) {
            userLocation = Location(LocationManager.GPS_PROVIDER)
            userLocation?.longitude = updatedCoordinates.longitude
            userLocation?.latitude = updatedCoordinates.latitude
        } else {
            userLocation = null
        }

    }
}