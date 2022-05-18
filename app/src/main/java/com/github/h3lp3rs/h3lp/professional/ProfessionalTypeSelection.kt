package com.github.h3lp3rs.h3lp.professional

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.dataclasses.MedicalType
import com.github.h3lp3rs.h3lp.forum.ForumCategory
import com.github.h3lp3rs.h3lp.forum.ForumCategory.*
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.storage.Storages.FORUM_THEMES_NOTIFICATIONS

class ProfessionalTypeSelection : AppCompatActivity() {

    private lateinit var storage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professional_type_selection)
        storage = storageOf(FORUM_THEMES_NOTIFICATIONS)
        loadData()
    }

    /**
     * Save data when the user leaves the activity
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        super.onStop()
        saveData()
    }

    /**
     * Function for the back button to go back to ProMainActivity
     */
    fun backHome(view: View) {
        val intent = Intent(this, ProMainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Show a dialogue with explication on what is the form for
     */
    fun helpDialogue(view: View) {
        AlertDialog.Builder(this).setTitle(getString(R.string.forum_themes))
            .setMessage(getString(R.string.help_my_forum_theme)).show()
    }

    /**
     * Load forum themes data
     */
    private fun loadData() {
        val theme = storage.getObjectOrDefault(
            getString(R.string.forum_theme_key),
            MedicalType::class.java, null
        ) ?: return

        checkCheckBox(theme.hasCategory(CARDIOLOGY), R.id.cardioTxt)
        checkCheckBox(theme.hasCategory(GENERAL), R.id.generalSwitch)
        checkCheckBox(theme.hasCategory(GYNECOLOGY), R.id.gynecologySwitch)
        checkCheckBox(theme.hasCategory(NEUROLOGY), R.id.neurologySwitch)
        checkCheckBox(theme.hasCategory(PEDIATRY), R.id.pediatrySwitch)
        checkCheckBox(theme.hasCategory(TRAUMATOLOGY), R.id.traumaTxt)
    }

    /**
     * Toggle a given switch
     * @param toggle the boolean to toggle the switch
     * @param id the id of the switch to toggle
     */
    private fun checkCheckBox(toggle: Boolean, id: Int) {
        findViewById<CheckBox>(id).isChecked = toggle
    }

    /**
     * Save theme data
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveData() {
        val categoriesList = emptyList<ForumCategory?>() +
                getCategoriesFromSwitch(R.id.generalSwitch, GENERAL) +
                getCategoriesFromSwitch(R.id.cardioTxt, CARDIOLOGY) +
                getCategoriesFromSwitch(R.id.traumaTxt, TRAUMATOLOGY) +
                getCategoriesFromSwitch(R.id.pediatrySwitch, PEDIATRY) +
                getCategoriesFromSwitch(R.id.neurologySwitch, NEUROLOGY) +
                getCategoriesFromSwitch(R.id.gynecologySwitch, GYNECOLOGY)
        val theme = MedicalType(categoriesList.filterNotNull())

        storage.setObject(getString(R.string.forum_theme_key), MedicalType::class.java, theme)
        storage.push()
    }

    /**
     * Returns the given category if it is checked, null otherwise
     */
    private fun getCategoriesFromSwitch(id: Int, currentCategory: ForumCategory): ForumCategory? {
        return if (findViewById<CheckBox>(id).isChecked) currentCategory else null
    }
}