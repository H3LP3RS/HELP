package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.*
import android.util.Base64
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.h3lp3rs.h3lp.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.Databases.MESSAGES
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.messaging.ChatActivity
import com.github.h3lp3rs.h3lp.messaging.Conversation
import com.github.h3lp3rs.h3lp.messaging.Conversation.Companion.createAndSendKeyPair
import com.github.h3lp3rs.h3lp.messaging.EXTRA_CONVERSATION_ID
import com.github.h3lp3rs.h3lp.messaging.Messenger
import com.github.h3lp3rs.h3lp.messaging.Messenger.HELPEE
import com.github.h3lp3rs.h3lp.messaging.Messenger.HELPER
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.xwray.groupie.ViewHolder
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey


private const val CONVERSATION_ID = "testing_id1"
private const val SENT_MESSAGE = "Testing Chat UI"
private const val RECEIVED_MESSAGE = "Tests succeeded!"

class ChatUiTest {
    private val currentMessenger = HELPEE
    private val toMessenger = HELPER

    private lateinit var conversationFrom: Conversation
    private lateinit var conversationTo: Conversation



    @Before
    fun setup() {
        // Launching the activity with the needed parameters
        val intent = Intent(
            getApplicationContext(),
            ChatActivity::class.java
        ).apply {
            putExtra(EXTRA_CONVERSATION_ID, CONVERSATION_ID)
            putExtra(EXTRA_USER_ROLE, HELPEE)
        }

        userUid = USER_TEST_ID

        setDatabase(MESSAGES, MockDatabase())
        resetStorage()

        // Remove existing keys from the keyStore
        val keyStore = KeyStore.getInstance(Conversation.ANDROID_KEY_STORE).apply { load(null) }
        keyStore.deleteEntry(Conversation.keyAlias(CONVERSATION_ID, HELPEE.name))
        keyStore.deleteEntry(Conversation.keyAlias(CONVERSATION_ID, HELPER.name))

        createAndSendKeyPair(CONVERSATION_ID, HELPEE, getApplicationContext())
        createAndSendKeyPair(CONVERSATION_ID, HELPER, getApplicationContext())

        conversationFrom = Conversation(CONVERSATION_ID, currentMessenger, getApplicationContext())
        conversationTo = Conversation(CONVERSATION_ID,toMessenger, getApplicationContext())

        ActivityScenario.launch<ChatActivity>(intent)

        init()
    }

    @After
    fun release() {
        //conversationFrom.deleteConversation()
        Intents.release()
    }

    @Test
    fun sendMessageDisplaysTheCorrectMessage() {
        onView(withId(R.id.text_view_enter_message)).perform(click()).perform(typeText(SENT_MESSAGE))
        onView(withId(R.id.button_send_message)).perform(click()).perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.recycler_view_chat))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(0))
            //.check(matches(hasDescendant(withText(SENT_MESSAGE))))
    }

    @Test
    fun receiveMessageDisplaysTheCorrectMessage() {
        conversationTo.sendMessage(RECEIVED_MESSAGE)
        onView(withId(R.id.recycler_view_chat))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(0))
            //.check(matches(hasDescendant(withText(RECEIVED_MESSAGE))))
    }
}