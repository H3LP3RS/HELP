package com.github.h3lp3rs.h3lp.messaging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.h3lp3rs.h3lp.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import com.github.h3lp3rs.h3lp.messaging.Messenger.HELPEE

private const val EXTRA_CONVERSATION_id = "conversation_id"
private const val EXTRA_USER_ROLE = "user_role"

class LatestMessagesActivity : AppCompatActivity() {
    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        recyclerview_latest_messages.adapter = adapter
        adapter.setOnItemClickListener { item, view ->
            // val userItem = item as HelperConversation
            val intent = Intent(view.context, ChatActivity::class.java)
            // This is needed to differentiate between sent and received text messages. It will be
            // compared  the Messenger received in a conversation.
            // If the chat activity was launched from the latest messages activity, we know the user
            // is a person requesting help.
            intent.putExtra(EXTRA_USER_ROLE, HELPEE)
            // TODO replace with actual conversation id
            intent.putExtra(EXTRA_CONVERSATION_id,"conversation")
            startActivity(intent)
        }
        addNewHelper()
        addNewHelper()
    }

    /**
     * Add a new conversation item to the view everytime a user accepts to provide help and sends
     * the conversation id
     */
     fun addNewHelper(){
        adapter.add(HelperConversation(""))
    }

    /**
     * Class representing a conversation layout of a helper who accepted to provide medical assistance.
     */
    class HelperConversation(val message: String): Item<ViewHolder>(){
        override fun bind(viewHolder : ViewHolder, position : Int) {

        }

        override fun getLayout() : Int {
           return R.layout.latest_message_row
        }

    }
}