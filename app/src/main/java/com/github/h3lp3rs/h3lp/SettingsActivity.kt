package com.github.h3lp3rs.h3lp

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.h3lp3rs.h3lp.signin.GoogleSignInAdapter.getCreationDate
import com.github.h3lp3rs.h3lp.signin.GoogleSignInAdapter.signOut
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.getUid
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        loadSyncPref()
        findViewById<TextView>(R.id.unique_id_text).text = getUid()
        findViewById<TextView>(R.id.user_since_text).text = getCreationDate()

        enforceSignInToCheck(R.id.medical_info_checkbox)
        enforceSignInToCheck(R.id.user_cookie_checkbox)
        enforceSignInToCheck(R.id.my_skills_checkbox)
    }

    /**
     * Adds a listener so that a checkbox cannot be checked unless the user
     * is signed in.
     * @param id the id of the check box
     */
    private fun enforceSignInToCheck(id: Int) {
        val checkBox = findViewById<CheckBox>(id)
        checkBox.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            if (getUid() == null) {
                checkBox.isChecked = false
                showSignInPopUp()
            }
        }
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
    fun backHome(view: View) {
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
    }

    /**
     * Function for the logout button to disconnect from account
     */
    fun logout(view: View) {
        val userSignIn = storageOf(SIGN_IN)
        userSignIn.setBoolean(getString(R.string.KEY_USER_SIGNED_IN), false)
        signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    fun clearSync(view: View) {
        storageOf(MEDICAL_INFO).clearOnlineSync()
        storageOf(USER_COOKIE).clearOnlineSync()
        storageOf(SKILLS).clearOnlineSync()
    }

    /**
     * Load synch data
     */
    private fun loadSyncPref() {
        check(MEDICAL_INFO.getOnlineSync(), R.id.medical_info_checkbox)
        check(USER_COOKIE.getOnlineSync(), R.id.user_cookie_checkbox)
        check(SKILLS.getOnlineSync(), R.id.my_skills_checkbox)
    }

    /**
     * Check a given checkbox
     * @param toggle if it must check or not
     * @param id the id of the checkbox
     */
    private fun check(toggle: Boolean, id: Int) {
        findViewById<CheckBox>(id).isChecked = toggle
    }

    /**
     * Save synchronized data
     */
    private fun saveData() {
        MEDICAL_INFO.setOnlineSync(getBooleanFromSwitch(R.id.medical_info_checkbox))
        USER_COOKIE.setOnlineSync(getBooleanFromSwitch(R.id.user_cookie_checkbox))
        SKILLS.setOnlineSync(getBooleanFromSwitch(R.id.my_skills_checkbox))
    }

    /**
     * Return the boolean from a switch button
     * @param id The switch button's id
     * @return True if the switch was checked, false otherwise
     */
    private fun getBooleanFromSwitch(id: Int): Boolean {
        return findViewById<CheckBox>(id).isChecked
    }

    /**
     * Opens a popup asking the user to sign in to continue.
     */
    private fun showSignInPopUp() {
        val dialog = Dialog(this)
        val signInPopup =
            layoutInflater.inflate(R.layout.sign_in_required_pop_up, null)

        dialog.setCancelable(false)
        dialog.setContentView(signInPopup)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.create()

        // Cancel button
        signInPopup.findViewById<Button>(R.id.close_popup_button)
            .setOnClickListener {
                dialog.dismiss()
            }

        // Sign in button
        signInPopup.findViewById<Button>(R.id.sign_in_popup_button)
            .setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(this, SignInActivity::class.java))
            }

        dialog.show()
    }
}