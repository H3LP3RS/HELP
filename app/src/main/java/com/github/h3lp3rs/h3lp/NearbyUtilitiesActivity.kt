package com.github.h3lp3rs.h3lp

import LocationHelper
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColorStateList
import com.github.h3lp3rs.h3lp.databinding.ActivityNearbyUtilitiesBinding
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

    private lateinit var hospitalBackgroundLayout: LinearLayout
    private lateinit var pharmacyBackgroundLayout: LinearLayout
    private lateinit var defibrillatorsBackgroundLayout: LinearLayout

    private var uncheckedButtonColor: ColorStateList? = null
    private var checkedButtonColor: ColorStateList? = null

    private lateinit var apiHelper: GoogleAPIHelper
    private val locationHelper = LocationHelper()

    private val isUtilityShown = HashMap<String, Boolean>()

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

        // Obtain the map fragment
        mapsFragment = supportFragmentManager
            .findFragmentById(R.id.mapNearbyUtilities) as MapsFragment

        // Initialize the user's current location
        locationHelper.requireAndHandleCoordinates(this) {
            currentLat = it.latitude
            currentLong = it.longitude
        }

        setupSelectionButtons()
        mapsFragment.executeOnMapReady(this::setRequestedButton)
    }

    /**
     * Creates listeners for the different buttons allowing the user to select
     * some utilities.
     */
    private fun setupSelectionButtons() {
        val hospitalButton = findViewById<ImageButton>(R.id.show_hospital_button)
        val defibrillatorsButton = findViewById<ImageButton>(R.id.show_defibrillators_button)
        val pharmacyButton = findViewById<ImageButton>(R.id.show_pharmacy_button)

        setupUtilityButtonListener(
            hospitalButton,
            resources.getString(R.string.nearby_hospitals),
            hospitalBackgroundLayout
        )
        setupUtilityButtonListener(
            defibrillatorsButton,
            resources.getString(R.string.nearby_defibrillators),
            defibrillatorsBackgroundLayout
        )
        setupUtilityButtonListener(
            pharmacyButton,
            resources.getString(R.string.nearby_phamacies),
            pharmacyBackgroundLayout
        )
    }

    /**
     * Creates the listener linked to a selection button, when the button is already pressed, we
     * remove the corresponding markers, otherwise we add them.
     * @param button: the selection button
     * @param utility: the utility corresponding to the selection button
     * @param background: the linear layout which that contains the selection button. Will be
     * transparent when the button is selected.
     */
    private fun setupUtilityButtonListener(
        button: ImageButton,
        utility: String,
        background: LinearLayout
    ) {
        button.setOnClickListener {
            val isShowing = isUtilityShown.getOrDefault(utility, false)
            if (!isShowing) {
                apiHelper.findNearbyUtilities(utility, currentLong, currentLat, mapsFragment)

                background.backgroundTintList = checkedButtonColor
                button.background.alpha = resources.getInteger(R.integer.selectionTransparency)
            } else {
                background.backgroundTintList = uncheckedButtonColor
                button.background.alpha = resources.getInteger(R.integer.noTransparency)

                mapsFragment.removeMarkers(utility)
            }

            isUtilityShown[utility] = !isShowing
        }
    }


    /**
     * Selects the button to show the nearby utility that was asked before before
     * this activity started.
     */
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
            resources.getString(R.string.nearby_defibrillators) -> {
                val defibrillatorsButton =
                    findViewById<ImageButton>(R.id.show_defibrillators_button)
                defibrillatorsButton.callOnClick()
            }
        }
    }
}

