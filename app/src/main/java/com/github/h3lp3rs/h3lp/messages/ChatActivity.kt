package com.github.h3lp3rs.h3lp.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.github.h3lp3rs.h3lp.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_receiver.view.*
import kotlinx.android.synthetic.main.chat_sender.view.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val adapter= GroupAdapter<ViewHolder>()
        adapter.add(SenderMessage())
        adapter.add(ReceiverMessage())
        adapter.add(SenderMessage())
        adapter.add(ReceiverMessage())
        findViewById<RecyclerView>(R.id.recycler_view_chat).adapter= adapter
    }
}
class SenderMessage: Item<ViewHolder>(){
    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.text_view_sender.text="Help me..."
    }

    override fun getLayout() : Int {
        return R.layout.chat_sender
    }
}
class ReceiverMessage: Item<ViewHolder>(){
    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.text_view_receiver.text="No..."
    }

    override fun getLayout() : Int {
        return R.layout.chat_receiver
    }
}