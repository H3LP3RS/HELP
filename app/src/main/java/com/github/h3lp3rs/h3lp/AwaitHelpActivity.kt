package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases.Companion.activateHelpListeners
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.EMERGENCIES
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.database.Databases.CONVERSATION_IDS
import com.github.h3lp3rs.h3lp.firstaid.AedActivity
import com.github.h3lp3rs.h3lp.firstaid.AllergyActivity
import com.github.h3lp3rs.h3lp.firstaid.AsthmaActivity
import com.github.h3lp3rs.h3lp.firstaid.HeartAttackActivity
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.messaging.LatestMessagesActivity
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_await_help.*

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
    private var helpeeId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_await_help)

        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key))
        setupLocation()

        val bundle = intent.extras
        if(bundle != null) {
            helpeeId = bundle.getString(EXTRA_HELPEE_ID)
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

        /*helpeeId?.let{
            databaseOf(CONVERSATION_IDS).addListListener(it, String::class.java)
            {list -> if (list.isNotEmpty()) foundHelperPerson(0.0, 0.0)}
        }*/

        // Initially the contact helpers is hidden, only after a user responds to the request it
        // becomes visible.
        constraint_layout_contact_helpers.visibility = View.INVISIBLE
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
     * bar by the number of helpers coming and makes the contact helpers button visible.
     * @param uid The uid of the helper
     * @param latitude Its latitude
     * @param longitude Its longitude
     */
    private fun foundHelperPerson(uid: String, latitude: Double, longitude: Double){
        if(helpersId.contains(uid)) return

        helpersId.add(uid)
        showHelperPerson(uid, latitude, longitude)

        runOnUiThread {
            findViewById<ProgressBar>(R.id.searchProgressBar).visibility = View.GONE
            findViewById<TextView>(R.id.progressBarText).visibility = View.GONE

            val helpersText = findViewById<TextView>(R.id.incomingHelpersNumber)
            if (++helpersNumbers > 1) {
                helpersText.text = String.format(getString(R.string.many_people_help), helpersNumbers)
            } else {
                helpersText.text = getString(R.string.one_person_help)
                // When the first user agrees to provide help, the user can contact
                // him via the chat feature.
                constraint_layout_contact_helpers.visibility = View.VISIBLE
                image_open_latest_messages.setOnClickListener{ goToLatestMessagesActivity() }
            }
            helpersText.visibility = View.VISIBLE
        }
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

    private fun goToLatestMessagesActivity(){
        val intent = Intent(this, LatestMessagesActivity::class.java)
        intent.putExtra(EXTRA_HELPEE_ID, helpeeId)
        startActivity(intent)
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
        // TODO the action on the DB is not yet defined
        // TODO should include deleting the conversation from the db
        goToActivity(MainPageActivity::class.java)
    }
}