package com.github.h3lp3rs.h3lp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.h3lp3rs.h3lp.messaging.ChatActivity
import org.junit.Rule
import org.junit.Test

class ChatUiTest {
    private val targetContext: Context = ApplicationProvider.getApplicationContext()

    @get:Rule
    val testRule = ActivityScenarioRule(ChatActivity::class.java)


    @Test
    fun sendMessageShowsMessageSent() {

    }
}