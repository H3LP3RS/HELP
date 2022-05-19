package com.github.h3lp3rs.h3lp.messaging

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.EXTRA_EMERGENCY_KEY
import com.github.h3lp3rs.h3lp.EXTRA_USER_ROLE
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.messaging.Conversation.Companion.createAndSendKeyPair
import com.github.h3lp3rs.h3lp.messaging.Messenger.HELPEE
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import java.util.*


class RecentMessagesActivity : AppCompatActivity() {
    private var helpeeId : String? = null
    private val adapter = GroupAdapter<ViewHolder>()

    // Database where all the conversation ids generated by helpers will be stored
    private val conversationIdsDb = databaseOf(Databases.CONVERSATION_IDS)
    private val messagesDatabase = databaseOf(Databases.MESSAGES)

    /**
     * The map is a synchronizedMap to allow for several conversations to be concurrently added
     * to it
     */
    private var idToItem = Collections.synchronizedMap<String, HelperConversation>(mutableMapOf())

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        val bundle = this.intent.extras
        helpeeId = bundle?.getString(EXTRA_EMERGENCY_KEY) ?: helpeeId

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as HelperConversation
            val intent = Intent(view.context, ChatActivity::class.java)
            // This is needed to differentiate between sent and received text messages. It will be
            // compared to the value of Messenger received in a conversation.
            // If the chat activity was launched from the latest messages activity, we know the user
            // is a person requesting help
            intent.putExtra(EXTRA_USER_ROLE, HELPEE)
            intent.putExtra(EXTRA_CONVERSATION_ID, userItem.getConversationId())
            startActivity(intent)
        }
        recyclerview_latest_messages.adapter = adapter

        listenForHelpers()
        listenForRemovedHelpers()
    }

    private fun listenForHelpers() {
        /**
         * Adds the corresponding conversation to the view when a helper sends a unique conversation
         * id to the helpee
         * @param conversationId The corresponding conversation id
         */
        fun onChildAdded(conversationId : String) {
            val helper = HelperConversation(conversationId)
            adapter.add(helper)

            // Create the key pair used to encrypt the conversation from end-to-end
            createAndSendKeyPair(conversationId, HELPEE)

            idToItem[conversationId] = helper
        }
        // Reference to the database of the conversation ids send by the helpers who agreed to
        // provide help

        conversationIdsDb.addEventListener(helpeeId,
            String::class.java,
            { id -> run { onChildAdded(id) } },
            {})
    }

    /**
     * Displays a message using snackbar
     * @param message message to display
     */
    private fun displayMessage(message : String) {
        Toast.makeText(
            applicationContext, message, Toast.LENGTH_LONG
        ).show()
    }

    private fun listenForRemovedHelpers() {
        /**
         * Event listener that handles deleting the conversation from the view upon deletion
         * from the database
         * @param key The key of the element that has been deleted
         */

        fun onChildRemoved(key : String) {
            // If the key is the conversation Id, that means that the user deleted the current
            // conversation
            idToItem[key]?.let {
                adapter.remove(it)
                idToItem.remove(key)
                displayMessage(getString(R.string.helper_cancelled))
            }
        }
        messagesDatabase.addEventListener(null, String::class.java, null) { key ->
            run {
                onChildRemoved(
                    key
                )
            }
        }
    }

    /**
     * Class representing a conversation layout of a helper who accepted to provide medical assistance.
     * @param conversationId The corresponding conversationId needed to send to the ChatActivity
     */
    class HelperConversation(private val conversationId : String) : Item<ViewHolder>() {
        override fun bind(viewHolder : ViewHolder, position : Int) {
            viewHolder.itemView.textview_username.text =
                Messenger.HELPER.toString().plus(position + 1)
        }

        override fun getLayout() : Int {
            return R.layout.latest_message_row
        }

        fun getConversationId() : String {
            return conversationId
        }
    }
}