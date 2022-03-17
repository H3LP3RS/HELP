package com.github.h3lp3rs.h3lp

import android.content.Intent
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


class MedicalCardActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        createBirthField()
        createHeightField()
        createWeightField()
        createBloodField()
        createGenderField()

        createHelpField(findViewById(R.id.medicalInfoConditionTxtLayout), getString(R.string.condition_help_msg))
        createHelpField(findViewById(R.id.medicalInfoTreatmentTxtLayout), getString(R.string.treatment_help_msg))
        createHelpField(findViewById(R.id.medicalInfoAllergyTxtLayout), getString(R.string.allergy_help_msg))


    }

    private fun createHelpField( textLayout: TextInputLayout, str: String) {
        textLayout.setEndIconOnClickListener { createSnackbar(it, str) }
    }

    private fun createSnackbar(it: View, str: String) {
        val snack = Snackbar.make(it, str, Snackbar.LENGTH_LONG)
        snack.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snack.setBackgroundTint(ContextCompat.getColor(this, R.color.teal_400))
        snack.show()
    }


    private fun createTestField( idEditText: Int, idTextInputLayout: Int , min : Int  , max : Int, minErrorMsg : String, maxErrorMsg : String) {
        val editText = findViewById<EditText>(idEditText)
        val textInputLayout = findViewById<TextInputLayout>(idTextInputLayout)
        editText.doOnTextChanged { text, _, _, _ ->
            when {
                text!!.isEmpty() -> {
                    textInputLayout.error = null
                }
                text.toString().toInt() > max -> {
                    textInputLayout.error = maxErrorMsg
                }
                text.toString().toInt() < min -> {
                    textInputLayout.error = minErrorMsg
                }
                else -> {
                    textInputLayout.error = null
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createBirthField() {
        createTestField(R.id.medicalInfoBirthEditTxt, R.id.medicalInfoBirthTxtLayout,
            resources.getInteger(R.integer.minYear),Calendar.getInstance().get(Calendar.YEAR),
            getString(R.string.yearTooOld), getString(R.string.yearTooRecent))
    }

    private fun createHeightField() {
        createTestField(R.id.medicalInfoHeightEditTxt, R.id.medicalInfoHeightTxtLayout,
            resources.getInteger(R.integer.minHeight),resources.getInteger(R.integer.maxHeight),
            getString(R.string.heightTooShort), getString(R.string.heightTooBig))
    }

    private fun createWeightField() {
        createTestField(R.id.medicalInfoWeightEditTxt, R.id.medicalInfoWeightTxtLayout,
            resources.getInteger(R.integer.minWeight),resources.getInteger(R.integer.maxWeight),
            getString(R.string.weightTooLight), getString(R.string.weightTooHeavy))

    }

    private fun createBloodField(){
        val bloodType = BloodType.values().map{it.type}
        val adapter = ArrayAdapter(this, R.layout.dropdown_menu_popup, bloodType)
        val editTextFilledExposedDropdown = findViewById<AutoCompleteTextView>(R.id.medicalInfoBloodDropdown)
        editTextFilledExposedDropdown.setAdapter(adapter)
    }

    private fun createGenderField(){
        val bloodType = Gender.values().map{it.sex}
        val adapter = ArrayAdapter(this, R.layout.dropdown_menu_popup, bloodType)
        val editTextFilledExposedDropdown = findViewById<AutoCompleteTextView>(R.id.medicalInfoGenderDropdown)
        editTextFilledExposedDropdown.setAdapter(adapter)
    }


    fun backHome( view: View){
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
    }
}