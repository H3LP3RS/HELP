package com.github.h3lp3rs.h3lp.messaging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.EXTRA_USER_ROLE
import com.github.h3lp3rs.h3lp.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_receiver.view.*
import kotlinx.android.synthetic.main.chat_sender.view.*

const val EXTRA_CONVERSATION_ID = "conversation_id"

class ChatActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()

    private var userRole : Messenger? = null
    private var conversationId : String? = null
    private lateinit var conversation : Conversation

    private val receiverLayout = R.layout.chat_receiver
    private val senderLayout = R.layout.chat_sender

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // This is to be able to know whether a message is meant to be in the right or left layout.
        val user = intent.getSerializableExtra(EXTRA_USER_ROLE)
        if (user != null) userRole = user as Messenger

        // The conversation id used to send text messages on the database
        conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID) ?: conversationId
        conversation = Conversation(conversationId!!, userRole!!)

        recycler_view_chat.adapter = adapter

        conversation.addListener { messages, messenger ->
            if (messages.isNotEmpty()) {
                val message = messages.last()
                if (message.messenger == messenger) adapter.add(
                    MessageLayout(
                        message.message,
                        senderLayout
                    )
                )
                else adapter.add(MessageLayout(message.message, receiverLayout))
            }
        }
        button_send_message.setOnClickListener {
            val text = text_view_enter_message.text.toString()
            // When the user clicks on send, the message is sent to the database
            conversation.sendMessage(text)
            // Clear the text field when the user hits send
            text_view_enter_message.text.clear()
        }
    }

}

/**
 * Class representing the layout of the user's text messages.
 */
private class MessageLayout(private val message : String, private val layout : Int) :
    Item<ViewHolder>() {

    override fun bind(viewHolder : ViewHolder, position : Int) {
        if (layout == R.layout.chat_sender) viewHolder.itemView.text_view_sender.text = message
        else viewHolder.itemView.text_view_receiver.text = message
    }

    override fun getLayout() : Int {
        return layout
    }
}
