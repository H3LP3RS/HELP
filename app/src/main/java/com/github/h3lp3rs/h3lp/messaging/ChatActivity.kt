package com.github.h3lp3rs.h3lp.messaging

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.EXTRA_USER_ROLE
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_receiver.view.*
import kotlinx.android.synthetic.main.chat_sender.view.*

const val EXTRA_CONVERSATION_ID = "conversation_id"

class ChatActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()

    var userRole : Messenger? = null
    private var conversationId : String? = null
    private lateinit var conversation : Conversation
    private val messagesDatabase = databaseOf(Databases.MESSAGES)

    private val receiverLayout = R.layout.chat_receiver
    private val senderLayout = R.layout.chat_sender

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // This is to be able to know whether a message is meant to be in the right or left layout
        val user = intent.getSerializableExtra(EXTRA_USER_ROLE)
        if (user != null) userRole = user as Messenger

        // The conversation id used to send text messages on the database
        conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID) ?: conversationId
        conversation = Conversation(conversationId!!, userRole!!)

        recycler_view_chat.adapter = adapter

        button_send_message.setOnClickListener {
            val text = text_view_enter_message.text.toString()
            // When the user clicks on send, the message is sent to the database
            conversation.sendMessage(text)
            // Clears the text field when the user hits send
            text_view_enter_message.text.clear()
        }
        listenForMessages()
        onConversationDeletion()
    }

    private fun listenForMessages() {
        // Event listener that handles the received and sent text messages and correctly updates
        // the view
        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(p0 : DataSnapshot, p1 : String?) {
                val chatMessage =
                    Gson().fromJson(p0.getValue(String::class.java), Message::class.java)

                chatMessage?.let {
                    // Compare the messenger to the current user to correctly display the message
                    if (it.messenger == userRole) {
                        adapter.add(MessageLayout(it.message, senderLayout, it.messenger))
                    } else {
                        adapter.add(MessageLayout(it.message, receiverLayout, it.messenger))
                    }
                    // Scroll to the last message received or sent
                    recycler_view_chat.smoothScrollToPosition(adapter.itemCount - 1)
                }
            }

            override fun onCancelled(p0 : DatabaseError) {}
            override fun onChildChanged(p0 : DataSnapshot, p1 : String?) {}
            override fun onChildMoved(p0 : DataSnapshot, p1 : String?) {}
            override fun onChildRemoved(p0 : DataSnapshot) {}
        }
        // Reference to the database of the chat messages belonging to the current conversation
        val conversationDb = messagesDatabase.getDatabaseReference(conversationId.toString())
        // Add the event listener to the current conversation
        conversationDb.addChildEventListener(childEventListener)

    }

    private fun onConversationDeletion() {
        // Event listener that handles deleting the text messages from the view upon deletion
        // from the database
        val childEventListener = object : ChildEventListener {

            override fun onChildAdded(p0 : DataSnapshot, p1 : String?) {}
            override fun onCancelled(p0 : DatabaseError) {}
            override fun onChildChanged(p0 : DataSnapshot, p1 : String?) {}
            override fun onChildMoved(p0 : DataSnapshot, p1 : String?) {}

            override fun onChildRemoved(p0 : DataSnapshot) {
                val key = p0.key
                // If the key is the conversation Id, that means that the user deleted the current
                // conversation
                if (key == conversationId) {
                    // Remove all the messages from the view
                    adapter.clear()
                    displayMessage(getString(R.string.deleted_conversation_message))
                    // If the user had previously accepted to provide help, upon cancellation either
                    // from him or the helpee, he simply goes back to the main page of the app
                    if (userRole == Messenger.HELPER) backHome()
                    // If the user is a helpee, finish() allows him to go back to the latest
                    // messages activity
                    else finish()
                }
            }
        }
        // TODO is it better to have it on the conversation IDs db?
        // Reference to the database of the chat messages
        val conversationIdsDB = messagesDatabase.getDatabaseReference(null)
        conversationIdsDB.addChildEventListener(childEventListener)

    }

    /**
     * Displays a message using snackbar
     * @param message message to display
     */
    private fun displayMessage(message : String) {
        Toast.makeText(
            applicationContext, message, Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Send back to the MainPageActivity
     */
    private fun backHome() {
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
    }
}

/**
 * Class representing the layout of the user's text messages.
 */
private class MessageLayout(
    private val message : String, private val layout : Int, private val messenger : Messenger
) : Item<ViewHolder>() {

    override fun bind(viewHolder : ViewHolder, position : Int) {
        if (layout == R.layout.chat_sender) {
            viewHolder.itemView.text_view_sender.text = message
            viewHolder.itemView.sender_profile_picture.setImageResource(getProfilePicture())
        } else {
            viewHolder.itemView.text_view_receiver.text = message
            viewHolder.itemView.receiver_profile_picture.setImageResource(getProfilePicture())
        }
    }

    override fun getLayout() : Int {
        return layout
    }

    /**
     * Gets the profile picture of a user
     * @return the id of the picture
     */
    private fun getProfilePicture() : Int {
        return if (messenger == Messenger.HELPEE) R.drawable.helpee_profile_picture
        else R.drawable.helper_profile_picture
    }
}
