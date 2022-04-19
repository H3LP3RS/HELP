package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.content.Intent
import android.app.Instrumentation.*
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.h3lp3rs.h3lp.messaging.*
import com.xwray.groupie.ViewHolder
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
private const val CONVERSATION_ID = "testing_id"
private const val SENT_MESSAGE = "Testing Chat UI"
private const val RECEIVED_MESSAGE = "Tests succeeded!"

class ChatUiTest {
    private val currentMessenger = Messenger.HELPEE
    private val toMessenger = Messenger.HELPER

    private lateinit var conversationFrom: Conversation
    private lateinit var conversationTo: Conversation

    @get:Rule
    val testRule = ActivityScenarioRule(ChatActivity::class.java)

    @Before
    fun setup() {
        init()
        val intent = Intent()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)
        conversationFrom = Conversation(CONVERSATION_ID, currentMessenger)
        conversationTo = Conversation(CONVERSATION_ID,toMessenger)
    }

    @After
    fun release() {
        conversationFrom.deleteConversation()
        Intents.release()
    }

    @Test
    fun sendMessageDisplaysTheCorrectMessage() {
        onView(withId(R.id.text_view_enter_message)).perform(click()).perform(typeText(SENT_MESSAGE))
        onView(withId(R.id.button_send_message)).perform(click()).perform(ViewActions.closeSoftKeyboard())
        onView(withId(R.id.recycler_view_chat))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(0))
            .check(matches(hasDescendant(withText(SENT_MESSAGE))))
    }
    @Test
    fun receiveMessageDisplaysTheCorrectMessage() {
        conversationTo.sendMessage(RECEIVED_MESSAGE)
        onView(withId(R.id.recycler_view_chat))
            .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(0))
            .check(matches(hasDescendant(withText(RECEIVED_MESSAGE))))
    }
    /*
    doesn't work :/
    private fun getIntent(): Intent {
        val intent = Intent(getApplicationContext(), ChatActivity::class.java)
        intent.putExtra(EXTRA_USER_ROLE, Messenger.HELPER)
        intent.putExtra(EXTRA_CONVERSATION_ID, "ChatUiTest")
        return intent
    }
     */
}