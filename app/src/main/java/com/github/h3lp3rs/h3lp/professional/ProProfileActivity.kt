package com.github.h3lp3rs.h3lp.professional

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf

class ProProfileActivity : AppCompatActivity() {
    private var proStatus = VerificationActivity.currentUserStatus
    private var proDomain = VerificationActivity.currentUserDomain
    private var proExperience = VerificationActivity.currentUserExperience

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_profile)

        createProPrivacyCheckBox()

        findViewById<Button>(R.id.ProProfileSaveButton).setOnClickListener { loadData() }
    }

    /**
     * Create a PrivacyCheckbox with a clickable link sending to the policy
     */
    private fun createProPrivacyCheckBox() {
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // This avoids checkbox checking when clicking on the policy link
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
        val checkBox = findViewById<CheckBox>(R.id.ProProfilePrivacyCheck)
        checkBox.text = cs
        checkBox.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showPolicy() {
        AlertDialog.Builder(this).setTitle(getString(R.string.privacy_policy))
            .setMessage(getString(R.string.pro_profile_privacy_policy)).show()
    }

    private fun loadData() {
        proStatus = findViewById<EditText>(R.id.proProfileStatusEditTxt).text.toString()
        proDomain = findViewById<EditText>(R.id.proProfileDomainEditTxt).text.toString()
        proExperience =
            findViewById<EditText>(R.id.ProProfileExperienceEditTxt).text.toString().toInt()

        val updatedProfile = ProUser(
            id = VerificationActivity.currentUserId,
            name = VerificationActivity.currentUserName,
            proofName = VerificationActivity.currentUserProofName,
            proofUri = VerificationActivity.currentUserProofUri,
            proStatus = proStatus,
            proDomain = proDomain,
            proExperience = proExperience
        )
        databaseOf(Databases.PRO_USERS).setObject(VerificationActivity.currentUserId, ProUser::class.java, updatedProfile)
    }


}