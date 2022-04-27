package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.databinding.ActivityHelpPageBinding
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.Helper
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.util.GDurationJSONParser
import com.google.android.gms.maps.MapsInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

const val EXTRA_HELP_REQUIRED_PARAMETERS = "help_page_required_key"
const val EXTRA_DESTINATION_LAT = "help_page_destination_lat"
const val EXTRA_DESTINATION_LONG = "help_page_destination_long"

/**
 * Activity used to display information about a person in need, their location, the path to them,
 * the time to get there, and what help they need
 * The user can then accept to help them (or not)
 */
class HelpPageActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityHelpPageBinding

    private var destinationLat = 46.519
    private var destinationLong = 6.667
    private var currentLong: Double = 0.0
    private var currentLat: Double = 0.0

    // helpRequired contains strings for each medication / specific help required by the user in
    // need e.g. Epipen, CPR
    private var helpRequired: List<String>? = null
    private lateinit var apiHelper: GoogleAPIHelper
    private var helpId: String? = null

    // Map fragment displayed
    private lateinit var mapsFragment: MapsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext)

        // Displaying the activity layout
        binding = ActivityHelpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = this.intent.extras
        helpId = bundle?.getString(EXTRA_EMERGENCY_KEY) ?: helpId
        helpRequired = bundle?.getStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS) ?: helpRequired
        destinationLat = bundle?.getDouble(EXTRA_DESTINATION_LAT) ?: destinationLat
        destinationLong = bundle?.getDouble(EXTRA_DESTINATION_LONG) ?: destinationLong

        // Obtain the map fragment
        mapsFragment = supportFragmentManager
            .findFragmentById(R.id.mapHelpPage) as MapsFragment

        // Initializes the API helper
        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key))

        // Initialize the current user's location
        setupLocation()

        // Displays the path to the user in need on the map fragment and, when the path has been
        // retrieved (through a google directions API request), computes and displays the time to
        // get to the user in need
        apiHelper.displayWalkingPath(
            currentLat,
            currentLong,
            destinationLat,
            destinationLong,
            mapsFragment
        ) { mapData: String? -> displayPathDuration(mapData) }
        displayRequiredMeds()
    }


    /**
     * Initializes the user's current location or returns to the main page in case a mistake occured
     * during the location information retrieval
     */
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
     * Displays the entire duration of a path from one point to another
     * @param pathData The string returned by a call to the Directions API
     */
    private fun displayPathDuration(pathData: String?) {
        val duration = pathData?.let { apiHelper.parseTask(it, GDurationJSONParser) }

        val walkingTimeInfo: TextView = findViewById(R.id.timeToPersonInNeed)
        walkingTimeInfo.text = String.format("- %s", duration)
    }

    /**
     * Displays the medication / help required by the user in need
     */
    private fun displayRequiredMeds() {
        helpRequired?.let { medication ->
            val helpRequiredText: TextView = findViewById(R.id.helpRequired)

            val stringBuilder: StringBuilder = StringBuilder()
            // helpRequired contains strings corresponding to any medication / specific help the person
            // in need requires
            for (med in medication) {
                stringBuilder.append("- ")
                stringBuilder.append(med)
                stringBuilder.appendLine()
            }
            helpRequiredText.text = stringBuilder.toString()
        }
    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    /**
     * Triggered when clicked on the cross
     */
    fun refuseHelp(view: View) {
        goToMainPage()
    }

    /**
     * Triggered when the helper accepts to help
     * @param view The view of the button
     */
    fun acceptHelp(view: View) {
        if(helpId == null) {
            goToMainPage()
            return
        }
        databaseOf(EMERGENCIES).getObject(helpId!!, EmergencyInformation::class.java).thenApply {
            // Add the helper to the list of helpers
            val me = Helper(userUid!!, currentLat, currentLong)
            val helpers = ArrayList<Helper>(it.helpers)
            if(!helpers.contains(me)) {
                helpers.add(me)
            }
            // Stop listening to other emergencies
            databaseOf(NEW_EMERGENCIES).clearAllListeners()
            // TODO: Here we can potentially periodically update the GPS coordinates
            // Update the value to notify that we are coming
            databaseOf(EMERGENCIES).setObject(helpId!!, EmergencyInformation::class.java, it.copy(helpers = helpers))
        }.exceptionally { goToMainPage() } // Expired
    }

    private fun goToMainPage() {
        goToActivity(MainPageActivity::class.java)
    }
}