package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.github.h3lp3rs.h3lp.dataclasses.BloodType
import com.github.h3lp3rs.h3lp.dataclasses.Gender
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation.Companion.ADULT_AGE
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation.Companion.DEFAULT_COUNTRY
import com.github.h3lp3rs.h3lp.dataclasses.MedicalInformation.Companion.EMPTY_NB
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.github.h3lp3rs.h3lp.storage.Storages.MEDICAL_INFO
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.i18n.phonenumbers.PhoneNumberUtil


class MedicalCardActivity : AppCompatActivity() {

    private lateinit var storage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        storage = storageOf(MEDICAL_INFO)
        loadData()

        // Create the field for the birth year with input check
        createTestField(
            R.id.medicalInfoBirthEditTxt,
            R.id.medicalInfoBirthTxtLayout,
            resources.getInteger(R.integer.minYear),
            Calendar.getInstance().get(Calendar.YEAR) - ADULT_AGE,
            getString(R.string.yearTooOld),
            getString(R.string.yearTooRecent)
        )

        // Create the field for the height with input check
        createTestField(
            R.id.medicalInfoHeightEditTxt, R.id.medicalInfoHeightTxtLayout,
            resources.getInteger(R.integer.minHeight), resources.getInteger(R.integer.maxHeight),
            getString(R.string.heightTooShort), getString(R.string.heightTooBig)
        )

        // Create the field for the weight with input check
        createTestField(
            R.id.medicalInfoWeightEditTxt, R.id.medicalInfoWeightTxtLayout,
            resources.getInteger(R.integer.minWeight), resources.getInteger(R.integer.maxWeight),
            getString(R.string.weightTooLight), getString(R.string.weightTooHeavy)
        )

        // Create Blood type dropDown menu in an InputTextLayout
        createDropdownField(BloodType.values().map { it.type }, R.id.medicalInfoBloodDropdown)

        // Create Gender dropDown menu in an InputTextLayout
        createDropdownField(Gender.values().map { it.name }, R.id.medicalInfoGenderDropdown)

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
     * Initialises the field for emergency contact phone number so that we make
     * sure to allow only valid phone numbers, or display an error.
     */

    private fun createPhoneNumberField() {
        val phoneInputText = findViewById<EditText>(R.id.medicalInfoContactNumberEditTxt)
        val phoneInputLayout = findViewById<TextInputLayout>(R.id.medicalInfoContactNumberTxtLayout)
        phoneInputText.doOnTextChanged { text, _, _, _ ->
            when {
                text!!.isEmpty() -> {
                    phoneInputLayout.error = ""
                }
                else -> {
                    try {
                        val number =
                            PhoneNumberUtil.getInstance().parse(
                                text.toString().trimStart('0'),
                                DEFAULT_COUNTRY
                            )
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
        }

    }


    /**
     * Creates a Field that tests the input and writes error 7
     * @param idEditText The id of the editText to test value
     * @param idTextInputLayout The Layout where to display erro
     * @param min The min margin
     * @param max The max margin
     * @param minErrorMsg The message to display if smallest than min
     * @param maxErrorMsg The message to display if biggest than min
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
     * Create a stylized Snackbar
     * @param it The view in which the snack should appeared
     * @param str The message to display
     */
    private fun createSnackbar(it: View, str: String) {
        val snack = Snackbar.make(it, str, Snackbar.LENGTH_LONG)
        snack.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snack.setBackgroundTint(ContextCompat.getColor(this, R.color.persimmon))
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
        loadTo(medicalInformation.emergencyContactNumber, R.id.medicalInfoContactNumberEditTxt)
        loadTo(medicalInformation.emergencyContactPrimaryName, R.id.medicalInfoContactNameEditTxt)

    }

    /**
     * Writes the given text into a view
     * @param data The text to write to the view
     * @param editTxtId The id of the view to write to
     */
    private fun loadTo(data: String, editTxtId: Int) {
        findViewById<EditText>(editTxtId).setText(data)
    }

    /**
     * Overrides loadTo by writing the given int into a view
     * @param data The int to write to the view
     * @param editTxtId The id of the view to write to
     */
    private fun loadTo(data: Int, editTxtId: Int) {
        loadTo(data.toString(), editTxtId)
    }

    /**
     * Check the validity of the field and that the policy is checked and then save the data
     */

    fun checkAndSaveChanges(view: View) {
        if (!checkField()) {
            createSnackbar(view, getString(R.string.invalid_field_msg))
        } else if (checkNull()) {
            createSnackbar(view, getString(R.string.invalid_empty_msg))
        } else if (!checkPolicy()) {
            createSnackbar(view, getString(R.string.privacy_policy_not_accepted))
        } else {
            saveChanges()
            createSnackbar(view, getString(R.string.changes_saved))
        }
    }

    /**
     * Check that no deterministic field is left empty
     * @return True if any of the fields is empty, false otherwise
     */
    private fun checkNull(): Boolean {
        return textIsEmpty(R.id.medicalInfoHeightEditTxt) ||
                textIsEmpty(R.id.medicalInfoWeightEditTxt) ||
                textIsEmpty(R.id.medicalInfoBirthEditTxt) ||
                noChoiceSelected(R.id.medicalInfoGenderDropdown) ||
                noChoiceSelected(R.id.medicalInfoBloodDropdown)
    }

    /**
     * Checks if a view's text is empty
     * @param id The view's id
     * @return True if the view's text is empty, false otherwise
     */
    private fun textIsEmpty(id: Int): Boolean {
        return findViewById<EditText>(id).text.toString().isEmpty()
    }

    /**
     * Checks if an autocompleted view's text is empty
     * @param id The view's id
     * @return True if the view's text is empty, false otherwise
     */
    private fun noChoiceSelected(id: Int): Boolean {
        return findViewById<AutoCompleteTextView>(id).text.toString().isEmpty()
    }

    /**
     * Check validity of the field
     * @return True if all the answers to the fields are valid, false otherwise
     */
    private fun checkField(): Boolean {
        val size = findViewById<TextInputLayout>(R.id.medicalInfoHeightTxtLayout)
        val year = findViewById<TextInputLayout>(R.id.medicalInfoBirthTxtLayout)
        val weight = findViewById<TextInputLayout>(R.id.medicalInfoWeightTxtLayout)
        val gender = findViewById<AutoCompleteTextView>(R.id.medicalInfoGenderDropdown)
        val bloodType = findViewById<AutoCompleteTextView>(R.id.medicalInfoBloodDropdown)
        val contactNumber = findViewById<TextInputLayout>(R.id.medicalInfoContactNumberTxtLayout)
        return size.error == null && year.error == null && weight.error == null
                && gender.text != null && bloodType.text != null && contactNumber.error == null
    }

    /**
     * Check that the policy was accepted
     * @return True if the medical privacy policy's checkbox was indeed accepted / checked by the
     * user, false otherwise
     */
    private fun checkPolicy(): Boolean {
        return findViewById<CheckBox>(R.id.medicalInfoPrivacyCheck).isChecked
    }

    /**
     * Save the medical card information
     */

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
        var formattedNumber = EMPTY_NB
        if (contactNumber != EMPTY_NB) {
            val number =
                PhoneNumberUtil.getInstance().parse(
                    contactNumber.trimStart('0'), DEFAULT_COUNTRY
                )
            formattedNumber = String.format("+%d%d", number.countryCode, number.nationalNumber)

        }


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


    /**
     * Create an help button at the end of the text layout with
     * @param textLayout TeytInputLayout
     * @param str the help message
     */
    private fun createHelpField(textLayout: TextInputLayout, str: String) {
        textLayout.setEndIconOnClickListener { createSnackbar(it, str) }
    }

    /**
     * Create Dropdown menu compatible with a TextInputLayout using autocomplete
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
     * Returns the text from a text view
     * @param editTxtId The text view's id
     * @return The text contained in this text view
     */
    private fun getStringFromId(editTxtId: Int): String {
        return findViewById<EditText>(editTxtId).text.toString()
    }

    /**
     * Returns the int contained in a text view (in case that view was used to store numbers
     * exclusively)
     * @param editTxtId The text view's id
     * @return The int contained in this text view
     */
    private fun getIntFromId(editTxtId: Int): Int {
        return getStringFromId(editTxtId).toInt()
    }
}