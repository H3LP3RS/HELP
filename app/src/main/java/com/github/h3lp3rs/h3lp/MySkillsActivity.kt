package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.github.h3lp3rs.h3lp.dataClass.helperSkills
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson

class MySkillsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_skills)
        loadData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        super.onStop()
        saveData()
    }

    fun backHome( view: View){
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
    }
    fun helpDialogue( view: View){
        AlertDialog.Builder(this).setTitle(getString(R.string.my_helper_skills)).setMessage(getString(R.string.help_my_skills)).show()

    }

    /**
     * Load skills data
     */
    private fun loadData() {
        val preferences = getSharedPreferences(getString(R.string.my_skills_prefs), MODE_PRIVATE)
        val json = preferences.getString(getString(R.string.my_skills_key), null)
        val gson = Gson()
        val skills = gson.fromJson(json, helperSkills::class.java) ?: return

        toggleSwitch(skills.hasEpipen,R.id.epipenSwitch)
        toggleSwitch(skills.hasVentolin,R.id.ventolinSwitch)
        toggleSwitch(skills.hasInsulin,R.id.insulinSwitch)
        toggleSwitch(skills.knowCPR,R.id.cprSwitch)
        toggleSwitch(skills.hasFirstAidKit,R.id.firstAidSwitch)
        toggleSwitch(skills.isMedicalPro,R.id.docctorSwitch)

    }

    private fun toggleSwitch(toggle:Boolean, id : Int){
        findViewById<SwitchMaterial>(id).isChecked = toggle
    }

    /**
     * Save skills data
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveData(){

        val skills = helperSkills(
            getBooleanFromSwitch(R.id.epipenSwitch),
            getBooleanFromSwitch(R.id.ventolinSwitch),
            getBooleanFromSwitch(R.id.insulinSwitch),
            getBooleanFromSwitch(R.id.cprSwitch),
            getBooleanFromSwitch(R.id.firstAidSwitch),
            getBooleanFromSwitch(R.id.docctorSwitch)
        )
        val preferencesEditor = getSharedPreferences(getString(R.string.my_skills_prefs), MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(skills)
        preferencesEditor.putString(getString(R.string.my_skills_key),json).apply()
    }

    private fun getBooleanFromSwitch(id : Int):Boolean{
        return findViewById<SwitchMaterial>(id).isChecked

    }
}