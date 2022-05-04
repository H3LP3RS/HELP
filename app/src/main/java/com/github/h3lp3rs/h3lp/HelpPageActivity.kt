package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.CONVERSATION_IDS
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.databinding.ActivityHelpPageBinding
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.Helper
import com.github.h3lp3rs.h3lp.locationmanager.GeneralLocationManager
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.messaging.Conversation
import com.github.h3lp3rs.h3lp.messaging.Conversation.Companion.UNIQUE_CONVERSATION_ID
import com.github.h3lp3rs.h3lp.messaging.Messenger.HELPER
import com.github.h3lp3rs.h3lp.messaging.*
import com.github.h3lp3rs.h3lp.util.GDurationJSONParser
import com.google.android.gms.maps.MapsInitializer
import kotlinx.android.synthetic.main.activity_help_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

const val EXTRA_HELP_REQUIRED_PARAMETERS = "help_page_required_key"
const val EXTRA_DESTINATION_LAT = "help_page_destination_lat"
const val EXTRA_DESTINATION_LONG = "help_page_destination_long"
const val EXTRA_USER_ROLE = "user_role"

/**
 * Activity used to display information about a person in need, their location, the path to them,
 * the time to get there, and what help they need
 * The user can then accept to help them (or not)
 */
class HelpPageActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding : ActivityHelpPageBinding

    private var destinationLat = 46.519
    private var destinationLong = 6.667
    private var currentLong : Double = 0.0
    private var currentLat : Double = 0.0

    // TODO : again, this is hardcoded for testing purposes but it will be removed (and initialized
    //  to null after the linking of activities)
    private var helpeeId : String = "test_end_to_end"

    // helpRequired contains strings for each medication / specific help required by the user in
    // need e.g. Epipen, CPR
    private var helpRequired: List<String>? = null
    private lateinit var apiHelper: GoogleAPIHelper
    private var helpId: String? = null

    // Map fragment displayed
    private lateinit var mapsFragment : MapsFragment

    // Conversation with the person in need of help (only if the user accepts to help them)
    private var conversation : Conversation? = null
    private var conversationId: String? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(applicationContext)

        // Displaying the activity layout
        binding = ActivityHelpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = this.intent.extras
        helpId = bundle?.getString(EXTRA_EMERGENCY_KEY) ?: helpId
        helpRequired = bundle?.getStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS) ?: helpRequired
        destinationLat = bundle?.getDouble(EXTRA_DESTINATION_LAT) ?: destinationLat
        destinationLong = bundle?.getDouble(EXTRA_DESTINATION_LONG) ?: destinationLong
        helpeeId = bundle?.getString(EXTRA_HELPEE_ID) ?: helpeeId

        // Obtain the map fragment
        mapsFragment = supportFragmentManager.findFragmentById(R.id.mapHelpPage) as MapsFragment

        // Initializes the API helper
        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key))

        // Initialize the current user's location
        setupLocation()

        // Displays the path to the user in need on the map fragment and, when the path has been
        // retrieved (through a google directions API request), computes and displays the time to
        // get to the user in need
        apiHelper.displayWalkingPath(
            currentLat, currentLong, destinationLat, destinationLong, mapsFragment
        ) { mapData : String? -> displayPathDuration(mapData) }
        displayRequiredMeds()

        // Initially the contact button is hidden, only after the user accepts the request does it
        // becomes visible.
        button_accept.setOnClickListener{ acceptHelpRequest() }
        button_reject.setOnClickListener{ goToMainPage() }
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
    private fun displayPathDuration(pathData : String?) {
        val duration = pathData?.let { apiHelper.parseTask(it, GDurationJSONParser) }

        val walkingTimeInfo : TextView = findViewById(R.id.timeToPersonInNeed)
        walkingTimeInfo.text = String.format("- %s", duration)
    }

    /**
     * Displays the medication / help required by the user in need
     */
    private fun displayRequiredMeds() {
        helpRequired?.let { medication ->
            val helpRequiredText : TextView = findViewById(R.id.helpRequired)

            val stringBuilder : StringBuilder = StringBuilder()
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
     * (see CommunicationProtocol.md for a detailed explanation)
     */
    private fun acceptHelpRequest() {
        if(helpId == null) {
            goToMainPage()
            return
        }
        databaseOf(EMERGENCIES).getObject(helpId!!, EmergencyInformation::class.java).thenApply {
            // Add the helper to the list of helpers
            val me = Helper(userUid!!, currentLat, currentLong)
            val helpers = ArrayList<Helper>(it.helpers)
            if(!helpers.contains(me)) {
                helpers.add(me)
            }
            // Stop listening to other emergencies
            databaseOf(NEW_EMERGENCIES).clearAllListeners()
            // TODO: Here we can potentially periodically update the GPS coordinates
            // Update the value to notify that we are coming
            databaseOf(EMERGENCIES).setObject(helpId!!, EmergencyInformation::class.java, it.copy(helpers = helpers))
            // Init chat
            initChat()
        }.exceptionally { goToMainPage() } // Expired
    }

    private fun initChat() {
        val conversationIdsDb = databaseOf(CONVERSATION_IDS)

        /**
         * Callback function which gets a unique conversation id, shares it with the person in
         * need of help and instantiates a conversation on that id
         * @param uniqueId The unique conversation id
         */
        fun onComplete(uniqueId : Int?) {
            uniqueId?.let {
                // Sending the conversation id to the person in need of help (share the
                // conversation id)
                conversationIdsDb.addToObjectsListConcurrently(helpeeId, Int::class.java, it)

                // Creating a conversation on that new unique conversation id
                conversation = Conversation(it.toString(), HELPER, this)
                conversationId = it.toString()
            }
        }
        // Gets a new conversation id atomically (to avoid 2 helpers getting the same) then
        // calls the callback
        conversationIdsDb.incrementAndGet(UNIQUE_CONVERSATION_ID, 1) { onComplete(it) }
        // Once the user accepts to help, the accept button disappears and he is able to
        // start conversations with the person who requested help.
        button_accept.setImageResource(R.drawable.chat)
        button_accept.setOnClickListener{goToChatActivity()}
    }

    private fun goToChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        // This is needed to differentiate between sent and received text messages. It will be
        // compared to the Messenger value received in a conversation.
        // If the chat activity was launched from the help page activity, we know the user is a
        // helper.
        intent.putExtra(EXTRA_USER_ROLE, HELPER)
        intent.putExtra(EXTRA_CONVERSATION_ID, conversationId)
        startActivity(intent)
    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName : Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    private fun goToMainPage() {
        goToActivity(MainPageActivity::class.java)
    }
}