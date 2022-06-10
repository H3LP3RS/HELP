package com.github.h3lp3rs.h3lp.view.professional

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.view.forum.ForumCategoriesActivity
import com.github.h3lp3rs.h3lp.view.utils.ActivityUtils.goToActivity
import com.github.h3lp3rs.h3lp.view.utils.ActivityUtils.goToMainPage

/**
 * Main activity of the professional portal
 */
class ProMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professional_main)
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
        goToMainPage()
    }

}