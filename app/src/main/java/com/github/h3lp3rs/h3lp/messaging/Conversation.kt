package com.github.h3lp3rs.h3lp.messaging

import android.util.Log
import com.github.h3lp3rs.h3lp.database.Database

class Conversation(
    private val conversationId: String,
    private val currentMessenger: Messenger,
    private val database: Database
) {
    var messages: MutableList<Message> = mutableListOf()

    init {
        database.addListListener(conversationId, Message::class.java) {
            messages = it.toMutableList()
            Log.i(
                "MSSG",
                it.toString()
            )
        }
    }

    fun sendMessage(messageText: String) {
        val message = Message(currentMessenger, messageText)
        database.addToObjectsList(conversationId, Message::class.java, message)
    }

    companion object {
        private const val MESSAGES = "messages"
    }
}