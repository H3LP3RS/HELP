package com.github.h3lp3rs.h3lp.messaging

/**
 * To preserve anonymity in conversations, the only information stored in a message is the messenger
 * (to be able to distinguish between the current user and the person they are talking to) and the
 * actual message
 */
data class Message(val messenger: Messenger, val message: String)
