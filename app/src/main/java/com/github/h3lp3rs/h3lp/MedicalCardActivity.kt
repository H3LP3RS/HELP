package com.github.h3lp3rs.h3lp

import android.content.Intent

import android.icu.util.Calendar
import android.net.wifi.ScanResult.UNSPECIFIED
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan

import android.view.View
import android.view.View.MeasureSpec.UNSPECIFIED
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout

import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.github.h3lp3rs.h3lp.MedicalInformation.Companion.DEFAULT_COUNTRY
import com.github.h3lp3rs.h3lp.dataclasses.BloodType
import com.github.h3lp3rs.h3lp.dataclasses.Gender
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber

class MedicalCardActivity : AppCompatActivity() {

    private lateinit var storage: LocalStorage

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        storage = storageOf(MEDICAL_INFO)
        loadData()

        createBirthField()
        createHeightField()
        createWeightField()
        createBloodField()
        createGenderField()
        createPrivacyCheckBox()
        createPhoneNumberField()

        createHelpField(
            findViewById(R.id.medicalInfoConditionTxtLayout),
            getString(R.string.condition_help_msg)
        )
        createHelpField(
            findViewById(R.id.medicalInfoTreatmentTxtLayout),
            getString(R.string.treatment_help_msg)
        )
        createHelpField(
            findViewById(R.id.medicalInfoAllergyTxtLayout),
            getString(R.string.allergy_help_msg)
        )
    }


    /**
     * initialises the field for emergency contact phone number so that we make
     * sure to allow only valid phone numbers, or display an error.
     */
    private fun createPhoneNumberField() {
        val phoneInputText = findViewById<EditText>(R.id.medicalInfoContactNumberEditTxt)
        val phoneInputLayout = findViewById<TextInputLayout>(R.id.medicalInfoContactNumberTxtLayout)
        phoneInputText.doOnTextChanged { text, _, _, _ ->
            try {
                val number =
                    PhoneNumberUtil.getInstance().parse(text.toString().trimStart('0'), "CH")
                if (PhoneNumberUtil.getInstance().isPossibleNumber(number)) {
                    phoneInputLayout.error = null
                } else {
                    phoneInputLayout.error = getString(R.string.invalid_number)
                }
            } catch (e: Exception) {
                phoneInputLayout.error = getString(R.string.invalid_number)
            }
        }
    }

    /**
     * create an Field that test input and write error 7
     * @param idEditText the id of the editText to test value
     * @param idTextInputLayout the Layout where to display erro
     * @param min the min margin
     * @param max the max margin
     * @param minErrorMsg the message to display if smallest than min
     * @param maxErrorMsg the message to display if biggest than min
     */
    private fun createTestField(
        idEditText: Int, idTextInputLayout: Int, min: Int, max: Int,
        minErrorMsg: String, maxErrorMsg: String
    ) {
        val editText = findViewById<EditText>(idEditText)
        val textInputLayout = findViewById<TextInputLayout>(idTextInputLayout)
        editText.doOnTextChanged { text, _, _, _ ->
            when {
                text!!.isEmpty() -> {
                    textInputLayout.error = getString(R.string.empty_error_msg)
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


    /**
     * create the field for the birth year with input check
     */
    private fun createBirthField() {
        createTestField(
            R.id.medicalInfoBirthEditTxt, R.id.medicalInfoBirthTxtLayout,
            resources.getInteger(R.integer.minYear), Calendar.getInstance().get(Calendar.YEAR),
            getString(R.string.yearTooOld), getString(R.string.yearTooRecent)
        )
    }

    /**
     * create the field for the height with input check
     */
    private fun createHeightField() {
        createTestField(
            R.id.medicalInfoHeightEditTxt, R.id.medicalInfoHeightTxtLayout,
            resources.getInteger(R.integer.minHeight), resources.getInteger(R.integer.maxHeight),
            getString(R.string.heightTooShort), getString(R.string.heightTooBig)
        )
    }

    /**
     * create the field for the weight with input check
     */
    private fun createWeightField() {
        createTestField(
            R.id.medicalInfoWeightEditTxt, R.id.medicalInfoWeightTxtLayout,
            resources.getInteger(R.integer.minWeight), resources.getInteger(R.integer.maxWeight),
            getString(R.string.weightTooLight), getString(R.string.weightTooHeavy)
        )
    }

    /**
     * create Blood type dropDown menu in an InputTextLayout
     */
    private fun createBloodField() {
        createDropdownField(BloodType.values().map { it.type }, R.id.medicalInfoBloodDropdown)
    }

    /**
     * create Gender dropDown menu in an InputTextLayout
     */
    private fun createGenderField() {
        createDropdownField(Gender.values().map { it.name }, R.id.medicalInfoGenderDropdown)
    }

    /**
     * create Dropdown menu compatible with a TextInputLayout using autocomplete
     * @param list list of element of the dropdown
     * @param dropdownId of the dropdown layout
     */
    private fun createDropdownField(list: List<String>, dropdownId: Int) {
        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup,
            list
        )

        val editTextFilledExposedDropdown =
            findViewById<AutoCompleteTextView>(dropdownId)

        editTextFilledExposedDropdown.setAdapter(adapter)
    }


    /**
     * Create an help button at the end of the text layout with
     * @param textLayout TeytInputLayout
     * @param str the help message
     */
    private fun createHelpField(textLayout: TextInputLayout, str: String) {
        textLayout.setEndIconOnClickListener { createSnackbar(it, str) }
    }


    /**
     * Create a stylized Snackbar
     * @param it the view in which the snack should appeared
     * @param str the message to display
     */
    private fun createSnackbar(it: View, str: String) {
        val snack = Snackbar.make(it, str, Snackbar.LENGTH_LONG)
        snack.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snack.setBackgroundTint(ContextCompat.getColor(this, R.color.teal_200))
        snack.show()
    }

    /**
     * Create a PrivacyCheckbox with a clickable link sending to the policy
     */
    private fun createPrivacyCheckBox() {
        val checkBox = findViewById<CheckBox>(R.id.medicalInfoPrivacyCheck)

        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // This avoids checkbox checking when clicking on the policy link
                // Sadly it is incompatible with Expresso onClick() so is commented for cirrus
                // widget.cancelPendingInputEvents()
                showPolicy()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        val linkText = SpannableString(getString(R.string.privacy_policy))
        linkText.setSpan(clickableSpan, 0, linkText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val cs = TextUtils.expandTemplate("I accept the ^1", linkText)
        checkBox.text = cs
        checkBox.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showPolicy() {
        AlertDialog.Builder(this).setTitle(getString(R.string.privacy_policy))
            .setMessage(getString(R.string.medical_info_privacy_policy)).show()
    }

    /**
     * Send back to MainPageActivity
     */

    fun backHome(view: View) {
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
    }

    /**
     * Load medical card data
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {
        val medicalInformation = storage.getObjectOrDefault(
            getString(R.string.medical_info_key),
            MedicalInformation::class.java, null
        ) ?: return

        loadTo(medicalInformation.size, R.id.medicalInfoHeightEditTxt)
        loadTo(medicalInformation.yearOfBirth, R.id.medicalInfoBirthEditTxt)
        loadTo(medicalInformation.weight, R.id.medicalInfoWeightEditTxt)
        loadTo(medicalInformation.conditions, R.id.medicalInfoConditionEditTxt)
        loadTo(medicalInformation.actualTreatment, R.id.medicalInfoTreatmentEditTxt)
        loadTo(medicalInformation.allergy, R.id.medicalInfoAllergyEditTxt)
        loadTo(medicalInformation.gender.name, R.id.medicalInfoGenderDropdown)
        loadTo(medicalInformation.bloodType.type, R.id.medicalInfoBloodDropdown)
    }

    /**
     * load string in an editTxt
     */
    private fun loadTo(data: String, editTxtId: Int) {
        findViewById<EditText>(editTxtId).setText(data)
    }

    /**
     * load int in an editTxt
     */
    private fun loadTo(data: Int, editTxtId: Int) {
        loadTo(data.toString(), editTxtId)
    }

    /**
     * Check the validity of the field and that the policy is checked and then save the data
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkAndSaveChanges(view: View) {
        if (!checkField()) {
            createSnackbar(view, getString(R.string.invalid_field_msg))
        } else if (!checkPolicy()) {
            createSnackbar(view, getString(R.string.privacy_policy_not_acceptes))
        } else {
            saveChanges()
            createSnackbar(view, getString(R.string.changes_saved))
        }
    }

    /**
     * Check validity of the field
     */
    private fun checkField(): Boolean {
        val size = findViewById<TextInputLayout>(R.id.medicalInfoHeightTxtLayout)
        val year = findViewById<TextInputLayout>(R.id.medicalInfoBirthTxtLayout)
        val weight = findViewById<TextInputLayout>(R.id.medicalInfoWeightTxtLayout)
        val gender = findViewById<AutoCompleteTextView>(R.id.medicalInfoGenderDropdown)
        val bloodType = findViewById<AutoCompleteTextView>(R.id.medicalInfoBloodDropdown)
        val contactNumber = findViewById<TextInputLayout>(R.id.medicalInfoContactNumberTxtLayout)
        return size.error == null && year.error == null && weight.error == null
                && gender.text != null && bloodType != null && contactNumber != null
    }

    /**
     * Check that the policy was accepted
     */
    private fun checkPolicy(): Boolean {
        return findViewById<CheckBox>(R.id.medicalInfoPrivacyCheck).isChecked
    }

    /**
     * Save the medical card information
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveChanges() {
        val size: Int = getIntFromId(R.id.medicalInfoHeightEditTxt)
        val year: Int = getIntFromId(R.id.medicalInfoBirthEditTxt)
        val weight: Int = getIntFromId(R.id.medicalInfoWeightEditTxt)
        val condition: String = getStringFromId(R.id.medicalInfoConditionEditTxt)
        val treatment: String = getStringFromId(R.id.medicalInfoTreatmentEditTxt)
        val allergy: String = getStringFromId(R.id.medicalInfoAllergyEditTxt)
        val gender: Gender =
            Gender.valueOf(findViewById<AutoCompleteTextView>(R.id.medicalInfoGenderDropdown).text.toString())
        val bloodType: BloodType = BloodType.valueOf(
            findViewById<AutoCompleteTextView>(R.id.medicalInfoBloodDropdown).text.toString()
                .replace('-', 'n').replace('+', 'p')
        )
        val contactName = getStringFromId(R.id.medicalInfoContactNameEditTxt)

        val contactNumber = getStringFromId(R.id.medicalInfoContactNumberEditTxt)
        val number = PhoneNumberUtil.getInstance().parse(
            contactNumber.trimStart('0'), DEFAULT_COUNTRY)

        val formattedNumber = String.format("+%d%d", number.countryCode, number.nationalNumber)

        val medicalInformation = MedicalInformation(
            size, weight, gender, year, condition, treatment,
            allergy, bloodType, contactName, formattedNumber
        )

        storage.setObject(
            getString(R.string.medical_info_key),
            MedicalInformation::class.java,
            medicalInformation
        )
        storage.push()
    }

    private fun getStringFromId(editTxtId: Int): String {
        return findViewById<EditText>(editTxtId).text.toString()
    }

    private fun getIntFromId(editTxtId: Int): Int {
        return getStringFromId(editTxtId).toInt()
    }
}