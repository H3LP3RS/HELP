//package com.github.h3lp3rs.h3lp
//
//import androidx.test.core.app.ApplicationProvider.getApplicationContext
//import com.github.h3lp3rs.h3lp.database.Database
//import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
//import com.github.h3lp3rs.h3lp.database.Databases.MESSAGES
//import com.github.h3lp3rs.h3lp.database.MockDatabase
//import com.github.h3lp3rs.h3lp.model.messaging.Conversation
//import com.github.h3lp3rs.h3lp.model.messaging.Message
//import com.github.h3lp3rs.h3lp.model.messaging.Messenger.HELPER
//import junit.framework.Assert.assertTrue
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.containsString
//import org.junit.Before
//import org.junit.Test
//import java.util.concurrent.CompletionException
//import java.util.concurrent.TimeUnit


// WAS A UNIT TEST => CAN NO LONGER BE DUE TO ANDROID KEY STORE

//class ConversationTest : H3lpAppTest() {
//    private val CONVERSATION_ID = "ID"
//    private val MESSAGE_TEXT = "Hello, how are you?"
//    private val MESSENGER = HELPER
//    private val EXPECTED_MESSAGE = Message(MESSENGER, MESSAGE_TEXT, "")
//
//    private lateinit var db: Database
//    private lateinit var conversation: Conversation
//
//
//
//    @Before
//    fun setup() {
//        db = MockDatabase()
//        setDatabase(MESSAGES, db)
//        conversation = Conversation(CONVERSATION_ID, MESSENGER, getApplicationContext())
//    }
//
//    @Test
//    fun sendMessageSendsMessageOnDb() {
//        var expectedMessageWasSent = false
//
//        conversation.sendMessage(MESSAGE_TEXT)
//
//        db.addListListener(CONVERSATION_ID, Message::class.java) {
//            if (it.size == 1 && it[0].message == MESSAGE_TEXT && it[0].messenger == MESSENGER)
//                expectedMessageWasSent = true
//        }
//        assertTrue(expectedMessageWasSent)
//    }
//
//    @Test
//    fun addListenerTriggeredOnNewMessage() {
//        var expectedMessageWasSent = false
//
//        conversation.addListListener { messages, curMessenger ->
//            if (messages.size == 1 && messages[0] == EXPECTED_MESSAGE && curMessenger == MESSENGER)
//                expectedMessageWasSent = true
//        }
//
//        conversation.sendMessage(MESSAGE_TEXT)
//
//        assertTrue(expectedMessageWasSent)
//    }
//
//    @Test
//    fun deleteConversationActuallyDeletesIt() {
//        conversation.sendMessage(MESSAGE_TEXT)
//        conversation.deleteConversation()
//        // We add a timeout to the future to avoid this test running infinitely
//        val future = db
//            .getObject(CONVERSATION_ID, Message::class.java)
//            .orTimeout(30, TimeUnit.SECONDS)
//
//        // Checking that the get object failed and that the failure message contained the key we
//        // were trying to access
//        //val exception = assertFailsWith<CompletionException> { future.join() }
//        //assertThat(exception.message, containsString(CONVERSATION_ID))
//        assertTrue(future.isCompletedExceptionally)
//    }
//}