package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.app.Instrumentation.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.h3lp3rs.h3lp.messaging.ChatActivity
import com.github.h3lp3rs.h3lp.messaging.EXTRA_CONVERSATION_ID
import com.github.h3lp3rs.h3lp.messaging.Messenger
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChatUiTest {

    @get:Rule
    val testRule = ActivityScenarioRule(ChatActivity::class.java)

    @Before
    fun setup() {
        init()
        val intent = Intent()
        val intentResult = ActivityResult(Activity.RESULT_OK, intent)
        intending(anyIntent()).respondWith(intentResult)
    }

    @After
    fun release() {
        Intents.release()
    }

    @Test
    fun sendMessageShowsMessageSent() {

    }
    /*
    private fun getIntent(): Intent {
        val intent = Intent(getApplicationContext(), ChatActivity::class.java)
        intent.putExtra(EXTRA_USER_ROLE, Messenger.HELPER)
        intent.putExtra(EXTRA_CONVERSATION_ID, "ChatUiTest")
        return intent
    }

     */
}