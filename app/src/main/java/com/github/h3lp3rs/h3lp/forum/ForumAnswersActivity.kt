package com.github.h3lp3rs.h3lp.forum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_forum_answers.*
import kotlinx.android.synthetic.main.answer_forum_row.view.*

class ForumAnswersActivity: AppCompatActivity()  {
    private val adapter = GroupAdapter<ViewHolder>()
    private lateinit var questionId : String
    private lateinit var category : String
    private lateinit var forum : Forum


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_answers)
        recycler_view_forum_answers.adapter = adapter

        val bundle = intent.extras!!
        questionId = bundle.getString(EXTRA_QUESTION_ID) ?: questionId
        category = bundle.getString(EXTRA_FORUM_CATEGORY) ?: category

        forum = ForumCategory.categoriesMap[category]?.let { ForumCategory.forumOf(it) }!!

        add_answer_button.setOnClickListener{
            val answer = text_view_enter_answer.text.toString()
            forum.getPost(questionId).thenAccept { q -> q.reply("7amid 2",answer)}
            // Clears the text field when the user hits send
            text_view_enter_answer.text.clear()
        }
        listenForAnswers()

    }

    private fun listenForAnswers() {
        fun onAnswerAdded(data: ForumPostData){
            Answer(data.content, data.key, category).let { adapter.add(it) }
            recycler_view_forum_answers.smoothScrollToPosition(adapter.itemCount - 1)
        }
        /*
        forum.listenToAll { data ->
            run { onAnswerAdded(data) }
        }
         */
        forum.getPost(questionId).thenAccept{ q -> q.listen { onAnswerAdded(q.post) }}
    }
}

private class Answer(private val answer : String, private val answerId : String, private val questionId : String) :
    Item<ViewHolder>() {

    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.answer_post.text = answer
    }

    override fun getLayout() : Int {
        return R.layout.answer_forum_row
    }

    fun getID() : String {
        return answerId
    }

    fun getQuestionID() : String {
        return questionId
    }

}