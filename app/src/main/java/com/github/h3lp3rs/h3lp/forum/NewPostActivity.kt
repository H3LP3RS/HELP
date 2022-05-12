package com.github.h3lp3rs.h3lp.forum

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.google.android.material.textfield.TextInputEditText

/**
 * Activity where a user sends a Posts in the forum
 */
class NewPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        createDropDownMenu()
    }

    /**
     * Creates the dropDown containing the forum categories
     */
    private fun createDropDownMenu() {
        val adapter = ArrayAdapter(
            this, R.layout.dropdown_menu_popup, ForumCategory.values()
        )

        val editTextFilledExposedDropdown =
            findViewById<AutoCompleteTextView>(R.id.newPostCategoryDropdown)

        editTextFilledExposedDropdown.setAdapter(adapter)
    }

    /**
     * Sends the post to the forum
     * @param view Current view
     */
    fun sendPost(view: View) {
        val category =
            findViewById<AutoCompleteTextView>(R.id.newPostCategoryDropdown).text.toString()
        val textViewAnswerQuestion = findViewById<TextInputEditText>(R.id.newPostTitleEditTxt)
        val question = textViewAnswerQuestion.text.toString()
        val forum = ForumCategory.categoriesMap[category]?.let { ForumCategory.forumOf(it) }!!
        // Add post to the database
        SignInActivity.userUid?.let { forum.newPost(it, question) }

        // Clears the text field when the user hits send
        textViewAnswerQuestion.text?.clear()
    }
}
