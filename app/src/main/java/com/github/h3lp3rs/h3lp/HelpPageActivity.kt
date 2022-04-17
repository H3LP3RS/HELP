package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.databinding.ActivityHelpPageBinding
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.messaging.Conversation
import com.github.h3lp3rs.h3lp.messaging.Conversation.Companion.UNIQUE_CONVERSATION_ID
import com.github.h3lp3rs.h3lp.messaging.Messenger.HELPER
import com.github.h3lp3rs.h3lp.util.GDurationJSONParser
import com.google.android.gms.maps.MapsInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

const val EXTRA_HELP_REQUIRED_PARAMETERS = "help_page_required_key"
const val EXTRA_DESTINATION_LAT = "help_page_destination_lat"
const val EXTRA_DESTINATION_LONG = "help_page_destination_long"
const val EXTRA_HELP_UNIQUE_ID = "help_page_helpee_unique_id"

/**
 * Activity used to display information about a person in need, their location, the path to them,
 * the time to get there, and what help they need
 * The user can then accept to help them (or not)
 */
class HelpPageActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityHelpPageBinding

    //TODO : currently, the destination is hardcoded, this will change with the task allowing
    // nearby helpers to go and help people in need (in which case the destination will be the
    // location of the user in need)
    private var destinationLat = 46.519
    private var destinationLong = 6.667
    private var currentLong: Double = 0.0
    private var currentLat: Double = 0.0
    private var helpUniqueId: String = "39"

    // TODO : this is only for displaying purposes, helpRequired will be initialized to null when
    //  we use this activity to retrieve actual helping requests
    // helpRequired contains strings for each medication / specific help required by the user in
    // need e.g. Epipen, CPR
    private var helpRequired: List<String>? = listOf("Epipen", "CPR")
    private lateinit var apiHelper: GoogleAPIHelper

    // Map fragment displayed
    private lateinit var mapsFragment: MapsFragment

    // Conversation with the person in need of help (only if the user accepts to help them)
    private var conversation: Conversation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext)

        // Displaying the activity layout
        binding = ActivityHelpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bundle = this.intent.extras
        helpRequired = bundle?.getStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS) ?: helpRequired
        destinationLat = bundle?.getDouble(EXTRA_DESTINATION_LAT) ?: destinationLat
        destinationLong = bundle?.getDouble(EXTRA_DESTINATION_LONG) ?: destinationLong
        helpUniqueId = bundle?.getString(EXTRA_HELP_UNIQUE_ID) ?: helpUniqueId

        // Obtain the map fragment
        mapsFragment = supportFragmentManager
            .findFragmentById(R.id.mapHelpPage) as MapsFragment

        // Initializes the API helper
        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key))

        // Initialize the current user's location
        setupLocation()

        // Displays the path to the user in need on the map fragment and, when the path has been
        // retrieved (through a google directions API request), computes and displays the time to
        // get to the user in need
        apiHelper.displayWalkingPath(
            currentLat,
            currentLong,
            destinationLat,
            destinationLong,
            mapsFragment
        ) { mapData: String? -> displayPathDuration(mapData) }
        displayRequiredMeds()
    }


    /**
     * Initializes the user's current location or returns to the main page in case a mistake occured
     * during the location information retrieval
     */
    private fun setupLocation() {
        val currentLocation = GeneralLocationManager.get().getCurrentLocation(this)
        if (currentLocation != null) {
            currentLat = currentLocation.latitude
            currentLong = currentLocation.longitude
        } else {
            // In case the permission to access the location is missing
            goToActivity(MainPageActivity::class.java)
        }
    }

    /**
     * Displays the entire duration of a path from one point to another
     * @param pathData The string returned by a call to the Directions API
     */
    private fun displayPathDuration(pathData: String?) {
        val duration = pathData?.let { apiHelper.parseTask(it, GDurationJSONParser) }

        val walkingTimeInfo: TextView = findViewById(R.id.timeToPersonInNeed)
        walkingTimeInfo.text = String.format("- %s", duration)
    }

    /**
     * Displays the medication / help required by the user in need
     */
    private fun displayRequiredMeds() {
        helpRequired?.let { medication ->
            val helpRequiredText: TextView = findViewById(R.id.helpRequired)

            val stringBuilder: StringBuilder = StringBuilder()
            // helpRequired contains strings corresponding to any medication / specific help the person
            // in need requires
            for (med in medication) {
                stringBuilder.append("- ")
                stringBuilder.append(med)
                stringBuilder.appendLine()
            }
            helpRequiredText.text = stringBuilder.toString()
        }
    }

    /**
     * Accepts the help requests and initialises a conversation with the person in need of help
     */
    fun acceptHelpRequest(view: View) {
        if (conversation == null) {
            val conversationIdsDb = databaseOf(Databases.CONVERSATION_IDS)
            val messagesDb = databaseOf(Databases.MESSAGES)
            fun onComplete(uniqueId: String?) {
                uniqueId?.let {
                    conversationIdsDb.addToObjectsList(helpUniqueId, Int::class.java, it.toInt())
                    conversation = Conversation(it, messagesDb, HELPER)

                    //TODO : remove
                    conversation?.sendMessage("Message from Helper")
                }
            }
            conversationIdsDb.incrementAndGet(UNIQUE_CONVERSATION_ID, 1) { onComplete(it) }
        }
    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    fun goToMainPage(view: View) {
        goToActivity(MainPageActivity::class.java)
    }

}