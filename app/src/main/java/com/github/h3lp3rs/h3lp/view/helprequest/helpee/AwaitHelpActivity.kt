package com.github.h3lp3rs.h3lp.view.helprequest.helpee

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases.CONVERSATION_IDS
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.model.database.Databases.EMERGENCIES
import com.github.h3lp3rs.h3lp.model.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.model.dataclasses.FirstAidHowTo.*
import com.github.h3lp3rs.h3lp.model.dataclasses.MedicalInformation
import com.github.h3lp3rs.h3lp.model.helprequestutils.LocalEmergencyCaller
import com.github.h3lp3rs.h3lp.model.locationmanager.LocationHelper
import com.github.h3lp3rs.h3lp.model.map.GoogleAPIHelper
import com.github.h3lp3rs.h3lp.model.notifications.EmergencyListener.activateListeners
import com.github.h3lp3rs.h3lp.model.storage.Storages
import com.github.h3lp3rs.h3lp.view.firstaid.EXTRA_FIRST_AID
import com.github.h3lp3rs.h3lp.view.firstaid.GeneralFirstAidActivity
import com.github.h3lp3rs.h3lp.view.map.MapsFragment
import com.github.h3lp3rs.h3lp.view.messaging.RecentMessagesActivity
import com.github.h3lp3rs.h3lp.view.utils.ActivityUtils.goToMainPage
import com.google.android.gms.maps.MapsInitializer.initialize
import kotlinx.android.synthetic.main.activity_await_help.*

/**
 * Activity during which the user waits for help from other user.
 */
class AwaitHelpActivity : AppCompatActivity() {

    private val locationHelper = LocationHelper()
    private val helpersId = ArrayList<String>()

    private lateinit var mapsFragment: MapsFragment
    private lateinit var apiHelper: GoogleAPIHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_await_help)
        initialize(applicationContext)

        mapsFragment = supportFragmentManager.findFragmentById(R.id.map) as MapsFragment
        apiHelper =
            GoogleAPIHelper(resources.getString(R.string.google_maps_key), applicationContext)

        locationHelper.updateCoordinates(this)

        // Bundle can't be null
        val bundle = intent.extras!!

        locationHelper.requireAndHandleCoordinates(this) { location ->
            val latitude = location.latitude
            val longitude = location.longitude

            // If we did not call emergency services already, show a pop_up
            if (!bundle.getBoolean(EXTRA_CALLED_EMERGENCIES)) {
                showEmergencyCallPopup(latitude, longitude)
            }
            // Start listening to potential responses
            val emergencyId = bundle.getInt(EXTRA_EMERGENCY_KEY)
            val emergencyDb = databaseOf(EMERGENCIES, applicationContext)
            emergencyDb.addListener(emergencyId.toString(), EmergencyInformation::class.java) {
                val helpers = it.helpers
                for (h in helpers) {
                    foundHelperPerson(h.uid, h.latitude, h.longitude, emergencyId.toString())
                }
            }
            await_help_call_button.setOnClickListener { emergencyCall(latitude, longitude) }

            // Initially the contact helpers is hidden, only after a user responds to the request it
            // becomes visible.
            image_open_latest_messages.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.light_gray
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )
            image_open_latest_messages.isClickable = false
        }
    }

    /**
     * Displays a popup asking the user to call the emergency services if they
     * require help, stating that help from other users is not guaranteed.
     * @param latitude The user's current latitude
     * @param longitude The user's current longitude
     */
    private fun showEmergencyCallPopup(latitude: Double, longitude: Double) {
        val builder = AlertDialog.Builder(this)
        val emergencyCallPopup = layoutInflater.inflate(R.layout.call_emergencies_popup, null)

        builder.setCancelable(false)
        builder.setView(emergencyCallPopup)

        val alertDialog = builder.create()

        // Pass button
        emergencyCallPopup.findViewById<Button>(R.id.close_call_popup_button).setOnClickListener {
            alertDialog.cancel()
        }

        // Call button
        emergencyCallPopup.findViewById<Button>(R.id.open_call_popup_button).setOnClickListener {
            alertDialog.cancel()
            launchEmergencyCall(latitude, longitude)
        }

        alertDialog.show()
    }

    /**
     * Called when a someone responds to the help request. Replaces the waiting
     * bar by the number of helpers coming and makes the contact helpers button visible.
     * @param uid The uid of the helper
     * @param latitude Their latitude
     * @param longitude Their longitude
     * @param emergencyId The emergency id (used to set up the conversation with the helper)
     */
    private fun foundHelperPerson(
        uid: String,
        latitude: Double,
        longitude: Double,
        emergencyId: String
    ) {
        if (helpersId.contains(uid)) return

        helpersId.add(uid)
        showHelperPerson(uid, latitude, longitude)

        // Since the testing only checks for modifications on the UI thread, we force its execution
        // on the corresponding thread to enable testing
        runOnUiThread {

            incomingHelpersNumber.text =
                resources.getQuantityString(
                    R.plurals.number_of_helpers,
                    helpersId.size,
                    helpersId.size
                )

            if (helpersId.size <= 1) {

                findViewById<ProgressBar>(R.id.searchProgressBar).visibility = View.GONE
                findViewById<TextView>(R.id.progressBarText).visibility = View.GONE
                incomingHelpersNumber.visibility = View.VISIBLE

                // When the first user agrees to provide help, the user can contact
                // him via the chat feature.
                image_open_latest_messages.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.gray
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
                image_open_latest_messages.isClickable = true
                image_open_latest_messages.setOnClickListener {
                    goToRecentMessagesActivity(
                        emergencyId
                    )
                }
            }
        }
    }

    /**
     * Adds a marker on the map representing the position of someone coming to help.
     * @param uid The uid of the helper
     * @param latitude The helper's current latitude
     * @param longitude The helper's current longitude
     */
    private fun showHelperPerson(uid: String, latitude: Double, longitude: Double) {
        val name = resources.getString(R.string.helper_marker_desc) + uid

        // Since the testing only checks for modifications on the UI thread, we force its execution
        // on the corresponding thread to enable testing
        runOnUiThread { mapsFragment.addMarker(latitude, longitude, name) }
    }

    /**
     * Called when the user presses the emergency call button. Opens a pop-up
     * asking the user to choose whether they want to call local emergency
     * services or their emergency contact, and dials the correct number
     * @param latitude The helper's current latitude
     * @param longitude The helper's current longitude
     */
    private fun emergencyCall(latitude: Double, longitude: Double) {
        val medicalInfo = Storages.storageOf(Storages.MEDICAL_INFO, applicationContext)
            .getObjectOrDefault(
                getString(R.string.medical_info_key),
                MedicalInformation::class.java,
                null
            )

        if (medicalInfo != null) {
            val builder = AlertDialog.Builder(this)
            val emergencyCallPopup = layoutInflater.inflate(R.layout.emergency_call_options, null)

            builder.setCancelable(false)
            builder.setView(emergencyCallPopup)

            val alertDialog = builder.create()

            // Ambulance button
            emergencyCallPopup.findViewById<ImageButton>(R.id.ambulance_call_button)
                .setOnClickListener {
                    // In case the getCurrentLocation failed (for example if the location services aren't
                    // activated) currentLocation is still null and the returned phone number will be the
                    // default emergency phone number
                    alertDialog.cancel()
                    launchEmergencyCall(latitude, longitude)
                }

            // Contact button
            emergencyCallPopup.findViewById<ImageButton>(R.id.contact_call_button)
                .setOnClickListener {
                    alertDialog.cancel()

                    val dial = "tel:${medicalInfo.emergencyContactNumber}"
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
                }
            alertDialog.show()
        } else {
            launchEmergencyCall(latitude, longitude)
        }

    }

    /**
     * Launches a the phone app with the local emergency number dialed
     * @param latitude The helper's current latitude
     * @param longitude The helper's current longitude
     */
    private fun launchEmergencyCall(latitude: Double, longitude: Double) {
        val emergencyNumber =
            LocalEmergencyCaller.getLocalEmergencyNumber(
                longitude,
                latitude,
                this
            )

        val dial = "tel:$emergencyNumber"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
    }

    /**
     * Called when a tutorial button is pressed to redirect to the correct activity
     */
    fun goToTutorial(view: View) {

        val intent = Intent(this, GeneralFirstAidActivity::class.java).apply {
            when (view.id) {
                R.id.heart_attack_tuto_button -> putExtra(EXTRA_FIRST_AID, HEART_ATTACK)
                R.id.epipen_tuto_button -> putExtra(EXTRA_FIRST_AID, ALLERGY)
                R.id.aed_tuto_button -> putExtra(EXTRA_FIRST_AID, AED)
                R.id.asthma_tuto_button -> putExtra(EXTRA_FIRST_AID, ASTHMA)
            }
        }
        startActivity(intent)
    }

    /**
     * Launches the RecentMessagesActivity with the given emergency key to display the messaging
     * screen
     * @param emergencyId The emergency id where the conversations are stored (see the communication
     * protocol)
     */
    private fun goToRecentMessagesActivity(emergencyId: String) {
        val intent = Intent(this, RecentMessagesActivity::class.java)
        intent.putExtra(EXTRA_EMERGENCY_KEY, emergencyId)
        startActivity(intent)
    }

    /**
     * Cancels the search on the Database and goes back to MainActivity
     */
    fun cancelHelpSearch(view: View) {
        val bundle = this.intent.extras
        if (bundle == null) {
            goToMainPage()
            return
        }

        val emergencyId = bundle.getInt(EXTRA_EMERGENCY_KEY)
        databaseOf(EMERGENCIES, applicationContext).delete(emergencyId.toString())
        // Re-listen to other emergencies
        activateListeners(applicationContext)
        // Delete the helpee's id
        databaseOf(CONVERSATION_IDS, applicationContext).delete(emergencyId.toString())
        // Redirect user to the main page after he cancels his emergency
        goToMainPage()
    }
}