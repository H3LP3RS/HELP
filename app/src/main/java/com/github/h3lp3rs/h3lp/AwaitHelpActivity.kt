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
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
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

    private lateinit var mapsFragment: MapsFragment
    private lateinit var apiHelper: GoogleAPIHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_await_help)

        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key))

        setupLocation()


        // Demo code
//        val db = databaseOf(Databases.NEW_EMERGENCIES)
//        db.clearListeners(getString(R.string.ventolin_db_key)) // Avoid being autocalled (for now, we'll need a ds for that later)
//        db.setString(getString(R.string.ventolin_db_key), getString(R.string.help))


        val bundle = this.intent.extras
        if(bundle != null) {
            askedMeds.plus(bundle.getStringArrayList(EXTRA_NEEDED_MEDICATION))

            // If we did not call emergency services already, show a pop_up
            if(!bundle.getBoolean(EXTRA_CALLED_EMERGENCIES)){
                showEmergencyCallPopup()
            }
        } else {
            showEmergencyCallPopup()
        }

        //TODO add listener on database so that we replace the loading bar by the number of
        // users coming to help and their position
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
    private fun foundHelperPerson(latitude: Double, longitude: Double){
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
    private fun showHelperPerson(latitude: Double, longitude: Double){
        val latLng = LatLng(latitude, longitude)
        val name = resources.getString(R.string.helper_marker_desc)

        val options = MarkerOptions()
        options.position(latLng)
        options.title(name)
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.helper_marker))

        mapsFragment.addMarker(options)
    }

    /**
     * @see HelpPageActivity.setupLocation
     */
    private fun setupLocation() {
        val currentLocation = GeneralLocationManager.get().getCurrentLocation(this)
        if (currentLocation != null) {
            currentLat = currentLocation.latitude
            currentLong = currentLocation.longitude
        } else {
            // In case the permission to access the location is missing
            //goToActivity(MainPageActivity::class.java)
        }
    }

    /**
     *  Opens the phone call app with the emergency number from the country the
     *  user is currently in dialed. Called either from the popup or the button.
     */
    fun launchEmergencyCall(view: View) {
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
     * Called when a button is pressed on the layout so that the user is
     * redirected to the right activity
     */
    fun goToActivity(view: View) {
        val intent = when (view.id){
            R.id.heart_attack_tuto_button ->
                Intent(this, HeartAttackActivity::class.java)
            R.id.epipen_tuto_button ->
                Intent(this, AllergyActivity::class.java)
            R.id.aed_tuto_button ->
                Intent(this, AedActivity::class.java)
            R.id.asthma_tuto_button ->
                Intent(this, AsthmaActivity::class.java)
            else -> Intent(this, MainPageActivity::class.java)
        }

        startActivity(intent)
    }
}