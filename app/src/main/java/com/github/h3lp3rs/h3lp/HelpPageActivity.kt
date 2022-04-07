package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.databinding.ActivityInfoOnHelpTaskBinding
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.util.GDurationJSONParser
import com.github.h3lp3rs.h3lp.util.GPathJSONParser
import com.github.h3lp3rs.h3lp.util.JSONParserInterface
import com.google.android.gms.maps.MapsInitializer
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Activity used to display information about a person in need, their location, the path to them,
 * the time to get there, and what help they need
 * The user can then accept to help them (or not)
 */
class HelpPageActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityInfoOnHelpTaskBinding
    //TODO : currently, the destination is hardcoded, this will change with the task allowing
    // nearby helpers to go and help people in need (in which case the destination will be the
    // location of the user in need)
    private val destinationLat = 46.51902895030102
    private val destinationLong = 6.567597089508282
    private var currentLong: Double = 0.0
    private var currentLat: Double = 0.0

    // TODO : this is only for testing purposes, it will be removed when use the code to retrieve
    //  actual helping requests
    // helpRequired contains strings for each medication / specific help required by the user in
    // need e.g. Epipen, CPR
    private val helpRequired: List<String> = listOf("Epipen", "CPR")
    private lateinit var apiHelper: GoogleAPIHelper

    // Map fragment displayed
    private lateinit var mapsFragment: MapsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext)

        // Displaying the activity layout
        binding = ActivityInfoOnHelpTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the map fragment
        mapsFragment = supportFragmentManager
            .findFragmentById(R.id.map) as MapsFragment

        // Initializes the API helper
        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key))

        // Initialize the current user's location
        setupLocation()

        // Displays the path to the user in need on the map fragment and, when the path has been
        // retrieved (through a google directions API request), computes and displays the time to
        // get to the user in need
        apiHelper.displayPath(
            currentLat,
            currentLong,
            destinationLat,
            destinationLong,
            mapsFragment,
            { mapData: String? -> displayPathDuration(mapData) }
        )
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
        val helpRequiredText: TextView = findViewById(R.id.helpRequired)

        val stringBuilder: StringBuilder = StringBuilder()
        // helpRequired contains strings corresponding to any medication / specific help the person
        // in need requires
        for (med in helpRequired) {
            stringBuilder.append("- ")
            stringBuilder.append(med)
            stringBuilder.appendLine()
        }
        helpRequiredText.text = stringBuilder.toString()
    }



    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    fun goToMainPage(view: View) {
        goToActivity(MainPageActivity::class.java)
    }

}