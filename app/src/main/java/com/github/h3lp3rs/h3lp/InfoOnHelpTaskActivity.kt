package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private var pathData: String? = null
    // TODO : this is only for testing purposes, it will be removed when use the code to retrieve
    //  actual helping requests
    private val medsRequired: List<String> =

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



        setupLocation()
        setupMap()
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

    private fun setupMap() {
        val url =
            NearbyUtilitiesActivity.DIRECTIONS_URL + "?destination=" + destinationLat + "," + destinationLong +
                    "&mode=${NearbyUtilitiesActivity.WALKING}" +
                    "&origin=" + currentLat + "," + currentLong +
                    "&key=" + resources.getString(R.string.google_maps_key)

        // Launches async routines to retrieve the path to the destination and display it on the map
        CoroutineScope(Dispatchers.Main).launch {
            pathData = withContext(Dispatchers.IO) { downloadUrl(url) }
            pathData?.let { Log.i("GPath", it) }
            getPath()
            displayPathDuration()
        }
    }


    /**
     * Retrieves the shortest path to the destination and displays it on the map
     */
    private fun getPath() {
        val path = pathData?.let { parseTask(it, GPathJSONParser) }
        path?.let {
            mapsFragment.showPolyline(it)
            mapsFragment.addMarker(
                destinationLat, destinationLong,
                NearbyUtilitiesActivity.END_POINT_NAME
            )
        }
    }

    private fun displayPathDuration() {
        val duration = pathData?.let { parseTask(it, GDurationJSONParser) }

        val walkingTimeInfo: TextView = findViewById(R.id.timeToPersonInNeed)

//        if (userLocation != null) { //TODO : readd
        walkingTimeInfo.text = String.format("- %s", duration)
//        }

    }

    private fun downloadUrl(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()

        val stream = connection.inputStream
        val reader = BufferedReader(InputStreamReader(stream))

        val builder = StringBuilder()

        reader.use { r ->
            var line = r.readLine()
            while (line != null) {
                builder.append(line)
                line = r.readLine()
            }
        }

        return builder.toString()
    }

    private fun <T> parseTask(data: String, parser: JSONParserInterface<T>): T {
        return parser.parseResult(JSONObject(data))
    }
}