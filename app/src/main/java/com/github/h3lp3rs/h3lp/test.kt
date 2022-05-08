package com.github.h3lp3rs.h3lp

import FireForum.Companion.UNIQUE_POST_ID
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.forum.ForumCategory
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.forumOf

class test : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)
        val db = databaseOf(Databases.FORUM)
        db.incrementAndGet(UNIQUE_POST_ID, 1)
        val forum = forumOf(ForumCategory.CARDIOLOGY)
        var postKey = ""
        forum.newPost("TEST", "AAAAAAAAAAAAAa")
            .handle { postMessage, error ->
                Log.i("MSG", "HMMMMMMMM are you running?")
                if (error != null) {
                    Log.i("MSG", error.message!!)
                }
                if (error == null) {
                    postKey = postMessage.post.key
                    postMessage.reply("Not me", "Indeed indeed")
                }
            }
        Log.i("MSG", "Thread finished sleeping")
        forum.getPost(listOf(postKey)).handle { it, error ->
            Log.i("MSG", "replying")
            if (error != null) {
                Log.i("MSG", error.message!!)
            } else {
                Log.i("MSG", it.replies.toString())
//        db.addToObjectsListConcurrently("CARDIOLOGY/24", String::class.java, "TEST")
                Log.i("MSG", "hmmm pls print")
            }
        }
    }
}