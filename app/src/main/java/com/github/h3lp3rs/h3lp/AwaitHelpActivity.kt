package com.github.h3lp3rs.h3lp

import LocationHelper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
<<<<<<< HEAD
import com.github.h3lp3rs.h3lp.database.Databases.*
=======
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases.CONVERSATION_IDS
import com.github.h3lp3rs.h3lp.database.Databases.Companion.activateHelpListeners
>>>>>>> main
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation
import com.github.h3lp3rs.h3lp.firstaid.AedActivity
import com.github.h3lp3rs.h3lp.firstaid.AllergyActivity
import com.github.h3lp3rs.h3lp.firstaid.AsthmaActivity
import com.github.h3lp3rs.h3lp.firstaid.HeartAttackActivity
<<<<<<< HEAD
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.messaging.LatestMessagesActivity
import com.github.h3lp3rs.h3lp.notification.EmergencyListener
=======
import com.github.h3lp3rs.h3lp.messaging.RecentMessagesActivity
>>>>>>> main
import com.github.h3lp3rs.h3lp.storage.Storages
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_await_help.*

/**
 * Activity during which the user waits for help from other user.
 */
class AwaitHelpActivity : AppCompatActivity() {
<<<<<<< HEAD
    private val helpersId = ArrayList<String>()

    private lateinit var mapsFragment: MapsFragment
    private lateinit var apiHelper: GoogleAPIHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
=======

    private val locationHelper = LocationHelper()

    private val askedMeds : List<String> = listOf()
    private var helpersNumbers = 0
    private val helpersId = ArrayList<String>()

    private lateinit var mapsFragment : MapsFragment
    private lateinit var apiHelper : GoogleAPIHelper
    private var helpeeId : String? = null

    override fun onCreate(savedInstanceState : Bundle?) {
>>>>>>> main
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_await_help)


        mapsFragment = supportFragmentManager.findFragmentById(R.id.mapAwaitHelp) as MapsFragment
        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key))

<<<<<<< HEAD
        // Initialize the current user's location
        val location = getLocation()
        if (location == null) {
            // In case the permission to access the location is missing
            goToActivity(MainPageActivity::class.java)
            return
        }
        val (latitude, longitude) = location

        // Bundle can't be null
        val bundle = intent.extras!!

        // If we did not call emergency services already, show a pop_up
        if(!bundle.getBoolean(EXTRA_CALLED_EMERGENCIES)){
            showEmergencyCallPopup(latitude, longitude)
        }
        // Start listening to potential responses
        val emergencyId = bundle.getInt(EXTRA_EMERGENCY_KEY)
        val emergencyDb = databaseOf(EMERGENCIES)
        emergencyDb.addListener(emergencyId.toString(), EmergencyInformation::class.java) {
            val helpers = it.helpers
            for(h in helpers) {
                foundHelperPerson(h.uid, h.latitude, h.longitude, emergencyId.toString())
=======
        locationHelper.updateCoordinates(this)

        val bundle = intent.extras
        if (bundle != null) {
            helpeeId = bundle.getString(EXTRA_HELPEE_ID)
            askedMeds.plus(bundle.getStringArrayList(EXTRA_NEEDED_MEDICATION))

            // If we did not call emergency services already, show a pop_up
            if (!bundle.getBoolean(EXTRA_CALLED_EMERGENCIES)) {
                showEmergencyCallPopup()
            }
            // Start listening to potential responses
            val emergencyId = bundle.getInt(EXTRA_EMERGENCY_KEY)
            val emergencyDb = databaseOf(EMERGENCIES)
            emergencyDb.addListener(emergencyId.toString(), EmergencyInformation::class.java) {
                val helpers = it.helpers
                for (h in helpers) {
                    foundHelperPerson(h.uid, h.latitude, h.longitude)
                }
>>>>>>> main
            }
        }

        await_help_call_button.setOnClickListener {emergencyCall(latitude, longitude)}


        // Initially the contact helpers is hidden, only after a user responds to the request it
        // becomes visible.
        constraint_layout_contact_helpers.visibility = View.INVISIBLE
    }

    /**
     * Displays a popup asking the user to call the emergency services if they
     * require help, stating that help from other users is not guaranteed.
     * @param latitude The user's current latitude
     * @param longitude The user's current longitude
     */
<<<<<<< HEAD
    private fun showEmergencyCallPopup(latitude: Double, longitude: Double){
=======
    private fun showEmergencyCallPopup() {
>>>>>>> main
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
<<<<<<< HEAD
    private fun foundHelperPerson(uid: String, latitude: Double, longitude: Double, emergencyId: String){
        if(helpersId.contains(uid)) return
=======
    private fun foundHelperPerson(uid : String, latitude : Double, longitude : Double) {
        if (helpersId.contains(uid)) return
>>>>>>> main

        helpersId.add(uid)
        showHelperPerson(uid, latitude, longitude)

        // Since the testing only checks for modifications on the UI thread, we force its execution
        // on the corresponding thread to enable testing
        runOnUiThread {
            findViewById<ProgressBar>(R.id.searchProgressBar).visibility = View.GONE
            findViewById<TextView>(R.id.progressBarText).visibility = View.GONE

            val helpersText = findViewById<TextView>(R.id.incomingHelpersNumber)
<<<<<<< HEAD
            if (helpersId.size > 1) {
                helpersText.text = String.format(getString(R.string.many_people_help), helpersId.size)
=======
            if (++helpersNumbers > 1) {
                helpersText.text =
                    String.format(getString(R.string.many_people_help), helpersNumbers)
>>>>>>> main
            } else {
                helpersText.text = getString(R.string.one_person_help)
                // When the first user agrees to provide help, the user can contact
                // him via the chat feature.
                constraint_layout_contact_helpers.visibility = View.VISIBLE
<<<<<<< HEAD
                image_open_latest_messages.setOnClickListener{ goToLatestMessagesActivity(emergencyId) }
=======
                image_open_latest_messages.setOnClickListener { goToRecentMessagesActivity() }
>>>>>>> main
            }
            helpersText.visibility = View.VISIBLE
        }
    }

    /**
     * Adds a marker on the map representing the position of someone coming to
     * help.
     * @param uid The uid of the helper
     * @param latitude The helper's current latitude
     * @param longitude The helper's current longitude
     */
<<<<<<< HEAD
    private fun showHelperPerson(uid: String, latitude: Double, longitude: Double){
=======
    private fun showHelperPerson(uid : String, latitude : Double, longitude : Double) {
        val latLng = LatLng(latitude, longitude)
>>>>>>> main
        val name = resources.getString(R.string.helper_marker_desc) + uid

        // Since the testing only checks for modifications on the UI thread, we force its execution
        // on the corresponding thread to enable testing
        runOnUiThread { mapsFragment.addMarker(latitude, longitude, name) }
    }

<<<<<<< HEAD
    /**
     * Initializes the user's current location or returns to the main page in case a mistake occured
     * during the location information retrieval
     * @return The user's current location in the format Pair(latitude, longitude)
     */
    private fun getLocation(): Pair<Double, Double>? {
        val currentLocation = GeneralLocationManager.get().getCurrentLocation(this)
        if (currentLocation != null) {
            return Pair(currentLocation.latitude, currentLocation.longitude)
        }
        return null
    }

=======
>>>>>>> main
    /**
     * Called when the user presses the emergency call button. Opens a pop-up
     * asking the user to choose whether they want to call local emergency
     * services or their emergency contact, and dials the correct number
     * @param latitude The helper's current latitude
     * @param longitude The helper's current longitude
     */
    @RequiresApi(Build.VERSION_CODES.O)
<<<<<<< HEAD
    private fun emergencyCall(latitude: Double, longitude: Double) {
        val medicalInfo = Storages.storageOf(Storages.MEDICAL_INFO)
            .getObjectOrDefault(getString(R.string.medical_info_key), MedicalInformation::class.java, null)
=======
    fun emergencyCall(view : View) {
        val medicalInfo = Storages.storageOf(Storages.MEDICAL_INFO).getObjectOrDefault(
                getString(R.string.medical_info_key),
                MedicalInformation::class.java,
                null
            )
>>>>>>> main

        if (medicalInfo != null) {
            val builder = AlertDialog.Builder(this)
            val emergencyCallPopup = layoutInflater.inflate(R.layout.emergency_call_options, null)

            builder.setCancelable(false)
            builder.setView(emergencyCallPopup)

            val alertDialog = builder.create()

<<<<<<< HEAD
            // Ambulance button
            emergencyCallPopup.findViewById<ImageButton>(R.id.ambulance_call_button).setOnClickListener {
                // In case the getCurrentLocation failed (for example if the location services aren't
                // activated) currentLocation is still null and the returned phone number will be the
                // default emergency phone number
                alertDialog.cancel()
                launchEmergencyCall(latitude, longitude)
            }

            // Contact button
            emergencyCallPopup.findViewById<ImageButton>(R.id.contact_call_button).setOnClickListener {
                alertDialog.cancel()
=======
            // ambulance button
            emergencyCallPopup.findViewById<ImageButton>(R.id.ambulance_call_button)
                .setOnClickListener {
                    // In case the getCurrentLocation failed (for example if the location services aren't
                    // activated, currentLocation is still null and the returned phone number will be the
                    // default emergency phone number
                    alertDialog.cancel()
                    launchEmergencyCall()
                }

            // contact button
            emergencyCallPopup.findViewById<ImageButton>(R.id.contact_call_button)
                .setOnClickListener {
                    alertDialog.cancel()
>>>>>>> main

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
<<<<<<< HEAD
    private fun launchEmergencyCall(latitude: Double, longitude: Double) {
        val emergencyNumber =
            LocalEmergencyCaller.getLocalEmergencyNumber(
                longitude,
                latitude,
                this
            )
=======
    private fun launchEmergencyCall() {
        val emergencyNumber = LocalEmergencyCaller.getLocalEmergencyNumber(
            locationHelper.getUserLongitude(), locationHelper.getUserLatitude(), this
        )
>>>>>>> main

        val dial = "tel:$emergencyNumber"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
    }

    /**
     * Called when a tutorial button is pressed to redirect to the correct activity
     */
<<<<<<< HEAD
    fun goToTutorial(view: View) {
=======
    fun goToActivity(view : View) {
>>>>>>> main
        val intent = when (view.id) {
            R.id.heart_attack_tuto_button -> Intent(this, HeartAttackActivity::class.java)
            R.id.epipen_tuto_button -> Intent(this, AllergyActivity::class.java)
            R.id.aed_tuto_button -> Intent(this, AedActivity::class.java)
            R.id.asthma_tuto_button -> Intent(this, AsthmaActivity::class.java)
            else -> Intent(this, MainPageActivity::class.java)
        }
        startActivity(intent)
    }

<<<<<<< HEAD

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    private fun goToLatestMessagesActivity(emergencyId: String){
        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.putExtra(EXTRA_EMERGENCY_KEY, emergencyId)
=======
    private fun goToRecentMessagesActivity() {
        val intent = Intent(this, RecentMessagesActivity::class.java)
        intent.putExtra(EXTRA_HELPEE_ID, helpeeId)
>>>>>>> main
        startActivity(intent)
    }

    /**
     * Cancels the search on the Database and goes back to MainActivity
     */
    fun cancelHelpSearch(view : View) {
        val bundle = this.intent.extras
        if (bundle == null) {
            startActivity(Intent(this, MainPageActivity::class.java))
            return
        }
        val emergencyId = bundle.getInt(EXTRA_EMERGENCY_KEY)
        databaseOf(EMERGENCIES).delete(emergencyId.toString())
        // Re-listen to other emergencies
<<<<<<< HEAD
        EmergencyListener.activateListeners()
        // TODO the action on the DB is not yet defined
        // TODO should include deleting the conversation from the db
=======
        activateHelpListeners()
        // Delete the helpee's id
        helpeeId?.let { databaseOf(CONVERSATION_IDS).delete(it) }
        // Redirect user to the main page after he cancels his emergency
>>>>>>> main
        startActivity(Intent(this, MainPageActivity::class.java))
    }
}