package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.database.repositories.EmergencyInfoRepository
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.database.Databases.CONVERSATION_IDS
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import kotlinx.android.synthetic.main.activity_help_parameters.*
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList

const val EXTRA_NEEDED_MEDICATION = "needed_meds_key"
const val EXTRA_CALLED_EMERGENCIES = "has_called_emergencies"
const val EXTRA_EMERGENCY_KEY = "emergency_key"

/**
 * Activity in which the user can select the medications they need urgently
 */
class HelpeeSelectionActivity : AppCompatActivity() {

    private var calledEmergencies = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_parameters)

        // Initialize the current user's location
        val location = getLocation()
        if (location != null) {
            val (latitude, longitude) = location
            val locationInformation: TextView = findViewById(R.id.location_information)

            val coordinatesText = getString(R.string.current_location)

            locationInformation.text = String.format(
                "%s latitude: %.4f longitude: %.4f",
                coordinatesText, latitude, longitude
            )

            help_params_call_button.setOnClickListener {
                emergencyCall(
                    latitude,
                    longitude
                )
            }
            help_params_search_button.setOnClickListener {
                searchHelp(
                    latitude,
                    longitude,
                    help_params_search_button
                )
            }
        } else {
            // If it is null, we still want to be able to call the emergency
            help_params_call_button.setOnClickListener {
                emergencyCall(
                    null,
                    null
                )
            }
        }
    }

    /**
     *  Called when the user presses the emergency call button. Opens a pop-up
     *  asking the user to choose whether they want to call local emergency
     *  services or their emergency contact, and dials the correct number.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun emergencyCall(latitude: Double?, longitude: Double?) {
        val medicalInfo = storageOf(MEDICAL_INFO)
            .getObjectOrDefault(
                getString(R.string.medical_info_key),
                MedicalInformation::class.java,
                null
            )

        if (medicalInfo != null) {
            val builder = AlertDialog.Builder(this)
            val emergencyCallPopup = layoutInflater.inflate(R.layout.emergency_call_options, null)

            builder.setCancelable(false)
            builder.setView(emergencyCallPopup)

            val alertDialog = builder.create()

            // ambulance button
            emergencyCallPopup.findViewById<ImageButton>(R.id.ambulance_call_button)
                .setOnClickListener {
                    // In case the getCurrentLocation failed (for example if the location services aren't
                    // activated, currentLocation is still null and the returned phone number will be the
                    // default emergency phone number
                    alertDialog.cancel()
                    launchEmergencyCall(latitude, longitude)
                }

            // contact button
            emergencyCallPopup.findViewById<ImageButton>(R.id.contact_call_button)
                .setOnClickListener {
                    alertDialog.cancel()

                    val dial = "tel:${medicalInfo.emergencyContactNumber}"
                    startActivity(Intent(ACTION_DIAL, Uri.parse(dial)))
                }
            alertDialog.show()
        } else {
            launchEmergencyCall(latitude, longitude)
        }
    }

    /**
     * Launches a the phone app with the local emergency number dialed
     */
    private fun launchEmergencyCall(latitude: Double?, longitude: Double?) {
        calledEmergencies = true
        val emergencyNumber =
            LocalEmergencyCaller.getLocalEmergencyNumber(
                longitude,
                latitude, this
            )

        val dial = "tel:$emergencyNumber"
        startActivity(Intent(ACTION_DIAL, Uri.parse(dial)))
    }

    /**
     *  Called when the user presses the "search for help" button after selecting their need.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchHelp(latitude: Double, longitude: Double, view: View) {
        val selectionPair = retrieveSelectedMedication(view)
        val meds = selectionPair.first
        val skills = selectionPair.second

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
            sendInfoToDB(latitude, longitude, skills, meds).thenAccept {
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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendInfoToDB(
        latitude: Double,
        longitude: Double,
        skills: HelperSkills,
        meds: ArrayList<String>
    ): CompletableFuture<Int> {
        // Database where all the conversation ids generated by helpers will be stored
        val conversationIdsDb = databaseOf(CONVERSATION_IDS)
        // Get emergency related databases
        val emergenciesDb = databaseOf(EMERGENCIES)
        val newEmergenciesDb = databaseOf(NEW_EMERGENCIES)
        // Get own medical storage and extract the information if available
        val storage = storageOf(MEDICAL_INFO)
        val medicalInfo = storage.getObjectOrDefault(
            getString(R.string.medical_info_key),
            MedicalInformation::class.java, null
        )

        return emergenciesDb.incrementAndGet(getString(R.string.EMERGENCY_UID_KEY), 1).thenApply {
            // Stop listening to new emergencies
            newEmergenciesDb.clearAllListeners()
            // Create and send the emergency object
            val emergencyInfo = EmergencyInformation(
                it.toString(),
                latitude,
                longitude,
                skills,
                meds,
                Calendar.getInstance().time,
                medicalInfo,
                ArrayList()
            )
            EmergencyInfoRepository(emergenciesDb).insert(emergencyInfo)
            // Raise the appropriate flags to notify potential helpers
            raiseFlagInDb(skills.hasVentolin, newEmergenciesDb, R.string.asthma_med, it)
            raiseFlagInDb(skills.hasEpipen, newEmergenciesDb, R.string.epipen, it)
            raiseFlagInDb(skills.knowsCPR, newEmergenciesDb, R.string.cpr, it)
            raiseFlagInDb(skills.hasInsulin, newEmergenciesDb, R.string.Insulin, it)
            raiseFlagInDb(skills.hasFirstAidKit, newEmergenciesDb, R.string.first_aid_kit, it)
            raiseFlagInDb(skills.isMedicalPro, newEmergenciesDb, R.string.med_pro, it)
            // Return unique id for future reference
            it
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
        if (flag) db.setInt(resources.getString(resId), emergencyId)
    }

    /**
     * Auxiliary function to retrieve the selected meds on the page and the required helper skills
     * @param view The view to retrieve medication from (in its children)
     */
    private fun retrieveSelectedMedication(view: View): Pair<ArrayList<String>, HelperSkills> {
        val viewGroup = view.parent as ViewGroup

        val meds = arrayListOf<String>()
        var skills = HelperSkills(
            false, false, false,
            false, false, false
        )

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

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    /**
     * Initializes the user's current location or returns to the main page in case a mistake occured
     * during the location information retrieval
     */
    private fun getLocation(): Pair<Double, Double>? {
        val currentLocation = GeneralLocationManager.get().getCurrentLocation(this)
        if (currentLocation != null) {
            return Pair(currentLocation.latitude, currentLocation.longitude)
        }
        return null
    }
}