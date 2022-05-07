package com.github.h3lp3rs.h3lp.forum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.h3lp3rs.h3lp.R

class ForumCategoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum_categories)
    }

    enum class MedicalCategory (val title: String){
        GENERALIST("generalist"), CARDIOLOGY("cardiology"), TRAUMATOLOGY("traumatology"),
        PEDIATRY("pediatry"), NEUROLOGY("neurology"), GYNECOLOGY("gynecology")

    }
}