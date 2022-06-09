package com.github.h3lp3rs.h3lp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.dataclasses.Report
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import kotlinx.android.synthetic.main.activity_report.*

/**
 * Activity where a user can report a bug or a suggestion
 */
class ReportActivity : AppCompatActivity() {

    companion object {
        const val bug = R.string.bug
        const val suggestion = R.string.suggestion
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

        reportCategoryDropdown.setAdapter(adapter)
    }

    /**
     * Sends the report to the database
     * @param view Current view
     */
    fun sendReport(view: View) {
        val category = reportCategoryDropdown.text.toString()
        val content = reportEditTxt.text.toString()
        val report = Report(category, content)
        // The UID might not be the best solution since a user can't give multiple reports. It's because we need
        // to have a link to the user that has a bug and also to keep consistency with other
        // implementations and keep the testing part simple since it's not the most important feature of the app.
        SignInActivity.userUid?.let {
            Databases.databaseOf(Databases.REPORTS).setObject(it, Report::class.java, report)
        }
        // Clears the text field when the user hits send
        reportEditTxt.text?.clear()
    }

}