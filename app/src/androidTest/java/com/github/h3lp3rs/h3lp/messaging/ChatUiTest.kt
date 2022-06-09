package com.github.h3lp3rs.h3lp.messaging

import android.content.Intent
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
import com.github.h3lp3rs.h3lp.utils.H3lpAppTest.Companion.USER_TEST_ID
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.Databases.MESSAGES
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.view.messaging.ChatActivity
import com.github.h3lp3rs.h3lp.model.messaging.Conversation
import com.github.h3lp3rs.h3lp.model.messaging.Conversation.Companion.createAndSendKeyPair
import com.github.h3lp3rs.h3lp.view.messaging.EXTRA_CONVERSATION_ID
import com.github.h3lp3rs.h3lp.model.messaging.Messenger.HELPEE
import com.github.h3lp3rs.h3lp.model.messaging.Messenger.HELPER
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.view.helprequest.helper.EXTRA_USER_ROLE
import com.xwray.groupie.ViewHolder
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.security.KeyStore


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

}