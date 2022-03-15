package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout

import android.widget.AutoCompleteTextView

import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson


class MedicalCardAcivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        loadData()

        createBirthField()
        createHeightField()
        createWeightField()
        createBloodField()
        createGenderField()

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun createBirthField() {
        val birthTxt = findViewById<EditText>(R.id.medicalInfoBirthEditTxt)
        val birthLayout = findViewById<TextInputLayout>(R.id.medicalInfoBirthTxtLayout)
        birthTxt.doOnTextChanged { text, _, _, _ ->
            when {
                text!!.isEmpty() -> {
                    birthLayout.error = null
                }
                text.toString().toInt() > Calendar.getInstance().get(Calendar.YEAR) -> {
                    birthLayout.error = getString(R.string.yearTooRecent)
                }
                text.toString().toInt() < resources.getInteger(R.integer.minYear)  -> {
                    birthLayout.error = getString(R.string.yearTooOld)
                }
                else -> {
                    birthLayout.error = null
                }
            }
        }
    }

    private fun createSnackbar(it: View, str: String) {
        val snack = Snackbar.make(it, str, Snackbar.LENGTH_LONG)
        snack.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snack.setBackgroundTint(ContextCompat.getColor(this, R.color.teal_200))
        snack.show()
    }

    private fun createHeightField() {
        val heightTxt = findViewById<EditText>(R.id.medicalInfoHeightEditTxt)
        val heightLayout = findViewById<TextInputLayout>(R.id.medicalInfoHeightTxtLayout)
        heightTxt.doOnTextChanged { text, _, _, _ ->
            when {
                text!!.isEmpty() -> {
                    heightLayout.error = null
                }
                text.toString().toInt() >  resources.getInteger(R.integer.maxHeight) -> {
                    heightLayout.error = getString(R.string.heightTooBig)
                }
                text.toString().toInt() <  resources.getInteger(R.integer.minHeight) -> {
                    heightLayout.error = getString(R.string.heightTooShort)
                }
                else -> {
                    heightLayout.error = null
                }
            }
        }
    }

    private fun createWeightField() {
        val weightTxt = findViewById<EditText>(R.id.medicalInfoWeightEditTxt)
        val weightLayout = findViewById<TextInputLayout>(R.id.medicalInfoWeightTxtLayout)
        weightTxt.doOnTextChanged { text, _, _, _ ->
            when {
                text!!.isEmpty() -> {
                    weightLayout.error = null
                }
                text.toString().toInt() <  resources.getInteger(R.integer.minWeight) -> {
                    weightLayout.error = getString(R.string.weightTooLight)
                }
                text.toString().toInt() >  resources.getInteger(R.integer.maxWeight) -> {
                    weightLayout.error = getString(R.string.weightTooHeavy)
                }
                else -> {
                    weightLayout.error = null
                }
            }
        }
    }

    private fun createBloodField(){
        val bloodType = BloodType.values().map{it.type}
        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup,
            bloodType
        )

        val editTextFilledExposedDropdown =
            findViewById<AutoCompleteTextView>(R.id.medicalInfoBloodDropdown)
        editTextFilledExposedDropdown.setAdapter(adapter)
    }

    private fun createGenderField(){
        val bloodType = Gender.values().map{it.name}
        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup,
            bloodType
        )

        val editTextFilledExposedDropdown =
            findViewById<AutoCompleteTextView>(R.id.medicalInfoGenderDropdown)
        editTextFilledExposedDropdown.setAdapter(adapter)
    }


    fun backHome( view: View){
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
    }


    private fun loadData(){
        val preferences = getSharedPreferences("medicalInfoPrefs", MODE_PRIVATE)
        val json = preferences.getString("MEDICAL_INFO_KEY",null)
        val gson = Gson()
        val medicalInformation = gson.fromJson(json, MedicalInformation::class.java) ?: return



        findViewById<EditText>(R.id.medicalInfoHeightEditTxt).setText(medicalInformation.size.toString())
        findViewById<EditText>(R.id.medicalInfoBirthEditTxt).setText(medicalInformation.yearOfBirth.toString())
        findViewById<EditText>(R.id.medicalInfoWeightEditTxt).setText(medicalInformation.weight.toString())
        findViewById<EditText>(R.id.medicalInfoConditionEditTxt).setText(medicalInformation.conditions)
        findViewById<EditText>(R.id.medicalInfoTreatmentEditTxt).setText(medicalInformation.actualTreatment)
        findViewById<EditText>(R.id.medicalInfoAllergyEditTxt).setText(medicalInformation.allergy)
        findViewById<AutoCompleteTextView>(R.id.medicalInfoGenderDropdown).setText(medicalInformation.gender.name)
        findViewById<AutoCompleteTextView>(R.id.medicalInfoBloodDropdown).setText(medicalInformation.bloodType.type)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun CheckAndSaveChanges(view: View){

        if(!checkField()){
            createSnackbar(view,getString(R.string.invalid_field_msg))
        }
        else if(!checkPoliciy()){
            createSnackbar(view,getString(R.string.privacy_policy_not_acceptes))
        }
        else{
            saveChanges()
            createSnackbar(view,getString( R.string.changes_saved))
        }
    }

    private fun checkField(): Boolean{
        val size  = findViewById<TextInputLayout>(R.id.medicalInfoHeightTxtLayout)
        val year  = findViewById<TextInputLayout>(R.id.medicalInfoBirthTxtLayout)
        val weight = findViewById<TextInputLayout>(R.id.medicalInfoWeightTxtLayout)
        val gender  = findViewById<AutoCompleteTextView>(R.id.medicalInfoGenderDropdown)
        val bloodType = findViewById<AutoCompleteTextView>(R.id.medicalInfoBloodDropdown)
        return size.error==null && year.error==null && weight.error==null && gender.text!=null && bloodType!=null
    }
    private fun checkPoliciy(): Boolean{
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveChanges(){
        val size : Int = findViewById<EditText>(R.id.medicalInfoHeightEditTxt).text.toString().toInt()
        val year : Int = findViewById<EditText>(R.id.medicalInfoBirthEditTxt).text.toString().toInt()
        val weight : Int = findViewById<EditText>(R.id.medicalInfoWeightEditTxt).text.toString().toInt()
        val condition : String = findViewById<EditText>(R.id.medicalInfoConditionEditTxt).text.toString()
        val treatment : String = findViewById<EditText>(R.id.medicalInfoTreatmentEditTxt).text.toString()
        val allergy : String = findViewById<EditText>(R.id.medicalInfoAllergyEditTxt).text.toString()
        val gender : Gender = Gender.valueOf(findViewById<AutoCompleteTextView>(R.id.medicalInfoGenderDropdown).text.toString())
        val bloodType : BloodType = BloodType.valueOf(findViewById<AutoCompleteTextView>(R.id.medicalInfoBloodDropdown).text.toString()
            .replace('-','n').replace('+','p'))

        val medicalInformation = MedicalInformation(size,weight,gender,year,condition,treatment,allergy,bloodType )

        val preferencesEditor = getSharedPreferences(getString(R.string.medical_info_prefs), MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(medicalInformation)
        preferencesEditor.putString(getString(R.string.medical_info_key),json).apply()

    }
}