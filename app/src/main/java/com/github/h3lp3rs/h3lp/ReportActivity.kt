package com.github.h3lp3rs.h3lp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.dataclasses.Report
import com.github.h3lp3rs.h3lp.forum.EXTRA_FORUM_CATEGORY
import com.github.h3lp3rs.h3lp.forum.ForumCategory
import com.github.h3lp3rs.h3lp.forum.ForumPostsActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_new_post.*
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : AppCompatActivity() {

    companion object{
        const val bug = "bug"
        const val suggestion = "suggestion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val bundle = intent.extras!!
        val category = bundle.getString(EXTRA_REPORT_CATEGORY)
        reportCategoryDropdown.setText(category)

        createReportCategoriesDownMenu()

    }

    /**
     * Creates the dropDown containing the report categories
     */
    private fun createReportCategoriesDownMenu() {
        val adapter = ArrayAdapter(
            this, R.layout.dropdown_menu_popup, listOf(bug, suggestion)
        )

        val editTextFilledExposedDropdown =
            findViewById<AutoCompleteTextView>(R.id.reportCategoryDropdown)

        editTextFilledExposedDropdown.setAdapter(adapter)
    }

    /**
     * Sends the report to the database
     * @param view Current view
     */
    fun sendReport(view : View) {
        val category = reportCategoryDropdown.text.toString()
        val content = reportEditTxt.text.toString()
        val report = Report(category,content)
        SignInActivity.userUid?.let { Databases.databaseOf(Databases.REPORTS).setObject(it, Report::class.java, report) }
        // Clears the text field when the user hits send
        reportEditTxt.text?.clear()
    }

}