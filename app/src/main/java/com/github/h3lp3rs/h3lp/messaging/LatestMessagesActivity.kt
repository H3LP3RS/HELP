package com.github.h3lp3rs.h3lp.messaging

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.EXTRA_HELPEE_ID
import com.github.h3lp3rs.h3lp.EXTRA_USER_ROLE
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.messaging.Messenger.HELPEE
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*


class LatestMessagesActivity : AppCompatActivity() {
    private var helpeeId : String? = null
    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        val bundle = this.intent.extras
        helpeeId = bundle?.getString(EXTRA_HELPEE_ID) ?: helpeeId

        databaseOf(Databases.MESSAGES).setString("HEWIEWEWE", helpeeId!!)

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as HelperConversation
            val intent = Intent(view.context, ChatActivity::class.java)
            // This is needed to differentiate between sent and received text messages. It will be
            // compared to the value of Messenger received in a conversation.
            // If the chat activity was launched from the latest messages activity, we know the user
            // is a person requesting help.
            intent.putExtra(EXTRA_USER_ROLE, HELPEE)
            intent.putExtra(EXTRA_CONVERSATION_ID, userItem.getConversationId())
            startActivity(intent)
        }
        recyclerview_latest_messages.adapter = adapter

        // Database where all the conversation ids generated by helpers will be stored
        val conversationIdsDb = Databases.databaseOf(Databases.CONVERSATION_IDS)

        /*
         * Preparing to instantiate conversations by adding a listener on the helper id,
         * on every new conversation id added, we create a corresponding Conversation
         * object and add it to the conversations list
        */
        conversationIdsDb.addListListener(
            helpeeId!!, String::class.java
        ) { addNewConversation(it) }
    }

    /**
     * Adds the corresponding conversation to the view when a helper sends a unique conversation
     * id to the helpee
     * @param conversationIds The list of all current conversation ids, by definition of
     * database.addToObjectsList the new conversation id is stored at the end of the list
     */
    private fun addNewConversation(conversationIds : List<String>) {
        // This emptiness check is required since listener callbacks are called at initialisation
        // (and thus when the list of conversation ids is still empty
        if (conversationIds.isNotEmpty()) {
            // Only taking the new conversation id
            adapter.add(HelperConversation(conversationIds.last(), conversationIds.size))
        }
    }

    /**
     * Class representing a conversation layout of a helper who accepted to provide medical assistance.
     * @param conversationId The corresponding conversationId needed to send to the ChatActivity
     * @param index The index of the helper
     */
    class HelperConversation(private val conversationId : String, private val index : Int) :
        Item<ViewHolder>() {
        override fun bind(viewHolder : ViewHolder, position : Int) {
            viewHolder.itemView.textview_username.text = "Helper $index"
        }

        override fun getLayout() : Int {
            return R.layout.latest_message_row
        }

        fun getConversationId() : String {
            return conversationId
        }

    }
}