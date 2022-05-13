package com.github.h3lp3rs.h3lp.professional

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.professional.VerificationActivity.Companion.currentUserId
import com.github.h3lp3rs.h3lp.professional.VerificationActivity.Companion.currentUserName
import com.github.h3lp3rs.h3lp.professional.VerificationActivity.Companion.currentUserProofName
import com.github.h3lp3rs.h3lp.professional.VerificationActivity.Companion.currentUserProofUri
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

/**
 * Activity that updates the professional users data
 */
class ProProfileActivity : AppCompatActivity() {
    // Initially, those fields have a default value
    private var proStatus = VerificationActivity.currentUserStatus
    private var proDomain = VerificationActivity.currentUserDomain
    private var proExperience = VerificationActivity.currentUserExperience

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_profile)

        createProPrivacyCheckBox()
    }

    /**
     * Create a professional PrivacyCheckbox with a clickable link sending to the policy
     */
    private fun createProPrivacyCheckBox() {
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                showPolicy()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }
        val linkText = SpannableString(getString(R.string.privacy_policy))
        linkText.setSpan(clickableSpan, 0, linkText.length, SPAN_EXCLUSIVE_EXCLUSIVE)
        val cs = TextUtils.expandTemplate("I accept the ^1", linkText)
        val checkBox = findViewById<CheckBox>(R.id.proProfilePrivacyCheck)
        checkBox.text = cs
        checkBox.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Displays the professional policy rules
     */
    private fun showPolicy() {
        AlertDialog.Builder(this).setTitle(getString(R.string.privacy_policy))
            .setMessage(getString(R.string.pro_profile_privacy_policy)).show()
    }

    /**
     * Loads the input data into the database to update the professional profile
     */
    private fun loadData() {
        proStatus = findViewById<EditText>(R.id.proProfileStatusEditTxt).text.toString()
        proDomain = findViewById<EditText>(R.id.proProfileDomainEditTxt).text.toString()
        proExperience = findViewById<EditText>(R.id.proProfileExperienceEditTxt).text.toString()

        val updatedProfile = ProUser(
            id = currentUserId,
            name = currentUserName,
            proofName = currentUserProofName,
            proofUri = currentUserProofUri,
            proStatus = proStatus,
            proDomain = proDomain,
            proExperience = proExperience
        )
        databaseOf(Databases.PRO_USERS).setObject(VerificationActivity.currentUserId, ProUser::class.java, updatedProfile)
    }

    /**
     * Checks that the policy was accepted
     */
    private fun checkPolicy(): Boolean {
        return findViewById<CheckBox>(R.id.proProfilePrivacyCheck).isChecked
    }


    /**
     * Creates a snackBar containing a message
     *
     * @param view The view where to display the snackbar
     * @param message The message to display
     */
    private fun createSnackBar(view: View, message: String) {
        val snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snack.setBackgroundTint(ContextCompat.getColor(this, R.color.teal_200))
        snack.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        snack.show()
    }

    /**
     * Checks if the policy is accepted and if so, loads data into the database
     *
     * @param view The view from where to load data
     */
    fun checkAndLoadData(view: View){
        if (!checkPolicy()) {
            createSnackBar(view, getString(R.string.privacy_policy_not_accepted))
        } else {
            loadData()
            createSnackBar(view, getString(R.string.changes_saved))
        }

    }
}