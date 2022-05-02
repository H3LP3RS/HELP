package com.github.h3lp3rs.h3lp.professional

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases.Companion.activateHelpListeners
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.dataclasses.MedicalType
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.google.android.material.switchmaterial.SwitchMaterial

class ProfessionalTypeSelection : AppCompatActivity() {

    private lateinit var storage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_professional_type_selection)
        storage = storageOf(FORUM_THEMES)
        loadData()
    }

    /**
     * When the user leaves the activity
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        super.onStop()
        saveData()
    }

    /**
     * Function for the back button to go back to ProMainActivity
     */
    fun backHome(view: View){
        val intent = Intent(this, ProMainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Show a dialogue with explication on what is the form for
     */
    fun helpDialogue(view: View){
        AlertDialog.Builder(this).setTitle(getString(R.string.forum_themes))
            .setMessage(getString(R.string.help_my_forum_theme)).show()
    }

    /**
     * Load forum themes data
     */
    private fun loadData() {
        val theme = storage.getObjectOrDefault(getString(R.string.forum_theme_key),
            MedicalType::class.java, null) ?: return

        checkCheckBox(theme.cardiology, R.id.cardioTxt)
        checkCheckBox(theme.generalist, R.id.generalSwitch)
        checkCheckBox(theme.gynecology, R.id.GynecologySwitch)
        checkCheckBox(theme.neurology, R.id.neurologySwitch)
        checkCheckBox(theme.pediatry, R.id.pediatrySwitch)
        checkCheckBox(theme.traumatology, R.id.traumaTxt)
    }

    /**
     * toggle a given switch
     * @param toggle the boolean to toggle the switch
     * @param id the id of the switch to toggle
     */
    private fun checkCheckBox(toggle: Boolean, id: Int){
        findViewById<CheckBox>(id).isChecked = toggle
    }

    /**
     * Save theme data
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveData() {
        val theme = MedicalType(
            getBooleanFromSwitch(R.id.generalSwitch),
            getBooleanFromSwitch( R.id.cardioTxt),
            getBooleanFromSwitch(R.id.traumaTxt),
            getBooleanFromSwitch( R.id.pediatrySwitch),
            getBooleanFromSwitch( R.id.neurologySwitch ),
            getBooleanFromSwitch( R.id.GynecologySwitch)
        )

        storage.setObject(getString(R.string.forum_theme_key), MedicalType::class.java, theme)
        storage.push()
    }

    /**
     * return the boolean from a switch button
     */
    private fun getBooleanFromSwitch(id: Int):Boolean{
        return findViewById<CheckBox>(id).isChecked
    }
}