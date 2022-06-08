package com.github.h3lp3rs.h3lp.view.forum

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.EXTRA_REPORT_CATEGORY
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.Companion.forumOf
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.getName
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.globalContext
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_new_post.*
import kotlinx.android.synthetic.main.activity_report.*

/**
 * Activity where a user sends a Posts in the forum
 */
class NewPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        val bundle = intent.extras!!
        val category = bundle.getString(EXTRA_FORUM_CATEGORY)
        newPostCategoryDropdown.setText(category)
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
        val category = newPostCategoryDropdown.text.toString()
        val textViewAnswerQuestion = findViewById<TextInputEditText>(R.id.newPostTitleEditTxt)
        val question = textViewAnswerQuestion.text.toString()
        val forum = ForumCategory.categoriesMap[category]?.let { cachedForumOf(it) }!!
        // Add post to the database
        val post = getName()?.let { forum.newPost(it, question, true) }
        // Enable notifications on replies to this post if user has activated it
        if (switch_enable_notifications.isChecked) {
            post?.thenAccept {
                it.sendIntentNotificationOnNewReplies(
                    globalContext, ForumPostsActivity::class.java
                )
            }
        }
        // Clears the text field when the user hits send
        textViewAnswerQuestion.text?.clear()
    }
}
