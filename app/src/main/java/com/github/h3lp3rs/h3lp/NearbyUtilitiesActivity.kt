package com.github.h3lp3rs.h3lp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.github.h3lp3rs.h3lp.databinding.ActivityNearbyUtilitiesBinding

import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import com.github.h3lp3rs.h3lp.util.GPlaceJsonParser
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Double.parseDouble
import java.net.HttpURLConnection
import java.net.URL


const val PLACES_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
const val DEFAULT_MAP_ZOOM = 15f
const val DEFAULT_SEARCH_RADIUS = 3000

class NearbyUtilitiesActivity : AppCompatActivity(), OnMapReadyCallback,
    OnRequestPermissionsResultCallback, CoroutineScope by MainScope(){

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityNearbyUtilitiesBinding
    private var utility: String? = null
    private var permissionDenied = false
    private var currentLong: Double = 0.0
    private var currentLat: Double = 0.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNearbyUtilitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        utility = intent.getStringExtra(EXTRA_NEARBY_UTILITIES)

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
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun setupMap() {
        if (!::map.isInitialized) return
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
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

                if (utility != null) {
                    findNearbyUtilities()
                }
            }

        } else {
            // Permission to access the location is missing
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun findNearbyUtilities() {
        val url = PLACES_URL + "?location=" + currentLat + "," + currentLong +
                "&radius=$DEFAULT_SEARCH_RADIUS" +
                "&types=$utility" +
                "&key=" + resources.getString(R.string.google_maps_key)

        // Launches async routines to retrieve nearby places and show them on
        // the map
        CoroutineScope(Dispatchers.Main).launch {
            val data: String = withContext(Dispatchers.IO) { downloadUrl(url) }
            Log.i("GPlaces", data)
            val placesMap = parsePlacesTask(data)
            showPlaces(placesMap)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Enable the my location layer if the permission has been granted.
            setupMap()
        } else {
            // Permission was denied.
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            permissionDenied = false
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
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

    private fun showPlaces(places: List<HashMap<String, String>>){
        // Remove eventual old markers
        map.clear()

        // Create new markers
        for (place in places) run {
            if (place.containsKey("lat")
                && place.containsKey("lng")
                && place.containsKey("name")
                && place["lat"] != null
                && place["lng"] != null){
                val lat = parseDouble(place["lat"])
                val lng = parseDouble(place["lng"])

                val name = place["name"]
                val latLng = LatLng(lat, lng)

                val options = MarkerOptions()
                options.position(latLng)
                options.title(name)

                // Adapt marker to utility
                when(utility) {
                    resources.getString(R.string.nearby_phamacies) -> {
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.pharmacy_marker))
                    }
                    resources.getString(R.string.nearby_hospitals) -> {
                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_marker))
                    }
                }

                map.addMarker(options)
            }
        }
    }


    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}