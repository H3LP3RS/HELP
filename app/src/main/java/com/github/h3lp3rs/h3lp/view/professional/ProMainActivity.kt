package com.github.h3lp3rs.h3lp.view.professional

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.view.forum.ForumCategoriesActivity

/**
 * Main activity of the professional portal
 */
class ProMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professional_main)
    }

    /** Starts the activity by sending intent */
    private fun goToActivity(ActivityName: Class<*>?) {
        val intent = Intent(this, ActivityName)
        startActivity(intent)
    }

    fun goToProProfileActivity(view: View) {
        goToActivity(ProProfileActivity::class.java)
    }

    fun goToForumTheme(view: View) {
        goToActivity(ProfessionalTypeSelection::class.java)
    }

    fun goToForumCategories(view: View) {
        goToActivity(ForumCategoriesActivity::class.java)
    }

    fun goToBasicPortal(view: View) {
        goToActivity(MainPageActivity::class.java)
    }

}