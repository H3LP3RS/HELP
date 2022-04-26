package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.activateHelpListeners
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.firstaid.AedActivity
import com.github.h3lp3rs.h3lp.firstaid.AllergyActivity
import com.github.h3lp3rs.h3lp.firstaid.AsthmaActivity
import com.github.h3lp3rs.h3lp.firstaid.HeartAttackActivity
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Activity during which the user waits for help from other user.
 */
class AwaitHelpActivity : AppCompatActivity() {

    private var currentLong: Double = 0.0
    private var currentLat: Double = 0.0
    private val askedMeds: List<String> = listOf()
    private var helpersNumbers = 0
    private val helpersId = ArrayList<String>()

    private lateinit var mapsFragment: MapsFragment
    private lateinit var apiHelper: GoogleAPIHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_await_help)

        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key))
        setupLocation()

        val bundle = this.intent.extras
        if(bundle != null) {
            askedMeds.plus(bundle.getStringArrayList(EXTRA_NEEDED_MEDICATION))

            // If we did not call emergency services already, show a pop_up
            if(!bundle.getBoolean(EXTRA_CALLED_EMERGENCIES)){
                showEmergencyCallPopup()
            }
            // Start listening to potential responses
            val emergencyId = bundle.getInt(EXTRA_EMERGENCY_KEY)
            val emergencyDb = databaseOf(EMERGENCIES)
            emergencyDb.addListener(emergencyId.toString(), EmergencyInformation::class.java) {
                val helpers = it.helpers
                for(h in helpers) {
                    foundHelperPerson(h.uid, h.latitude, h.longitude)
                }
            }
        } else {
            showEmergencyCallPopup()
        }
    }

    /**
     * Displays a popup asking the user to call the emergency services if they
     * require help, stating that help from other users is not guaranteed.
     */
    private fun showEmergencyCallPopup(){
        val builder = AlertDialog.Builder(this)
        val emergencyCallPopup = layoutInflater.inflate(R.layout.call_emergencies_popup, null)

        builder.setCancelable(false)
        builder.setView(emergencyCallPopup)

        val alertDialog = builder.create()

        // pass button
        emergencyCallPopup.findViewById<Button>(R.id.close_call_popup_button).setOnClickListener {
            alertDialog.cancel()
        }

        // call button
        emergencyCallPopup.findViewById<Button>(R.id.open_call_popup_button).setOnClickListener {
            findViewById<ImageView>(R.id.await_help_call_button).callOnClick()
            alertDialog.cancel()
        }

        alertDialog.show()
    }

    /**
     * Called when a someone responds to the help request. Replaces the waiting
     * bar by the number of helpers coming.
     */
    private fun foundHelperPerson(uid: String, latitude: Double, longitude: Double){
        if(helpersId.contains(uid)) return

        helpersId.add(uid)
        showHelperPerson(uid, latitude, longitude)

        findViewById<ProgressBar>(R.id.searchProgressBar).visibility = View.GONE
        findViewById<TextView>(R.id.progressBarText).visibility = View.GONE

        val helpersText = findViewById<TextView>(R.id.incomingHelpersNumber)
        if (++helpersNumbers > 1) {
            helpersText.text = String.format( "%d people are coming to help you", helpersNumbers)
        } else {
            helpersText.text = String.format( "%d person is coming to help you", helpersNumbers)
        }
        helpersText.visibility = View.VISIBLE
    }

    /**
     * Adds a marker on the map representing the position of someone coming to
     * help.
     */
    private fun showHelperPerson(uid: String, latitude: Double, longitude: Double){
        val latLng = LatLng(latitude, longitude)
        val name = resources.getString(R.string.helper_marker_desc) + uid

        val options = MarkerOptions()
        options.position(latLng)
        options.title(name)
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.helper_marker))

        //mapsFragment.addMarker(options) TODO: Fragment not working
    }

    private fun setupLocation() {
        val currentLocation = GeneralLocationManager.get().getCurrentLocation(this)
        if (currentLocation != null) {
            currentLat = currentLocation.latitude
            currentLong = currentLocation.longitude
        } else {
            // In case the permission to access the location is missing
            goToActivity(MainPageActivity::class.java)
        }
    }

    /**
     *  Opens the phone call app with the emergency number from the country the
     *  user is currently in dialed. Called either from the popup or the button.
     */
    fun emergencyCall(view: View) {
        val emergencyNumber =
            LocalEmergencyCaller.getLocalEmergencyNumber(
                currentLong,
                currentLat,
                this
            )

        val dial = "tel:$emergencyNumber"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
    }

    /**
     * Auxiliary function to redirect the buttons presses to a tutorial page
     */
    private fun goToActivity(activityName: Class<*>?) {
        val intent = Intent(this, activityName)
        startActivity(intent)
    }

    /** Called when the user taps the asthma attack button */
    fun goToAsthmaActivity(view: View) {
        goToActivity(AsthmaActivity::class.java)
    }

    /** Called when the user taps the defibrillator button */
    fun goToAedActivity(view: View) {
        goToActivity(AedActivity::class.java)
    }

    /** Called when the user taps the allergies button */
    fun goToAllergyActivity(view: View) {
        goToActivity(AllergyActivity::class.java)
    }

    /** Called when the user taps the heart attack button */
    fun goToHeartAttackActivity(view: View) {
        goToActivity(HeartAttackActivity::class.java)
    }

    /**
     * Cancels the search on the Database and goes back to MainActivity
     */
    fun cancelHelpSearch(view: View){
        val bundle = this.intent.extras
        if(bundle == null) {
            goToActivity(MainPageActivity::class.java)
            return
        }
        val emergencyId = bundle.getInt(EXTRA_EMERGENCY_KEY)
        databaseOf(EMERGENCIES).delete(emergencyId.toString())
        // Re-listen to other emergencies
        activateHelpListeners()
        goToActivity(MainPageActivity::class.java)
    }
}