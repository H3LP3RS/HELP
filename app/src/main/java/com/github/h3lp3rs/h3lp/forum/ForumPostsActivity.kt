package com.github.h3lp3rs.h3lp.forum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.EXTRA_HELPEE_ID
import com.github.h3lp3rs.h3lp.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_forum_posts.*
import kotlinx.android.synthetic.main.chat_receiver.view.*
import kotlinx.android.synthetic.main.post_forum_row.view.*

const val EXTRA_QUESTION_ID = "forum_question_id"
// TODO put it in activity of categories once implemented
const val EXTRA_FORUM_CATEGORY = "forum_category"


class ForumPostsActivity : AppCompatActivity() {
    // TODO extra category
    private val adapter = GroupAdapter<ViewHolder>()
    private var category : String? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_posts)
        recycler_view_forum.adapter = adapter

        val bundle = this.intent.extras
        category = bundle?.getString(EXTRA_FORUM_CATEGORY) ?: category

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as Post
            intent.putExtra(EXTRA_QUESTION_ID, userItem.getID())
            //val intent = Intent(view.context, ForumActivity::class.java)
            //startActivity(intent)
        }
    }


}

private class Post(private val question : String, private val questionId : String, private val category : String) :
    Item<ViewHolder>() {

    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.text_view_receiver.text = question
        viewHolder.itemView.image_post.setImageResource(getImage())
    }

    override fun getLayout() : Int {
        return R.layout.post_forum_row
    }

    private fun getImage() : Int {
        TODO("Image per cat")
    }

    fun getID() : String {
        return questionId
    }

}