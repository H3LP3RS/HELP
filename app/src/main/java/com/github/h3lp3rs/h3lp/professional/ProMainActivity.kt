package com.github.h3lp3rs.h3lp.professional

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.ForumAnswersActivity
import com.github.h3lp3rs.h3lp.forum.ForumCategoriesActivity
import com.github.h3lp3rs.h3lp.forum.ForumPostsActivity
import com.github.h3lp3rs.h3lp.forum.NewPostActivity

/**
 * Main activity of the professional portal
 */
class ProMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professional_main)
    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName : Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    fun goToProProfileActivity(view : View){
        goToActivity(ProProfileActivity::class.java)
    }

    fun goToForumTheme(view: View) {
        goToActivity(ProfessionalTypeSelection::class.java)
    }

    fun goToForumCategories(view: View) {
        goToActivity(ForumCategoriesActivity::class.java)
    }

}