package com.github.h3lp3rs.h3lp.view.forum

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.DELAY
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.*

const val EXTRA_FORUM_CATEGORY = "forum_category"

/**
 * Activity where the user selects the forum category he/she wants to go to
 */
class ForumCategoriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_categories)

    }

    /**
     * Goes to the activity containing forum posts of the selected category
     */
    fun goToForum(view: View) {
        val category = when (view.id) {
            R.id.generalist_expand_button -> GENERAL
            R.id.cardio_expand_button -> CARDIOLOGY
            R.id.traum_expand_button -> TRAUMATOLOGY
            R.id.pedia_expand_button -> PEDIATRY
            R.id.neuro_expand_button -> NEUROLOGY
            R.id.gyne_expand_button -> GYNECOLOGY
            else -> GENERAL
        }

        val intent = Intent(this, ForumPostsActivity::class.java)
        intent.putExtra(EXTRA_FORUM_CATEGORY, category.name)
        startActivity(intent)
    }

    /** Called when the user taps the back button */
    fun goBack(view: View) {
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, DELAY)
    }

}