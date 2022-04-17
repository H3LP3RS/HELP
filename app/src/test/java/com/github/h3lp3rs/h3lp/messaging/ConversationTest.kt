package com.github.h3lp3rs.h3lp.messaging

import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.messaging.Messenger.HELPER
import junit.framework.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class ConversationTest {
    private val CONVERSATION_ID = "ID"
    private val TEST_SEED = Random(0)
    private val BYTES_PER_CHAR = 2
    private val MESSENGER = HELPER

    private lateinit var db: Database
    private lateinit var conversation: Conversation


    @Before
    fun setup() {
        db = MockDatabase()
        conversation = Conversation(CONVERSATION_ID, db, MESSENGER)
    }

    @Test
    fun sendMessageSendsMessageOnDb() {
        val string = TEST_SEED.nextBytes(5 * BYTES_PER_CHAR).toString()
        val expectedMessage = Message(MESSENGER, string)
        var expectedMessageWasSent = false

        conversation.sendMessage(string)

        db.addListListener(CONVERSATION_ID, Message::class.java) {
            if (it.size == 1 && it[0] == expectedMessage)
                expectedMessageWasSent = true
        }
        assertTrue(expectedMessageWasSent)
    }

    @Test
    fun addListenerTriggeredOnNewMessage() {
        val string = TEST_SEED.nextBytes(5 * BYTES_PER_CHAR).toString()
        val expectedMessage = Message(MESSENGER, string)
        var expectedMessageWasSent = false

        conversation.addListener { messages, curMessenger ->
            if (messages.size == 1 && messages[0] == expectedMessage && curMessenger == MESSENGER)
                expectedMessageWasSent = true
        }
        conversation.sendMessage(string)

        assertTrue(expectedMessageWasSent)
    }

}