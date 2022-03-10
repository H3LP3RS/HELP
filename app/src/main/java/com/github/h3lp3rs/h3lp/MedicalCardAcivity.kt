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


class MedicalCardAcivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

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
                text.toString().toInt() < 1900 -> {
                    birthLayout.error = getString(R.string.yearTooOld)
                }
                else -> {
                    birthLayout.error = null
                }
            }
        }
    }

    private fun createHeightField() {
        val heightTxt = findViewById<EditText>(R.id.medicalInfoHeightEditTxt)
        val heightLayout = findViewById<TextInputLayout>(R.id.medicalInfoHeightTxtLayout)
        heightTxt.doOnTextChanged { text, _, _, _ ->
            when {
                text!!.isEmpty() -> {
                    heightLayout.error = null
                }
                text.toString().toInt() > 250 -> {
                    heightLayout.error = getString(R.string.heightTooBig)
                }
                text.toString().toInt() < 80 -> {
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
                text.toString().toInt() < 20 -> {
                    weightLayout.error = getString(R.string.weightTooLight)
                }
                text.toString().toInt() > 500 -> {
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
        val bloodType = Gender.values().map{it.sex}
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
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}