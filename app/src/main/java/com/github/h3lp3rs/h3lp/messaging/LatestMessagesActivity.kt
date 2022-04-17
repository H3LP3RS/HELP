package com.github.h3lp3rs.h3lp.messaging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.h3lp3rs.h3lp.AwaitHelpActivity
import com.github.h3lp3rs.h3lp.EXTRA_CALLED_EMERGENCIES
import com.github.h3lp3rs.h3lp.EXTRA_NEEDED_MEDICATION
import com.github.h3lp3rs.h3lp.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

const val EXTRA_CONVERSATION_ID = "conversation_id"
const val EXTRA_USER_ROLE = "user_role"

class LatestMessagesActivity : AppCompatActivity() {
    private val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        recyclerview_latest_messages.adapter = adapter
        adapter.setOnItemClickListener { item, view ->

            val userItem = item as HelperConversation

            val intent = Intent(view.context, ChatActivity::class.java)
            val bundle = Bundle()
            // TODO replace with Alex's enum
            bundle.putString(EXTRA_USER_ROLE, "Helpee")
            // TODO replace with actual conversation id
            bundle.putString(EXTRA_CONVERSATION_ID,"conversation_id")
            intent.putExtras(bundle)
            startActivity(intent)

        }
    }

    fun addHelper(){
        adapter.add(HelperConversation("message"))
    }

    /**
     * Class representing a conversation with a helper who accepted to provide medical assistance.
     */
    class HelperConversation(val message: String): Item<ViewHolder>(){
        override fun bind(viewHolder : ViewHolder, position : Int) {

        }

        override fun getLayout() : Int {
           return R.layout.latest_message_row
        }

    }
}