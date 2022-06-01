package com.github.h3lp3rs.h3lp.map

import LocationHelper
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.h3lp3rs.h3lp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import java.lang.Double.parseDouble
import java.util.*


typealias GooglePlace = HashMap<String, String>

/**
 * Fragment containing a google map and several methods to add markers, paths... on it
 * We use a fragment to make it easily usable any activity that requires a map without code
 * duplication
 */
class MapsFragment : Fragment(), CoroutineScope by MainScope(), GoogleMap.OnPolylineClickListener {
    private lateinit var map: GoogleMap
    private var isMapReady = false

    private val locationHelper = LocationHelper()

    // Functions to execute once the map is ready
    private val onMapReadyQueue = LinkedList<() -> Unit>()

    private var currentLong: Double = 0.0
    private var currentLat: Double = 0.0

    // places and markers (key is the utility)
    private val placedMarkers = HashMap<String, List<Marker>>()


    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        setupMap()


        // Set listener for click events.
        map.setOnPolylineClickListener(this)
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun setupMap() {
        if (!::map.isInitialized) return
        locationHelper.requireAndHandleCoordinates(requireContext()) {
            map.isMyLocationEnabled = true
            currentLat = it.latitude
            currentLong = it.longitude

            val myPosition = LatLng(currentLat, currentLong)

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    myPosition,
                    DEFAULT_MAP_ZOOM
                )
            )

            // Execute pending functions
            for (f in onMapReadyQueue) {
                f()
            }

            isMapReady = true
        }
    }

    /**
     * Auxiliary function to store a function we would like to execute once
     * the map is ready.
     * @param function: function that will be executed once the map is ready, or
     * right away if it is already the case.
     */
    fun executeOnMapReady(function: () -> Unit) {
        if (isMapReady) {
            function()
        } else {
            onMapReadyQueue.add(function)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    /**
     * General method to add markers for places on the map
     * @param places The locations of the markers to add
     * @param utility The utility these locations correspond to (to display a different marker
     *  depending on which utility is displayed)
     */
    fun showPlaces(places: List<GooglePlace>, utility: String) {
        // Create new markers
        for (place in places) run {
            if (place.containsKey("lat")
                && place.containsKey("lng")
                && place.containsKey("name")
            ) {
                val lat = parseDouble(place["lat"]!!)
                val lng = parseDouble(place["lng"]!!)

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
     * Adds a polyline that shows the path on the map
     * @param points The points that construct the polyline
     */
    fun showPolyline(points: List<LatLng>) {
        val polylineOpt = PolylineOptions().clickable(true)

        points.forEach { p ->
            polylineOpt.add(p)
        }

        val polyline: Polyline = map.addPolyline(polylineOpt)
        stylePolyline(polyline)
    }

    /**
     * Styles the polyline
     * @param polyline The polyline object that needs styling
     */
    private fun stylePolyline(polyline: Polyline) {
        polyline.startCap = RoundCap()
        polyline.endCap = SquareCap()
        polyline.width = POLYLINE_STROKE_WIDTH_PX.toFloat()
        polyline.color = BLUE_ARGB
        polyline.jointType = JointType.ROUND
    }

    /**
     * Puts a pin marker at the end of the path with default marker options (a location and a name
     * for the marker)
     * @param destinationLat Latitude of the endpoint of the path
     * @param destinationLong Longitude of the endpoint of the path
     * @param markerName Name to give to the marker (is visible upon clicking on it)
     */
    fun addMarker(destinationLat: Double, destinationLong: Double, markerName: String) {
        // Only add a marker when the map is ready
        executeOnMapReady {
            val options = MarkerOptions()
            val latLng = LatLng(destinationLat, destinationLong)

            options.position(latLng)
            options.title(markerName)
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_point_pin))

            addMarker(options)
        }
    }

    /**
     * Adds a custom marker to the map
     */
    fun addMarker(marker: MarkerOptions) {
        map.addMarker(marker)
    }

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
            requireContext(), getString(R.string.on_click_path),
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Utility function to remove all markers corresponding to one utility
     */
    fun removeMarkers(utility: String) {
        placedMarkers[utility]?.let { markersList ->
            markersList.map { it.remove() }
        }

        placedMarkers[utility] = arrayListOf()
    }

    companion object {
        // Constants for the map parameters
        const val DEFAULT_MAP_ZOOM = 15f

        // Constants for the polyline appearance

        // The minus is simply there since the polyline color attribute requires an integer, but writing
        // the actual HEX value of blue with an alpha larger than 0xF would need a long, we thus write
        // this larger HEX value correctly for an integer, that is with 2's complement
        private const val BLUE_ARGB = -0x1FF7E3D
        private const val POLYLINE_STROKE_WIDTH_PX = 12

        // Constants for the polyline appearance after having been clicked
        private const val PATTERN_GAP_LENGTH_PX = 20
        private val DOT: PatternItem = Dot()
        private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())

        // Create a stroke pattern of a gap followed by a dot.
        private val PATTERN_POLYLINE_DOTTED: List<PatternItem> = listOf(GAP, DOT)
    }
}




