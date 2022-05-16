package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_forum_answers.*
import kotlinx.android.synthetic.main.activity_forum_posts.*

/**
 * Activity containing the forum posts of a given a category
 */
class ForumPostsActivity : AppCompatActivity() {
    companion object {
        // The post of which the user wants to see the answers
        lateinit var selectedPost : ForumPost
    }
    private val adapter = GroupAdapter<ViewHolder>()
    private var category : String? = null
    private lateinit var forum : Forum

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_posts)

        recycler_view_forum_posts.adapter = adapter

        val bundle = intent.extras!!
        category = bundle.getString(EXTRA_FORUM_CATEGORY) ?: category
        forum = ForumCategory.categoriesMap[category]?.let { ForumCategory.forumOf(it) }!!

        adapter.setOnItemClickListener { item, view ->
            selectedPost = item as ForumPost
            val intent = Intent(view.context, ForumAnswersActivity::class.java)
            startActivity(intent)
        }

        add_post_button.setOnClickListener { view ->
            // When the user clicks on add, he is redirected to the new post activity to be able to
            // add a post
            val intent = Intent(view.context, NewPostActivity::class.java)
            startActivity(intent)
        }

        listenToNewPosts()
    }

    /**
     * Displays all the posts in the forum database
     */
    private fun listenToNewPosts() {
        fun onPostAdded(data : ForumPostData) {
            category?.let { ForumPost(forum, data, emptyList()) }?.let { adapter.add(it) }
            recycler_view_forum_answers.smoothScrollToPosition(adapter.itemCount - 1)
        }

        forum.listenToAll { data ->
            run { onPostAdded(data) }
        }
    }

}
