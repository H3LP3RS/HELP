package com.github.h3lp3rs.h3lp.messaging

import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.MESSAGES

/**
 * Object representing a conversation.
 * @param conversationId The unique conversation id of the conversation, shared by both parties of
 * the conversation, it is the place this conversation is stored in the database
 * @param currentMessenger The user that launched the conversation, used to differentiate between
 *  the user that launched the chat and the other user, for example to display the messages with
 *  matching Messenger as sent by the current user
 */
class Conversation(
    val conversationId: String,
    private val currentMessenger: Messenger
) {
    private val database = databaseOf(MESSAGES)

    /**
     * Sends a message from the current user to the database
     * @param messageText The message text
     */
    fun sendMessage(messageText: String) {
        val message = Message(currentMessenger, messageText)
        database.addToObjectsListConcurrently(conversationId, Message::class.java, message)
    }

    /**
     * Adds a listener on the conversation, the listener is triggered every time a new message is sent to the
     * conversation
     * @param onNewMessage Callback called on every new message
     */
    fun addListener(onNewMessage: (messages: List<Message>, currentMessenger: Messenger) -> Unit) {
        database.addListListener(conversationId, Message::class.java) {
            onNewMessage(it.toList(), currentMessenger)
        }

    }

    /**
     * Deletes the conversation from the database
     */
    fun deleteConversation() {
        database.delete(conversationId)
    }

    companion object {
        // Key where we store and get the latest unique conversation id in the database, this allows
        // for concurrent accesses to always get a new id
        const val UNIQUE_CONVERSATION_ID = "unique conversation id"
    }
}