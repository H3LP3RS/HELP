package com.github.h3lp3rs.h3lp.forum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.h3lp3rs.h3lp.*

const val EXTRA_FORUM_CATEGORY = "forum_category"

class ForumCategoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_categories)

    }

    fun goToForum(view: View) {
        val category = when (view.id) {
            R.id.generalist_expand_button -> MedicalCategory.GENERALIST
            R.id.cardio_expand_button -> MedicalCategory.CARDIOLOGY
            R.id.traum_expand_button -> MedicalCategory.TRAUMATOLOGY
            R.id.pedia_expand_button -> MedicalCategory.PEDIATRIC
            R.id.neuro_expand_button -> MedicalCategory.NEUROLOGY
            R.id.gyne_expand_button -> MedicalCategory.GYNECOLOGY
            else -> MedicalCategory.GENERALIST
        }
        val bundle = Bundle()
        bundle.putString(EXTRA_FORUM_CATEGORY, category.title)
        val intent = Intent(this, ForumPostsActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    enum class MedicalCategory(val title: String) {
        GENERALIST("generalist"), CARDIOLOGY("cardiology"), TRAUMATOLOGY("traumatology"),
        PEDIATRIC("pediatric"), NEUROLOGY("neurology"), GYNECOLOGY("gynecology")
    }
}