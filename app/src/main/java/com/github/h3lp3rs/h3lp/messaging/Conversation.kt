package com.github.h3lp3rs.h3lp.messaging

import com.github.h3lp3rs.h3lp.database.Database

class Conversation(
    private val conversationId: String,
    private val database: Database,
    private val currentMessenger: Messenger
) {

    fun sendMessage(messageText: String) {
        val message = Message(currentMessenger, messageText)
        database.addToObjectsList(conversationId, Message::class.java, message)
    }

    fun addListener(onNewMessage: (messages: List<Message>, currentMessenger: Messenger) -> Unit) {
        database.addListListener(conversationId, Message::class.java) {
            onNewMessage(it.toList(), currentMessenger)
        }
    }
}