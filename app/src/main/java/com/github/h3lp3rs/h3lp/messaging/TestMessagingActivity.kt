package com.github.h3lp3rs.h3lp.messaging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.messaging.Messenger.HELPER

class TestMessagingActivity : AppCompatActivity() {
    private val db: Database = databaseOf(Databases.MESSAGES)
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_messaging)
        val button = findViewById<Button>(R.id.testButton)
        val conversation = Conversation("uniqueId", HELPER, db)
        button.setOnClickListener {
            conversation.sendMessage(counter.toString())
            ++counter
        }
        conversation.sendMessage("Hello there")
    }
}