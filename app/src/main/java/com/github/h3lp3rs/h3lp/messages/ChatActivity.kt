package com.github.h3lp3rs.h3lp.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.github.h3lp3rs.h3lp.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_receiver.view.*
import kotlinx.android.synthetic.main.chat_sender.view.*


class ChatActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        /*
        adapter.add(SenderMessage(""))
        adapter.add(ReceiverMessage(""))
        adapter.add(SenderMessage(""))
        adapter.add(ReceiverMessage(""))
         */
        recycler_view_chat.adapter= adapter

        button_send_message.setOnClickListener{
            val text = text_view_enter_message.text.toString()
            sendTextMessage(text)
            // TODO remove and add listener on to messages as well
            adapter.add(SenderMessage(text))
        }
    }
    private fun sendTextMessage(message: String){
        // TODO send message to firebase fromID to toID
    }
}
class SenderMessage(private val message:String): Item<ViewHolder>(){
    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.text_view_sender.text = message
    }

    override fun getLayout() : Int {
        return R.layout.chat_sender
    }
}
class ReceiverMessage(private val message:String): Item<ViewHolder>(){
    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.text_view_receiver.text = message
    }

    override fun getLayout() : Int {
        return R.layout.chat_receiver
    }
}