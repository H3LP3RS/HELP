package com.github.h3lp3rs.h3lp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.h3lp3rs.h3lp.signin.GoogleSignInAdapter.getCreationDate
import com.github.h3lp3rs.h3lp.signin.GoogleSignInAdapter.signOut
import com.github.h3lp3rs.h3lp.signin.SignInActivity
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.getUid
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        loadSyncPref()
        findViewById<TextView>(R.id.unique_id_text).text = getUid()
        findViewById<TextView>(R.id.user_since_text).text = getCreationDate() ?: ""
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
}