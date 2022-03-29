package com.github.h3lp3rs.h3lp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.ColorStateList
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getColorStateList
import com.github.h3lp3rs.h3lp.databinding.ActivityNearbyUtilitiesBinding
import com.github.h3lp3rs.h3lp.util.GPlaceJsonParser
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
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

class NearbyUtilitiesActivity : AppCompatActivity(), OnMapReadyCallback,
    CoroutineScope by MainScope(),GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener {

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
                defibrillatorsBackgroundLayout.backgroundTintList = checkedButtonColor
                defibrillatorsButton.background.alpha =
                    resources.getInteger(R.integer.selectionTransparency)

                showingDefibrillators = true
            } else {
                defibrillatorsBackgroundLayout.backgroundTintList = uncheckedButtonColor
                defibrillatorsButton.background.alpha =
                    resources.getInteger(R.integer.noTransparency)

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

        showPolyline(listOf(
            LatLng(-35.016, 143.321),
            LatLng(-34.747, 145.592),
            LatLng(-34.364, 147.891),
            LatLng(-33.501, 150.217),
            LatLng(-32.306, 149.248),
            LatLng(-32.491, 147.309))
        )

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

        // Set listeners for click events.
        map.setOnPolylineClickListener(this)
        map.setOnPolygonClickListener(this)
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
    private fun showPolyline(points : List<LatLng>){
        val polylineOpt = PolylineOptions().clickable(true)
        points.forEach { p ->
            polylineOpt.add(p)
        }
                /*.add(
                    LatLng(-35.016, 143.321),
                    LatLng(-34.747, 145.592),
                    LatLng(-34.364, 147.891),
                    LatLng(-33.501, 150.217),
                    LatLng(-32.306, 149.248),
                    LatLng(-32.491, 147.309)

                )*/
        val polyline: Polyline = map.addPolyline(polylineOpt)
        polyline.tag = "A"
        stylePolyline(polyline)
    }
    private val COLOR_BLACK_ARGB = -0x1000000
    private val POLYLINE_STROKE_WIDTH_PX = 12

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private fun stylePolyline(polyline: Polyline) {
        var type = ""
        // Get the data object stored with the polyline.
        if (polyline.tag != null) {
            type = polyline.tag.toString()
        }
        when (type) {
            "A" ->                 // Use a custom bitmap as the cap at the start of the line.
                polyline.startCap = CustomCap(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10F
                )
            "B" ->                 // Use a round cap at the start of the line.
                polyline.startCap = RoundCap()
        }
        polyline.endCap = RoundCap()
        polyline.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
        polyline.color = COLOR_BLACK_ARGB
        polyline.jointType = JointType.ROUND
    }

    private val PATTERN_GAP_LENGTH_PX = 20
    private val DOT: PatternItem = Dot()
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())

    // Create a stroke pattern of a gap followed by a dot.
    private val PATTERN_POLYLINE_DOTTED: List<PatternItem> = listOf(GAP, DOT)

    /**
     * Listens for clicks on a polyline.
     * @param polyline The polyline object that the user has clicked.
     */
    override fun onPolylineClick(polyline: Polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if (polyline.pattern == null || !polyline.pattern!!.contains(DOT)) {
            polyline.pattern = PATTERN_POLYLINE_DOTTED
        } else {
            // The default pattern is a solid stroke.
            polyline.pattern = null
        }
        Toast.makeText(
            this, "Route type " + polyline.tag.toString(),
            Toast.LENGTH_SHORT
        ).show()
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

    override fun onPolygonClick(p0: Polygon) {
        TODO("Not yet implemented")
    }

}

