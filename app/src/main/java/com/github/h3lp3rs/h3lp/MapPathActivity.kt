package com.github.h3lp3rs.h3lp

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.*
import com.github.h3lp3rs.h3lp.databinding.ActivityMapPathBinding
import com.github.h3lp3rs.h3lp.util.GPathJsonParser
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


const val DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json"
const val BICYCLING = "biycycling"

class MapPathActivity : AppCompatActivity(), OnMapReadyCallback,
    CoroutineScope by MainScope(), GoogleMap.OnPolylineClickListener,
    GoogleMap.OnPolygonClickListener{

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapPathBinding
    private var currentLong: Double = 0.0
    private var currentLat: Double = 0.0
    private var path: List<LatLng>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapPathBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready
        // to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        setupMap()

//        showPolyline(listOf(
//            LatLng(-35.016, 143.321),
//            LatLng(-34.747, 145.592),
//            LatLng(-34.364, 147.891),
//            LatLng(-33.501, 150.217),
//            LatLng(-32.306, 149.248),
//            LatLng(-32.491, 147.309))
//        )
//
//        // Position the map's camera near Alice Springs in the center of Australia,
//        // and set the zoom factor so most of Australia shows on the screen.
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

        // Set listeners for click events.
        map.setOnPolylineClickListener(this)
        map.setOnPolygonClickListener(this)

        getPath()

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
            }

        } else {
            // Permission to access the location is missing
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getPath() {
        //TODO : remove
        val destinationLat = 46.6
        val destinationLong = 6.63
        val url = DIRECTIONS_URL + "?destination=" + destinationLat + "," + destinationLong +
                "&mode=$BICYCLING" +
                "&origin=" + currentLat + "," + currentLong +
                "&key=" + resources.getString(R.string.google_maps_key)

        // Launches async routines to retrieve nearby places and show them
        // on the map
        CoroutineScope(Dispatchers.Main).launch {
            val data: String = withContext(Dispatchers.IO) { downloadUrl(url) }
            Log.i("GPath", data)
            path = parsePlacesTask(data)
            path?.let { showPolyline(path!!) }
        }
    }

    private fun showPath() {

        setupMap()

        showPolyline(listOf(
            LatLng(-35.016, 143.321),
            LatLng(-34.747, 145.592),
            LatLng(-34.364, 147.891),
            LatLng(-33.501, 150.217),
            LatLng(-32.306, 149.248),
            LatLng(-32.491, 147.309))
        )

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

    private fun parsePlacesTask(data: String): List<LatLng> {
        val parser = GPathJsonParser()

        val obj = JSONObject(data)

        return parser.parseResult(obj)
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
        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-23.684, 133.903), 4f))

        // Set listeners for click events.
        map.setOnPolylineClickListener(this)
        map.setOnPolygonClickListener(this)

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

    override fun onPolygonClick(p0: Polygon) {
        TODO("Not yet implemented")
    }

}

