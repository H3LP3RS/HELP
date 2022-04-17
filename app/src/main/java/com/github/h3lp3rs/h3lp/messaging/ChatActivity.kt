package com.github.h3lp3rs.h3lp.messaging

import Messenger
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_receiver.view.*
import kotlinx.android.synthetic.main.chat_sender.view.*


class ChatActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()
    private lateinit var userRole : Messenger
    private lateinit var conversationId : String

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        userRole = intent.getSerializableExtra(EXTRA_USER_ROLE) as Messenger
        conversationId = intent.getSerializableExtra(EXTRA_CONVERSATION_ID) as String

        adapter.add(SenderMessage("M1"))
        adapter.add(ReceiverMessage("R1"))
        adapter.add(SenderMessage("M2"))
        adapter.add(ReceiverMessage("R2"))

        recycler_view_chat.adapter = adapter

        button_send_message.setOnClickListener {
            val text = text_view_enter_message.text.toString()
            // When the user clicks on send, the message is sent to the database and shown in the
            // view.
            sendTextMessage(text)
            // TODO remove and add listener on to incoming messages as well
            adapter.add(SenderMessage(text))
            // Clears the text field when the user sends the message
            text_view_enter_message.text.clear()
        }
    }

    private fun sendTextMessage(message : String) {
        // TODO send message to firebase fromID to toID
    }
}

/**
 * Class representing the current user's text messages.
 */
class SenderMessage(private val message : String) : Item<ViewHolder>() {
    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.text_view_sender.text = message
    }

    override fun getLayout() : Int {
        return R.layout.chat_sender
    }
}

/**
 * Class representing the person the user is trying to communicate with.
 */
class ReceiverMessage(private val message : String) : Item<ViewHolder>() {
    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.text_view_receiver.text = message
    }

    override fun getLayout() : Int {
        return R.layout.chat_receiver
    }
}