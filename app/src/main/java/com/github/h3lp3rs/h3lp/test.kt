package com.github.h3lp3rs.h3lp

import FireForum.Companion.UNIQUE_POST_ID
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.forum.ForumCategory
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.forumOf
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData

class test : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)
        val db = databaseOf(Databases.FORUM)
        val forum = forumOf(ForumCategory.CARDIOLOGY)
        var postKey = ""
//        forum.newPost("not a doctor", "how heart?")
//            .handle { postMessage, error ->
//                Log.i("MSG", "HMMMMMMMM are you running?")
//                if (error != null) {
//                    Log.i("MSG", error.message!!)
//                }
//                if (error == null) {
//                    postKey = postMessage.post.key
//                    postMessage.reply("a doctor", "heart gud")
//                }
//            }
//        Log.i("MSG", "Thread finished sleeping")
//        forum.getPost(listOf("41")).handle { it, error ->
//            Log.i("MSG", "replying")
//            if (error != null) {
//                Log.i("MSG", error.message!!)
//            } else {
//                Log.i("MSG", it.replies[0].content)
////        db.addToObjectsListConcurrently("CARDIOLOGY/24", String::class.java, "TEST")
//                Log.i("MSG", "hmmm pls print")
//            }
//        }
//        db.getObject("CARDIOLOGY/41", ForumPostData::class.java).handle { it, error ->
//            Log.i("MSG", "replying")
//            if (error != null) {
//                Log.i("MSG", error.message!!)
//            } else {
//                Log.i("MSG", it.toString())
//            }
//        }

        forum.root().getAll().handle { list, error ->
            Log.i("MSG", "getAll")
            if (error == null) {
                for ((category, posts) in list) {
                    for (post_ in posts) {
                        Log.i("MSG", category)
                        Log.i("MSG", "POST : " + post_.replies[0].content)
                    }
                }
            }
        }
    }
}