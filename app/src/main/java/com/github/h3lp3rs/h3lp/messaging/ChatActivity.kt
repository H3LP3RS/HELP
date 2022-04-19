package com.github.h3lp3rs.h3lp.messaging
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.EXTRA_DESTINATION_LAT
import com.github.h3lp3rs.h3lp.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_receiver.view.*
import kotlinx.android.synthetic.main.chat_sender.view.*

private const val EXTRA_CONVERSATION_ID = "conversation_id"
class ChatActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()
    private lateinit var userRole : Messenger
    private var conversationId : String? = null
    val receiverLayout = R.layout.chat_receiver
    val senderLayout = R.layout.chat_sender

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // This is to be able to know whether a message is meant to be in the right or left layout.
        userRole = intent.getSerializableExtra(EXTRA_USER_ROLE) as Messenger

        val bundle = this.intent.extras
        // The conversation id to which to add messages to
        conversationId = bundle?.getString(EXTRA_CONVERSATION_ID) ?: conversationId

        adapter.add(MessageLayout("Hellooo!",senderLayout))

        recycler_view_chat.adapter = adapter

        button_send_message.setOnClickListener {
            val text = text_view_enter_message.text.toString()
            // When the user clicks on send, the message is sent to the database and shown in the
            // view.
            sendTextMessage(text)
            // TODO remove and add listener on to incoming messages as well
            adapter.add(MessageLayout(text,senderLayout))
            // Clears the text field when the user sends the message
            text_view_enter_message.text.clear()
        }
    }

    private fun sendTextMessage(message : String) {
        // TODO send message to firebase fromID to toID
    }
}

/**
 * Class representing the layout of the user's text messages.
 */
private class MessageLayout(private val message : String, private val layout: Int ) : Item<ViewHolder>() {
    val senderLayout = R.layout.chat_sender

    override fun bind(viewHolder : ViewHolder, position : Int) {
        if(layout== senderLayout)
            viewHolder.itemView.text_view_sender.text = message
        else
            viewHolder.itemView.text_view_receiver.text = message
    }

    override fun getLayout() : Int {
        return layout
    }
}
