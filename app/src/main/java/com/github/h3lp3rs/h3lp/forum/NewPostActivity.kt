package com.github.h3lp3rs.h3lp.forum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.github.h3lp3rs.h3lp.R

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        createDropDown()
    }

    private fun createDropDown(){
        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup,
            // TODO replace by list of categories
            listOf(ForumCategoriesActivity.MedicalCategory.values())
        )

        val editTextFilledExposedDropdown =
            findViewById<AutoCompleteTextView>(R.id.newPostCategoryDropdown)

        editTextFilledExposedDropdown.setAdapter(adapter)
    }

    fun savePost(view : View){
        val category = findViewById<AutoCompleteTextView>(R.id.newPostCategoryDropdown).text.toString()
        val question = findViewById<AutoCompleteTextView>(R.id.newPostTitleEditTxt).text.toString()
        // TODO save in DB
    }
}