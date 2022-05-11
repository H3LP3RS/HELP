package com.github.h3lp3rs.h3lp.forum

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.google.android.material.textfield.TextInputEditText

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        createDropDownMenu()
    }

    private fun createDropDownMenu() {
        val adapter = ArrayAdapter(
            this, R.layout.dropdown_menu_popup, ForumCategory.values()
        )

        val editTextFilledExposedDropdown =
            findViewById<AutoCompleteTextView>(R.id.newPostCategoryDropdown)

        editTextFilledExposedDropdown.setAdapter(adapter)
    }

    fun sendPost(view : View) {
        val category =
            findViewById<AutoCompleteTextView>(R.id.newPostCategoryDropdown).text.toString()
        val textViewAnswerQuestion = findViewById<TextInputEditText>(R.id.newPostTitleEditTxt)
        val question = textViewAnswerQuestion.text.toString()
        val forum = ForumCategory.categoriesMap[category]?.let { ForumCategory.forumOf(it) }!!
        // Add post to the database
        forum.newPost("7amid", question)

        // Clears the text field when the user hits send
        textViewAnswerQuestion.text?.clear()
    }
}
// category?.let { it1 -> Post(qst,"", it1) }?.let { it2 -> adapter.add(it2) }