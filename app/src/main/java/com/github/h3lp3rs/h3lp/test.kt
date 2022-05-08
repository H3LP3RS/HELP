package com.github.h3lp3rs.h3lp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.MESSAGES
import com.github.h3lp3rs.h3lp.forum.ForumCategory
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.forumOf

class test : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)
        val db = databaseOf(MESSAGES)
        val forum = forumOf(ForumCategory.CARDIOLOGY)
        forum.newPost("Me", "Hi cool forum you got there")
            .
            .thenApply {
                Log.i("MSG", "HMMMMMMMM are you running?")
                it.reply("Not me", "Indeed indeed")
            }

        Log.i("MSG", "hmmm pls print")
    }
}