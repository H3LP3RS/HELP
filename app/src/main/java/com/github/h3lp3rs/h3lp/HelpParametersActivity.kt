package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.database.repositories.EmergencyInfoRepository
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf

import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList

const val EXTRA_NEEDED_MEDICATION = "needed_meds_key"
const val EXTRA_CALLED_EMERGENCIES = "has_called_emergencies"
const val EXTRA_EMERGENCY_KEY = "emergency_key"

/**
 * Activity in which the user can select the medications they need urgently
 */
class HelpParametersActivity : AppCompatActivity() {
    // userLocation contains the user's current coordinates (is initialized to null since we could
    // encounter an error while getting the user's location)
    private var userLocation: Location? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var meds: ArrayList<String> = ArrayList()
    private var skills: HelperSkills? = null
    private val currentTime: Date = Calendar.getInstance().time
    private var calledEmergencies = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_parameters)

        // Get the coordinates and display them on the screen to enable the user to give their exact
        // location to the emergency services
        updateCoordinates()
        val locationInformation: TextView = findViewById(R.id.location_information)
        val coordinatesText = getString(R.string.current_location)
        if (userLocation != null) {
            latitude =  userLocation!!.latitude
            longitude = userLocation!!.longitude
            locationInformation.text = String.format(
                    "%s latitude: %.4f longitude: %.4f",
            coordinatesText,
            userLocation!!.latitude,
            userLocation!!.longitude
            )
        } else {
            // If the user didn't allow location permissions, they won't be able to see their
            // current location
            locationInformation.text = getString(R.string.error_retrieving_location)
        }

    }

    /**
     *  Called when the user presses the emergency call button. Opens the phone call app with the
     *  emergency number from the country the user is currently in dialed.
     *  @param view The view of the button pressed
     */
    fun emergencyCall(view: View) {
        // In case the getCurrentLocation failed (for example if the location services aren't
        // activated, currentLocation is still null and the returned phone number will be the
        // default emergency phone number
        calledEmergencies = true
        updateCoordinates()
        val emergencyNumber =
            LocalEmergencyCaller.getLocalEmergencyNumber(
                userLocation?.longitude,
                userLocation?.latitude,
                this
            )
        val dial = "tel:$emergencyNumber"
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(dial)))
    }


    /**
     *  Called when the user presses the "search for help" button after selecting their need.
     *  @param view The view of the button pressed
     */
    fun searchHelp(view: View) {
        val selectionPair = retrieveSelectedMedication(view)
        meds = selectionPair.first
        skills = selectionPair.second

        if (meds.isEmpty()) {
            Toast.makeText(
                applicationContext,
                getString(R.string.AT_LEAST_ONE_ITEM), Toast.LENGTH_SHORT
            ).show()
        } else {
            val bundle = Bundle()
            bundle.putStringArrayList(EXTRA_NEEDED_MEDICATION, meds)
            bundle.putBoolean(EXTRA_CALLED_EMERGENCIES, calledEmergencies)
            val intent = Intent(this, AwaitHelpActivity::class.java)
            sendInfoToDB().thenAccept {
                bundle.putInt(EXTRA_EMERGENCY_KEY, it)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    /**
     * Stores the emergency information in the database for further use
     * @return The id of the emergency in a future
     */
    private fun sendInfoToDB(): CompletableFuture<Int> {
        // Get emergency related databases
        val emergenciesDb = databaseOf(EMERGENCIES)
        val newEmergenciesDb = databaseOf(NEW_EMERGENCIES)
        // Get own medical storage and extract the information if available
        val storage = storageOf(MEDICAL_INFO)
        val medicalInfo = storage.getObjectOrDefault(getString(R.string.medical_info_key),
            MedicalInformation::class.java, null)
        // TODO: Use future once this is has been changed to avoid double work
        val uid = emergenciesDb.getInt(getString(R.string.EMERGENCY_UID_KEY))
        // Increment
        emergenciesDb.incrementAndGet(getString(R.string.EMERGENCY_UID_KEY), 1) {}
        return uid.thenApply {
            // Stop listening to new emergencies
            newEmergenciesDb.clearAllListeners()
            // Create and send the emergency object
            val id = it + 1
            val emergencyInfo = EmergencyInformation(id.toString(), latitude!!, longitude!!, skills!!, meds, currentTime, medicalInfo, ArrayList())
            EmergencyInfoRepository(emergenciesDb).insert(emergencyInfo)
            // Raise the appropriate flags to notify potential helpers
            val needed = skills!!
            raiseFlagInDb(needed.hasVentolin, newEmergenciesDb, R.string.asthma_med, id)
            raiseFlagInDb(needed.hasEpipen, newEmergenciesDb, R.string.epipen, id)
            raiseFlagInDb(needed.knowsCPR, newEmergenciesDb, R.string.cpr, id)
            raiseFlagInDb(needed.hasInsulin, newEmergenciesDb, R.string.Insulin, id)
            raiseFlagInDb(needed.hasFirstAidKit, newEmergenciesDb, R.string.first_aid_kit, id)
            raiseFlagInDb(needed.isMedicalPro, newEmergenciesDb, R.string.med_pro, id)
            // Return unique id for future reference
            id
        }
    }

    /**
     * Raises the flag if needed in the database of the corresponding key in the given resource id
     * @param flag Whether or not the flag must be risen
     * @param db The database to raise the flag
     * @param resId The id in the resource file that corresponds to a key on which we will raise the
     * flag
     * @param emergencyId The unique emergency id
     */
    private fun raiseFlagInDb(flag: Boolean, db: Database, resId: Int, emergencyId: Int) {
        if(flag) db.setInt(resources.getString(resId), emergencyId)
    }

    /**
     * Auxiliary function to retrieve the selected meds on the page and the required helper skills
     * @param view The view to retrieve medication from (in its children)
     */
    private fun retrieveSelectedMedication(view: View): Pair<ArrayList<String>, HelperSkills> {
        val viewGroup = view.parent as ViewGroup

        val meds = arrayListOf<String>()
        var skills = HelperSkills(false, false, false,
                                false,false, false)

        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i) as View
            if (child is ToggleButton) {
                if (child.isChecked) {
                    // Add medication string (for display)
                    meds.add(child.textOff as String)
                    // Update skills object (for code usage)
                    when (child.textOff) {
                        resources.getString(R.string.epipen) -> {
                            skills = skills.copy(hasEpipen = true)
                        }
                        resources.getString(R.string.cpr) -> {
                            skills = skills.copy(knowsCPR = true)
                        }
                        resources.getString(R.string.asthma_med) -> {
                            skills = skills.copy(hasVentolin = true)
                        }
                        resources.getString(R.string.Insulin) -> {
                            skills = skills.copy(hasInsulin = true)
                        }
                        resources.getString(R.string.first_aid_kit) -> {
                            skills = skills.copy(hasFirstAidKit = true)
                        }
                        else -> {
                            skills = skills.copy(isMedicalPro = true)
                        }
                    }
                }
            }
        }
        return Pair(meds, skills)
    }

    /**
     * Function that updates the user's current coordinates
     */
    private fun updateCoordinates() {
        val updatedCoordinates = GeneralLocationManager.get().getCurrentLocation(this)

        if (updatedCoordinates != null) {
            userLocation = Location(LocationManager.GPS_PROVIDER)
            userLocation?.longitude = updatedCoordinates.longitude
            userLocation?.latitude = updatedCoordinates.latitude
        } else {
            userLocation = null
        }
    }
}