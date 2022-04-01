package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColorStateList
import com.github.h3lp3rs.h3lp.databinding.ActivityNearbyUtilitiesBinding
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.util.AED_LOCATIONS_LAUSANNE
import com.github.h3lp3rs.h3lp.util.GPathJSONParser
import com.github.h3lp3rs.h3lp.util.GPlaceJSONParser
import com.github.h3lp3rs.h3lp.util.JSONParserInterface
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class NearbyUtilitiesActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityNearbyUtilitiesBinding
    private lateinit var mapsFragment: MapsFragment
    private var requestedUtility: String? = null
    private var currentLong: Double = 0.0
    private var currentLat: Double = 0.0

    private var showingHospitals = false
    private var showingPharmacies = false
    private var showingDefibrillators = false

    private lateinit var hospitalBackgroundLayout: LinearLayout
    private lateinit var pharmacyBackgroundLayout: LinearLayout
    private lateinit var defibrillatorsBackgroundLayout: LinearLayout

    private var uncheckedButtonColor: ColorStateList? = null
    private var checkedButtonColor: ColorStateList? = null

    // places and markers (key is the utility)
    private val requestedPlaces = HashMap<String, List<GooglePlace>>()

    //TODO : currently, the destination is hardcoded, this will change with the task allowing
    // nearby helpers to go and help people in need (in which case the destination will be the
    // location of the user in need)
    private val destinationLat = 46.51902895030102
    private val destinationLong = 6.567597089508282

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNearbyUtilitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedUtility = intent.getStringExtra(EXTRA_NEARBY_UTILITIES)

        // Retrieve elements from the UI and setup constants
        hospitalBackgroundLayout = findViewById(R.id.show_hospital_button_layout)
        defibrillatorsBackgroundLayout = findViewById(R.id.show_defibrillators_button_layout)
        pharmacyBackgroundLayout = findViewById(R.id.show_pharmacy_button_layout)

        checkedButtonColor = getColorStateList(this, R.color.select_meds_checked)
        uncheckedButtonColor = getColorStateList(this, R.color.select_meds_unchecked)


        setupLocation()
        setupSelectionButtons()
        setRequestedButton() // TODO : check that since it's called at creation (not when the map is ready necess), it doesn't cause a problem
        getPath() // TODO : find a way to call getPath when the map is ready, maybe use the callback ?

        // Obtain the SupportMapFragment and get notified when the map is ready
        // to be used.
        mapsFragment = supportFragmentManager
            .findFragmentById(R.id.map) as MapsFragment
    }

    private fun setupLocation() {
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


    /**
     * Creates listeners for the different buttons allowing the user to select
     * some utilities.
     */
    private fun setupSelectionButtons() {
        val hospitalButton = findViewById<ImageButton>(R.id.show_hospital_button)
        val defibrillatorsButton = findViewById<ImageButton>(R.id.show_defibrillators_button)
        val pharmacyButton = findViewById<ImageButton>(R.id.show_pharmacy_button)

        hospitalButton.setOnClickListener {
            if (!showingHospitals) {
                findNearbyUtilities(resources.getString(R.string.nearby_hospitals))

                hospitalBackgroundLayout.backgroundTintList = checkedButtonColor
                hospitalButton.background.alpha =
                    resources.getInteger(R.integer.selectionTransparency)

                showingHospitals = true
            } else {
                hospitalBackgroundLayout.backgroundTintList = uncheckedButtonColor
                hospitalButton.background.alpha = resources.getInteger(R.integer.noTransparency)
                mapsFragment.removeMarkers(resources.getString(R.string.nearby_hospitals))

                showingHospitals = false
            }
        }

        defibrillatorsButton.setOnClickListener {
            if (!showingDefibrillators) {
                findNearbyUtilities(resources.getString(R.string.nearby_defibrillators))

                defibrillatorsBackgroundLayout.backgroundTintList = checkedButtonColor
                defibrillatorsButton.background.alpha =
                    resources.getInteger(R.integer.selectionTransparency)

                showingDefibrillators = true
            } else {
                defibrillatorsBackgroundLayout.backgroundTintList = uncheckedButtonColor
                defibrillatorsButton.background.alpha =
                    resources.getInteger(R.integer.noTransparency)
                mapsFragment.removeMarkers(resources.getString(R.string.nearby_defibrillators))

                showingDefibrillators = false
            }
        }

        pharmacyButton.setOnClickListener {
            if (!showingPharmacies) {
                findNearbyUtilities(resources.getString(R.string.nearby_phamacies))

                pharmacyBackgroundLayout.backgroundTintList = checkedButtonColor
                pharmacyButton.background.alpha =
                    resources.getInteger(R.integer.selectionTransparency)

                showingPharmacies = true
            } else {
                pharmacyBackgroundLayout.backgroundTintList = uncheckedButtonColor
                pharmacyButton.background.alpha = resources.getInteger(R.integer.noTransparency)
                mapsFragment.removeMarkers(resources.getString(R.string.nearby_phamacies))

                showingPharmacies = false

            }
        }
    }


    private fun setRequestedButton() {
        when (requestedUtility) {
            resources.getString(R.string.nearby_phamacies) -> {
                val pharmacyButton = findViewById<ImageButton>(R.id.show_pharmacy_button)
                pharmacyButton.callOnClick() // TODO : change to dynamic click
            }
            resources.getString(R.string.nearby_hospitals) -> {
                val hospitalButton = findViewById<ImageButton>(R.id.show_hospital_button)
                hospitalButton.callOnClick()
            }
        }
    }


    /**
     * Retrieves the shortest path to the destination and displays it on the map
     */
    private fun getPath() {
        val url = DIRECTIONS_URL + "?destination=" + destinationLat + "," + destinationLong +
                "&mode=${WALKING}" +
                "&origin=" + currentLat + "," + currentLong +
                "&key=" + resources.getString(R.string.google_maps_key)

        // Launches async routines to retrieve the path to the destination and display it on the map
        CoroutineScope(Dispatchers.Main).launch {
            val data: String = withContext(Dispatchers.IO) { downloadUrl(url) }
            Log.i("GPath", data)
            val path = parseTask(data, GPathJSONParser)
            path.let {
                mapsFragment.showPolyline(path)
                mapsFragment.addMarker(destinationLat, destinationLong, END_POINT_NAME)
            }
        }
    }

    private fun findNearbyUtilities(utility: String) {
        if (!requestedPlaces.containsKey(utility)) {
            if (utility == resources.getString(R.string.nearby_defibrillators)) {
                requestedPlaces[utility] = AED_LOCATIONS_LAUSANNE
                requestedPlaces[utility]?.let { mapsFragment.showPlaces(it, utility) }
            } else {
                val url = PLACES_URL + "?location=" + currentLat + "," + currentLong +
                        "&radius=$DEFAULT_SEARCH_RADIUS" +
                        "&types=$utility" +
                        "&key=" + resources.getString(R.string.google_maps_key)

                // Launches async routines to retrieve nearby places and show them
                // on the map
                CoroutineScope(Dispatchers.Main).launch {
                    val data: String = withContext(Dispatchers.IO) { downloadUrl(url) }
                    Log.i("GPlaces", data)
                    requestedPlaces[utility] = parseTask(data, GPlaceJSONParser)
                    requestedPlaces[utility]?.let { mapsFragment.showPlaces(it, utility) }
                }
            }
        } else {
            requestedPlaces[utility]?.let { mapsFragment.showPlaces(it, utility) }
        }
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


    companion object {
        // Constants to access the Google places API
        const val PLACES_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
        const val DEFAULT_SEARCH_RADIUS = 3000

        // Constants to access the Google directions API

        const val DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json"
        const val WALKING = "walking"


        private const val END_POINT_NAME = "user in need"
    }
}

