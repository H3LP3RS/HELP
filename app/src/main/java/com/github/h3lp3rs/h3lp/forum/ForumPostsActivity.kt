package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_forum_posts.*
import kotlinx.android.synthetic.main.post_forum_row.view.*

const val EXTRA_QUESTION_ID = "forum_question_id"

class ForumPostsActivity : AppCompatActivity() {
    // TODO extra category
    private val adapter = GroupAdapter<ViewHolder>()
    private var category : String? = null
    private lateinit var forum : Forum

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_posts)
        recycler_view_forum_posts.adapter = adapter

        val bundle = this.intent.extras
        category = bundle?.getString(EXTRA_FORUM_CATEGORY) ?: category

        forum = ForumCategory.categoriesMap[category]?.let { ForumCategory.forumOf(it) }!!

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as Post
            intent.putExtra(EXTRA_QUESTION_ID, userItem.getID())
            val intent = Intent(view.context, ForumAnswersActivity::class.java)
            startActivity(intent)
        }

        add_post_button.setOnClickListener{
            val qst = text_view_enter_post.text.toString()
            // When the user clicks on add, the message is sent to the database
            // forum.addAnswer(answer)
            category?.let { it1 -> Post(qst,"", it1) }?.let { it2 -> adapter.add(it2) }
            // Clears the text field when the user hits send
            text_view_enter_post.text.clear()
        }

        listenForPosts()
    }

    private fun listenForPosts(){
        fun onPostAdded(data: ForumPostData){
            category?.let { Post(data.content, data.key, it) }?.let { adapter.add(it) }
        }

        forum.listenToAll { data ->
            run { onPostAdded(data) }
        }
    }


}

private class Post(private val question : String, private val questionId : String, private val category : String) :
    Item<ViewHolder>() {

    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.question_post.text = question
        viewHolder.itemView.image_post.setImageResource(getImage())
    }

    override fun getLayout() : Int {
        return R.layout.post_forum_row
    }

    private fun getImage() : Int {
        return when (category) {
            ForumCategory.GENERAL.toString() -> R.drawable.ic_generalist
            ForumCategory.CARDIOLOGY.toString() -> R.drawable.ic_cardiology
            ForumCategory.TRAUMATOLOGY.toString() -> R.drawable.ic_traumatology
            ForumCategory.PEDIATRY.toString() -> R.drawable.ic_pediatric
            ForumCategory.NEUROLOGY.toString() -> R.drawable.ic_neurology
            ForumCategory.GYNECOLOGY.toString() -> R.drawable.ic_gynecology
            else -> {
                R.drawable.ic_generalist
            }
        }
    }

    fun getID() : String {
        return questionId
    }

}