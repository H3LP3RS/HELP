package com.github.h3lp3rs.h3lp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.github.h3lp3rs.h3lp.databinding.ActivityNearbyUtilitiesBinding

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.ColorStateList
import android.location.Criteria
import android.location.LocationManager
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.*
import com.github.h3lp3rs.h3lp.util.GPlaceJsonParser
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Double.parseDouble
import java.net.HttpURLConnection
import java.net.URL

typealias GooglePlace = HashMap<String, String>

const val PLACES_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
const val DEFAULT_MAP_ZOOM = 15f
const val DEFAULT_SEARCH_RADIUS = 3000
private val AED_LOCATIONS_LAUSANNE = listOf(
    hashMapOf("name" to "aed","lat" to "46.53662", "lng" to "6.58833"),
    hashMapOf("name" to "aed","lat" to "46.53257", "lng" to "6.58685"),
    hashMapOf("name" to "aed","lat" to "46.52786", "lng" to "6.61601"),
    hashMapOf("name" to "aed","lat" to "46.52934", "lng" to "6.62275"),
    hashMapOf("name" to "aed","lat" to "46.52292", "lng" to "6.62627"),
    hashMapOf("name" to "aed","lat" to "46.52301", "lng" to "6.63239"),
    hashMapOf("name" to "aed","lat" to "46.52362", "lng" to "6.63364"),
    hashMapOf("name" to "aed","lat" to "46.51760", "lng" to "6.63102"),
    hashMapOf("name" to "aed","lat" to "46.52018", "lng" to "6.63399"),
    hashMapOf("name" to "aed","lat" to "46.52353", "lng" to "6.63880"),
)

class NearbyUtilitiesActivity : AppCompatActivity(), OnMapReadyCallback,
    CoroutineScope by MainScope() {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityNearbyUtilitiesBinding
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
    private val placedMarkers = HashMap<String, List<Marker>>()


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


        setupSelectionButtons()

        // Obtain the SupportMapFragment and get notified when the map is ready
        // to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
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
                removeMarkers(resources.getString(R.string.nearby_hospitals))

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
                defibrillatorsButton.background.alpha = resources.getInteger(R.integer.noTransparency)
                removeMarkers(resources.getString(R.string.nearby_defibrillators))

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
                removeMarkers(resources.getString(R.string.nearby_phamacies))

                showingPharmacies = false

            }
        }
    }


    /**
     * Manipulates the map once available.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        setupMap()
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun setupMap() {
        if (!::map.isInitialized) return
        if (checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true

            // Retrieve current location and center camera around it
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val provider = locationManager.getBestProvider(Criteria(), true)

            val currentLocation = provider?.let { locationManager.getLastKnownLocation(it) }

            if (currentLocation != null) {
                currentLat = currentLocation.latitude
                currentLong = currentLocation.longitude

                val myPosition = LatLng(currentLat, currentLong)

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, DEFAULT_MAP_ZOOM))

                when (requestedUtility) {
                    resources.getString(R.string.nearby_phamacies) -> {
                        val pharmacyButton = findViewById<ImageButton>(R.id.show_pharmacy_button)
                        pharmacyButton.callOnClick()
                    }
                    resources.getString(R.string.nearby_hospitals) -> {
                        val hospitalButton = findViewById<ImageButton>(R.id.show_hospital_button)
                        hospitalButton.callOnClick()
                    }
                }
            }

        } else {
            // Permission to access the location is missing
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun findNearbyUtilities(utility: String) {
        if (!requestedPlaces.containsKey(utility)) {
            if(utility == resources.getString(R.string.nearby_defibrillators)){
                requestedPlaces[utility] = AED_LOCATIONS_LAUSANNE
                requestedPlaces[utility]?.let { showPlaces(it, utility) }
            }
            else{
                val url = PLACES_URL + "?location=" + currentLat + "," + currentLong +
                        "&radius=$DEFAULT_SEARCH_RADIUS" +
                        "&types=$utility" +
                        "&key=" + resources.getString(R.string.google_maps_key)

                // Launches async routines to retrieve nearby places and show them
                // on the map
                CoroutineScope(Dispatchers.Main).launch {
                    val data: String = withContext(Dispatchers.IO) { downloadUrl(url) }
                    Log.i("GPlaces", data)
                    requestedPlaces[utility] = parsePlacesTask(data)
                    requestedPlaces[utility]?.let { showPlaces(it, utility) }
                }
            }
        } else {
            requestedPlaces[utility]?.let { showPlaces(it, utility) }
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

    private fun parsePlacesTask(data: String): List<HashMap<String, String>> {
        val parser = GPlaceJsonParser()

        val obj = JSONObject(data)

        return parser.parseResult(obj)
    }

    private fun showPlaces(places: List<GooglePlace>, utility: String) {
        // Create new markers
        for (place in places) run {
            if (place.containsKey("lat")
                && place.containsKey("lng")
                && place.containsKey("name")
                && place["lat"] != null
                && place["lng"] != null
            ) {
                val lat = parseDouble(place["lat"])
                val lng = parseDouble(place["lng"])

                val name = place["name"]
                val latLng = LatLng(lat, lng)

                val options = MarkerOptions()
                options.position(latLng)
                options.title(name)

                // Adapt marker to utility
                when (utility) {
                    resources.getString(R.string.nearby_phamacies) -> {
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.pharmacy_marker))
                    }
                    resources.getString(R.string.nearby_hospitals) -> {
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_marker))
                    }
                    resources.getString(R.string.nearby_defibrillators) -> {
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.aed_marker))
                    }
                }

                // Add marker to list so that we can remove it later
                val marker = map.addMarker(options)
                if (marker != null) {
                    if (placedMarkers.containsKey(utility)) {
                        (placedMarkers[utility] as ArrayList).add(marker)
                    } else {
                        placedMarkers[utility] = arrayListOf(marker)
                    }
                }
            }
        }
    }

    /**
     * Utility function to remove all markers corresponding to one utility
     */
    private fun removeMarkers(utility: String) {
        for (marker in placedMarkers[utility]!!) {
            marker.remove()
        }

        placedMarkers[utility] = arrayListOf()
    }

}

