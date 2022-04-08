package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.github.h3lp3rs.h3lp.dataclasses.HelperSkills
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.google.android.material.switchmaterial.SwitchMaterial

class MySkillsActivity : AppCompatActivity() {

    private lateinit var storage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_skills)
        storage = storageOf(SKILLS)
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
     * Function for the back button to go back to MainActivity
     */
    fun backHome(view: View){
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
    }

    /**
     * Show a dialogue with explication on what is the form for
     */
    fun helpDialogue(view: View){
        AlertDialog.Builder(this).setTitle(getString(R.string.my_helper_skills))
            .setMessage(getString(R.string.help_my_skills)).show()
    }

    /**
     * Load skills data
     */
    private fun loadData() {
        val skills = storage.getObjectOrDefault(getString(R.string.my_skills_key),
            HelperSkills::class.java, null) ?: return

        toggleSwitch(skills.hasEpipen, R.id.epipenSwitch)
        toggleSwitch(skills.hasVentolin, R.id.ventolinSwitch)
        toggleSwitch(skills.hasInsulin, R.id.insulinSwitch)
        toggleSwitch(skills.knowsCPR, R.id.cprSwitch)
        toggleSwitch(skills.hasFirstAidKit, R.id.firstAidSwitch)
        toggleSwitch(skills.isMedicalPro, R.id.doctorSwitch)
    }

    private fun toggleSwitch(toggle: Boolean, id: Int){
        findViewById<SwitchMaterial>(id).isChecked = toggle
    }

    /**
     * Save skills data
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveData() {
        val skills = HelperSkills(
            getBooleanFromSwitch(R.id.epipenSwitch),
            getBooleanFromSwitch(R.id.ventolinSwitch),
            getBooleanFromSwitch(R.id.insulinSwitch),
            getBooleanFromSwitch(R.id.cprSwitch),
            getBooleanFromSwitch(R.id.firstAidSwitch),
            getBooleanFromSwitch(R.id.doctorSwitch)
        )
        storage.setObject(getString(R.string.my_skills_key), HelperSkills::class.java, skills)
        storage.push()
    }

    /**
     * return the boolean from a switch button
     */
    private fun getBooleanFromSwitch(id: Int):Boolean{
        return findViewById<SwitchMaterial>(id).isChecked
    }
}