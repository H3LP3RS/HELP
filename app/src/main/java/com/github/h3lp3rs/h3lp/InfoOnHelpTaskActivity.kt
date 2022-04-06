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

class InfoOnHelpTaskActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    //TODO : currently, the destination is hardcoded, this will change with the task allowing
    // nearby helpers to go and help people in need (in which case the destination will be the
    // location of the user in need)
    private lateinit var binding: ActivityInfoOnHelpTaskBinding
    private val destinationLat = 46.51902895030102
    private val destinationLong = 6.567597089508282
    private var currentLong: Double = 0.0
    private var currentLat: Double = 0.0

    // TODO : this is only for testing purposes, it will be removed when use the code to retrieve
    //  actual helping requests
    private val helpRequired: List<String> = listOf("Epipen", "CPR")
    private lateinit var apiHelper: GoogleAPIHelper

    private lateinit var mapsFragment: MapsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext)

        binding = ActivityInfoOnHelpTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready
        // to be used.
        mapsFragment = supportFragmentManager
            .findFragmentById(R.id.map) as MapsFragment


        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key))

        setupLocation()
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



    private fun setupLocation() { //TODO : possibly create a superclass since this code is copied from nearbyUtilitiesActivity
        val currentLocation = GeneralLocationManager.get().getCurrentLocation(this)
        if (currentLocation != null) {
            currentLat = currentLocation.latitude
            currentLong = currentLocation.longitude
        } else {
            // In case the permission to access the location is missing
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayPathDuration(pathData: String?) {
        val duration = pathData?.let { apiHelper.parseTask(it, GDurationJSONParser) }

        val walkingTimeInfo: TextView = findViewById(R.id.timeToPersonInNeed)

//        if (userLocation != null) { //TODO : readd
        walkingTimeInfo.text = String.format("- %s", duration)
//        }

    }


    private fun displayRequiredMeds() {
        val helpRequiredText: TextView = findViewById(R.id.helpRequired)
        val stringBuilder: StringBuilder = StringBuilder()
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