package com.github.h3lp3rs.h3lp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.MESSAGES
import com.github.h3lp3rs.h3lp.messaging.Message
import com.github.h3lp3rs.h3lp.messaging.Messenger

class test : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)
        val db = databaseOf(MESSAGES)
        db.getObjectsList("542", Message::class.java).thenApply {
            Log.i("MSG", it.toString())
        }
        val s = db.addToObjectsListConcurrently("542", Message::class.java, Message(Messenger.HELPEE, "TESTING"))
        Log.i("MSG", "hmmm pls print")
        Log.i("MSG", s!!)
    }
}