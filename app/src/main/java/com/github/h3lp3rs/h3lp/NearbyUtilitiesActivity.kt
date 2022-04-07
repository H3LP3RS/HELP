package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColorStateList
import com.github.h3lp3rs.h3lp.GoogleAPIHelper.Companion.DEFAULT_SEARCH_RADIUS
import com.github.h3lp3rs.h3lp.GoogleAPIHelper.Companion.PLACES_URL
import com.github.h3lp3rs.h3lp.databinding.ActivityNearbyUtilitiesBinding
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.util.AED_LOCATIONS_LAUSANNE
import com.github.h3lp3rs.h3lp.util.GPlaceJSONParser
import kotlinx.coroutines.*


/**
 * Activity that displays nearby utilities such as pharmacies, defibrillators and hospitals
 */
class NearbyUtilitiesActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityNearbyUtilitiesBinding

    // Map fragment displayed
    private lateinit var mapsFragment: MapsFragment

    private var requestedUtility: String? = null

    private var currentLong: Double = 0.0
    private var currentLat: Double = 0.0
    private lateinit var pathData: String

    private var showingHospitals = false
    private var showingPharmacies = false
    private var showingDefibrillators = false

    private lateinit var hospitalBackgroundLayout: LinearLayout
    private lateinit var pharmacyBackgroundLayout: LinearLayout
    private lateinit var defibrillatorsBackgroundLayout: LinearLayout

    private var uncheckedButtonColor: ColorStateList? = null
    private var checkedButtonColor: ColorStateList? = null

    private lateinit var apiHelper: GoogleAPIHelper

    // Places and markers (key is the utility)
    private val requestedPlaces = HashMap<String, List<GooglePlace>>()

    // TODO : currently, the destination is hardcoded, this will change with the task allowing
    // nearby helpers to go and help people in need (in which case the destination will be the
    // location of the user in need)
    private val destinationLat = 46.51902895030102
    private val destinationLong = 6.567597089508282

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Displaying the activity layout
        binding = ActivityNearbyUtilitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedUtility = intent.getStringExtra(EXTRA_NEARBY_UTILITIES)

        // Retrieve elements from the UI and setup constants
        hospitalBackgroundLayout = findViewById(R.id.show_hospital_button_layout)
        defibrillatorsBackgroundLayout = findViewById(R.id.show_defibrillators_button_layout)
        pharmacyBackgroundLayout = findViewById(R.id.show_pharmacy_button_layout)

        checkedButtonColor = getColorStateList(this, R.color.select_meds_checked)
        uncheckedButtonColor = getColorStateList(this, R.color.select_meds_unchecked)

        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key))

        // Initialize the user's current location
        setupLocation()
        setupSelectionButtons()
        setRequestedButton() // TODO : check that since it's called at creation (not when the map is ready necess), it doesn't cause a problem

        // Obtain the map fragment
        mapsFragment = supportFragmentManager
            .findFragmentById(R.id.map) as MapsFragment

        // Displays the path to a user in need on the map fragment
        apiHelper.displayWalkingPath(
            currentLat,
            currentLong,
            destinationLat,
            destinationLong,
            mapsFragment
        )
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
                pharmacyButton.callOnClick()
            }
            resources.getString(R.string.nearby_hospitals) -> {
                val hospitalButton = findViewById<ImageButton>(R.id.show_hospital_button)
                hospitalButton.callOnClick()
            }
        }
    }

    /**
     * Finds the nearby utilities and displays them
     * @param utility The utility searched for (pharmacies, hospitals or defibrillators)
     */
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
                    pathData = withContext(Dispatchers.IO) { apiHelper.downloadUrl(url) }
                    Log.i("GPlaces", pathData)
                    requestedPlaces[utility] = apiHelper.parseTask(pathData, GPlaceJSONParser)
                    requestedPlaces[utility]?.let { mapsFragment.showPlaces(it, utility) }
                }
            }
        } else {
            requestedPlaces[utility]?.let { mapsFragment.showPlaces(it, utility) }
        }
    }
}

