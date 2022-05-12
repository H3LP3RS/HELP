package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.dataclasses.MedicalType
import com.github.h3lp3rs.h3lp.forum.ForumCategory.*
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.forumOf
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.getName
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
    private val enabledCategoriesNotifications = storageOf(Storages.FORUM_THEMES_NOTIFICATIONS)

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_posts)

        recycler_view_forum_posts.adapter = adapter

        val bundle = intent.extras!!
        category = bundle.getString(EXTRA_FORUM_CATEGORY) ?: category

        forum = ForumCategory.categoriesMap[category]?.let { forumOf(it) }!!

        adapter.setOnItemClickListener { item, view ->
            selectedPost = item as ForumPost
            val intent = Intent(view.context, ForumAnswersActivity::class.java)
            intent.putExtra(EXTRA_FORUM_CATEGORY, category)
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
            // Send a notification only if the user is not the author of the post
            if (data.author != getName()) {
                val medicalType = enabledCategoriesNotifications.getObjectOrDefault(
                    getString(R.string.forum_theme_key), MedicalType::class.java, null
                )
                medicalType?.let {
                    fun checkIfEnabled(medicalType : String) : Boolean {
                        val enabled = when (medicalType) {
                            GENERAL.name -> it.generalist
                            PEDIATRY.name -> it.pediatry
                            CARDIOLOGY.name -> it.cardiology
                            TRAUMATOLOGY.name -> it.traumatology
                            GYNECOLOGY.name -> it.gynecology
                            NEUROLOGY.name -> it.neurology
                            else -> false
                        }
                        return enabled
                    }
                    if (checkIfEnabled(category!!)){
                        // TODO
                    }

                }
            }
        }

        forum.listenToAll { data ->
            run { onPostAdded(data) }
        }
    }

}
