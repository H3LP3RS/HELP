package com.github.h3lp3rs.h3lp

import LocationHelper
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.databinding.ActivityHelpPageBinding
import com.github.h3lp3rs.h3lp.databinding.ActivityHelpPageBinding.*
import com.github.h3lp3rs.h3lp.dataclasses.EmergencyInformation
import com.github.h3lp3rs.h3lp.dataclasses.Helper
import com.github.h3lp3rs.h3lp.messaging.ChatActivity
import com.github.h3lp3rs.h3lp.messaging.Conversation
import com.github.h3lp3rs.h3lp.messaging.Conversation.Companion.UNIQUE_CONVERSATION_ID
import com.github.h3lp3rs.h3lp.messaging.Conversation.Companion.createAndSendKeyPair
import com.github.h3lp3rs.h3lp.messaging.EXTRA_CONVERSATION_ID
import com.github.h3lp3rs.h3lp.messaging.Messenger.HELPER
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.util.GDurationJSONParser
import com.google.android.gms.maps.MapsInitializer.*
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
class HelperPageActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    // UI Initialization
    private lateinit var binding: ActivityHelpPageBinding
    private lateinit var apiHelper: GoogleAPIHelper
    private lateinit var mapsFragment: MapsFragment

    private val locationHelper = LocationHelper()

    // Helper connection with helpee data
    private lateinit var conversation: Conversation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(applicationContext)

        // Displaying the activity layout
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        mapsFragment = supportFragmentManager.findFragmentById(R.id.mapHelpPage) as MapsFragment
        apiHelper = GoogleAPIHelper(resources.getString(R.string.google_maps_key), applicationContext)

        // Bundle cannot be empty
        val bundle = this.intent.extras!!

        val emergencyId = bundle.getString(EXTRA_EMERGENCY_KEY)!!

        val destinationLat = bundle.getDouble(EXTRA_DESTINATION_LAT)
        val destinationLong = bundle.getDouble(EXTRA_DESTINATION_LONG)

        // Require user to sign in
        if(userUid == null) {
            showSignInPopUp()
        }

        // Initialize the current user's location
        locationHelper.requireAndHandleCoordinates(this) {

            // Displays the path to the user in need on the map fragment and, when the path has been
            // retrieved (through a google directions API request), computes and displays the time to
            // get to the user in need
            apiHelper.displayWalkingPath(
                it.latitude, it.longitude, destinationLat, destinationLong, mapsFragment
            ) { mapData: String? -> displayPathDuration(mapData) }
        }

        val medicationRequired = bundle.getStringArrayList(EXTRA_HELP_REQUIRED_PARAMETERS)!!
        displayRequiredMeds(medicationRequired)

        // Initially the contact button is hidden, only after the user accepts the request does it
        // becomes visible.
        locationHelper.requireAndHandleCoordinates(this) { location ->
            val latitude = location.latitude
            val longitude = location.longitude
            button_accept.setOnClickListener { acceptHelpRequest(emergencyId, latitude, longitude) }
        }

        button_reject.setOnClickListener { goToMainPage() }

        setUpEmergencyCancellation(emergencyId)
    }

    /**
     * Opens a popup asking the user to sign in to continue.
     */
    private fun showSignInPopUp() {
        val dialog = Dialog(this)
        val signInPopup =
            layoutInflater.inflate(R.layout.sign_in_required_pop_up, null)

        dialog.setCancelable(false)
        dialog.setContentView(signInPopup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.create()

        // Cancel button
        signInPopup.findViewById<Button>(R.id.close_popup_button)
            .setOnClickListener {
                dialog.dismiss()
                goToMainPage()
            }

        // Sign in button
        signInPopup.findViewById<Button>(R.id.sign_in_popup_button)
            .setOnClickListener {
                dialog.dismiss()
                goToActivity(SignInActivity::class.java)
            }

        dialog.show()
    }

    /**
     * Displays the entire duration of a path from one point to another
     * @param pathData The string returned by a call to the Directions API
     */
    private fun displayPathDuration(pathData: String?) {
        val duration = pathData?.let { apiHelper.parseTask(it, GDurationJSONParser) }
        duration?.let {
            val walkingTimeInfo: TextView = findViewById(R.id.timeToPersonInNeed)
            walkingTimeInfo.text = String.format("- %s", it)
        }
    }

    /**
     * Displays the medication / help required by the user in need
     * @param medication The medication that the user in need specified they needed
     */
    private fun displayRequiredMeds(medication: ArrayList<String>) {
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

    /**
     * Accepts the help requests and initialises a conversation with the person in need of help
     * (see CommunicationProtocol.md for a detailed explanation)
     * @param emergencyId The unique id of the current emergency
     * @param currentLat The user's current latitude
     * @param currentLong The user's current longitude
     */
    private fun acceptHelpRequest(emergencyId: String, currentLat: Double, currentLong: Double) {
        val emergencyDb = databaseOf(EMERGENCIES)
        emergencyDb.getObject(emergencyId, EmergencyInformation::class.java).thenApply {
            // Add the helper to the list of helpers
            val me = Helper(userUid!!, currentLat, currentLong)
            val helpers = ArrayList<Helper>(it.helpers)
            helpers.add(me)
            // Stop listening to other emergencies
            databaseOf(NEW_EMERGENCIES).clearAllListeners()

            // TODO: Here we can potentially periodically update the GPS coordinates
            // Update the value to notify that we are coming
            databaseOf(EMERGENCIES).setObject(
                emergencyId,
                EmergencyInformation::class.java,
                it.copy(helpers = helpers)
            )
            // Init chat
            initChat(emergencyId)
        }.exceptionally { goToMainPage() } // Expired

        // If the user accepts to help, he can change his mind and cancel later
        button_reject.setOnClickListener { conversation.deleteConversation() }
    }

    /**
     * Initialises a chat between the user (the helper here) and the helpee
     * @param emergencyId The emergency id (used as a point on the database to communicate a unique
     * conversation id between each helper / helpee pair)
     */
    private fun initChat(emergencyId: String) {
        val conversationIdsDb = databaseOf(CONVERSATION_IDS)
        conversationIdsDb.incrementAndGet(UNIQUE_CONVERSATION_ID, 1).thenApply {
            // Sending the conversation id to the person in need of help (share the
            // conversation id)
            conversationIdsDb.addToObjectsListConcurrently(emergencyId, Int::class.java, it)

            // Create the key pair used to encrypt the conversation from end-to-end
            createAndSendKeyPair(it.toString(), HELPER)

            // Creating a conversation on that new unique conversation id
            conversation = Conversation(it.toString(), HELPER)
        }
        // Once the user accepts to help, the accept button disappears and he is able to
        // start conversations with the person who requested help.
        button_accept.setImageResource(R.drawable.chat)
        button_accept.setOnClickListener { goToChatActivity() }
    }

    private fun goToChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        // This is needed to differentiate between sent and received text messages. It will be
        // compared to the Messenger value received in a conversation.
        // If the chat activity was launched from the help page activity, we know the user is a
        // helper.
        intent.putExtra(EXTRA_USER_ROLE, HELPER)
        intent.putExtra(EXTRA_CONVERSATION_ID, conversation.conversationId)
        startActivity(intent)
    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    private fun goToMainPage() {
        goToActivity(MainPageActivity::class.java)
    }

    private fun setUpEmergencyCancellation(emergencyId: String) {
        fun onChildRemoved(id: String) {
            if (id == emergencyId) {
                // If the person the user is trying to help has cancelled his emergency, the
                // conversation is deleted from the database and the helper is redirected to the
                // main page
                conversation.deleteConversation()
                goToActivity(MainPageActivity::class.java)
            }
        }
        // The event is added to the entire conversation IDS database and so no child key is needed
        databaseOf(CONVERSATION_IDS).addEventListener(
            null,
            String::class.java, null,
        ) { id -> run { onChildRemoved(id) } }
    }
}