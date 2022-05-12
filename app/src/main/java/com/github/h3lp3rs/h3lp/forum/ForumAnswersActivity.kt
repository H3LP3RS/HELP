package com.github.h3lp3rs.h3lp.forum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.ForumPostsActivity.Companion.selectedPost
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.getName
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_forum_answers.*
import kotlinx.android.synthetic.main.answer_forum_row.view.*

/**
 * Activity where the user sees all answers of the selected post and enters an answer
 */
class ForumAnswersActivity : AppCompatActivity() {
    private val adapter = GroupAdapter<ViewHolder>()
    private lateinit var category : String
    private lateinit var forum : Forum

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_answers)

        recycler_view_forum_answers.adapter = adapter
        category = selectedPost.post.category.name

        forum = ForumCategory.categoriesMap[category]?.let { ForumCategory.forumOf(it) }!!

        add_answer_button.setOnClickListener {
            val answer = text_view_enter_answer.text.toString()
            // Currently uses the firebase uid as the post's author, could ba changed later
            getName()!!.let { id -> selectedPost.reply(id, answer) }
            // Clear the text field
            text_view_enter_answer.text.clear()
        }

        listenForAnswers()
    }

    /**
     * Displays all the answers of the selected post
     */
    private fun listenForAnswers() {

        fun onAnswerAdded(data : ForumPostData) {
            Answer(data.content, data.author).let { adapter.add(it) }
            recycler_view_forum_answers.smoothScrollToPosition(adapter.itemCount - 1)
        }

        selectedPost.listen { onAnswerAdded(it) }

    }
}

/**
 * Class representing the layout of a forum answer
 */
private class Answer(
    private val answer : String,
    private val author : String,
) : Item<ViewHolder>() {
    /**
     * Class representing the layout of a forum answer
     */

    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.answer_post.text = answer
        viewHolder.itemView.answer_author.text = author
    }

    override fun getLayout() : Int {
        return R.layout.answer_forum_row
    }

}