package com.github.h3lp3rs.h3lp.signin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.disableOnlineSync
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import kotlinx.android.synthetic.main.activity_sign_in.*

const val MAX_LENGTH_USERNAME = 13
const val ERROR_MESSAGE_ON_LONG_USERNAME = "Invalid username: your username is too long."
const val MIN_LENGTH_USERNAME = 3
const val ERROR_MESSAGE_ON_SHORT_USERNAME = "Invalid username: your username is too short."

class SignInActivity : AppCompatActivity() {
    lateinit var signInClient : SignInInterface<AuthResult>
    private lateinit var userCookie : LocalStorage
    private lateinit var userSignIn : LocalStorage
    private lateinit var USER_SIGNED_IN : String
    private lateinit var USER_UID : String
    private lateinit var USER_NAME : String


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        // Store the context for local storage use
        globalContext = this

        setContentView(R.layout.activity_sign_in)
        // Initialize Firebase Auth
        findViewById<ImageButton>(R.id.signInButton).setOnClickListener {
            launchSignIn()
        }

        // Continue without sign in
        findViewById<TextView>(R.id.noSignInText).setOnClickListener {
            if (checkUsernameField()) {
                username = text_field_username.text.toString()
                disableOnlineSync()
                checkToSAndLaunchIfNotAcceptedElseMain()
            } else {
                displayMessage(
                    findViewById<View>(android.R.id.content).rootView,
                    getString(R.string.username_error_field_msg)
                )
            }
        }

        // Sign in local storage doesn't need online sync
        USER_SIGNED_IN = getString(R.string.KEY_USER_SIGNED_IN)
        USER_UID = getString(R.string.KEY_USER_UID)
        USER_NAME = getString(R.string.KEY_USER_NAME)
        // Check if the user is already signed in
        offlineCheckIfSignedIn()

        createUsernameField()
    }

    /**
     * Checks the Terms of Service to see if they were already accepted, if they were, launches the
     * app by going to the main page, if not, goes back to the activity to accept the ToS
     */
    private fun checkToSAndLaunchIfNotAcceptedElseMain() {
        // Check ToS agreement
        userCookie = storageOf(USER_COOKIE) // Fetch from storage
        if (!userCookie.getBoolOrDefault(getString(R.string.KEY_USER_AGREE), false)) {
            val intent = Intent(this, PresArrivalActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Check if the current user is already signed in and update activity accordingly
     */
    private fun offlineCheckIfSignedIn() {
        userSignIn = storageOf(SIGN_IN) // Fetch from storage
        if (userSignIn.getBoolOrDefault(USER_SIGNED_IN, false)) {
            userUid = userSignIn.getStringOrDefault(USER_UID, "")
            username = userSignIn.getStringOrDefault(USER_NAME, "")
            checkToSAndLaunchIfNotAcceptedElseMain()
        }
    }

    /**
     * Save the user authentication information to the local storage
     */
    private fun saveAuthentication() {
        userSignIn.setBoolean(USER_SIGNED_IN, true)
        userUid?.let { userSignIn.setString(USER_UID, it) }
        username?.let { userSignIn.setString(USER_NAME, it) }
    }


    /**
     * Initialize client and launch the sign in request
     */
    private fun launchSignIn() {
        if (checkUsernameField()) {
            signInClient = SignIn.get()
            val signInIntent = signInClient.signIn(this)
            resultLauncher.launch(signInIntent)
        } else {
            displayMessage(
                findViewById<View>(android.R.id.content).rootView,
                getString(R.string.username_error_field_msg)
            )
        }
    }

    // Handle sign in request result
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            authenticateUser(
                result, this
            )
        }

    /**
     * Authenticate user
     *
     * @param result sign in intent result containing the user account
     * @param activity current activity
     */
    fun authenticateUser(result : ActivityResult, activity : Activity) {
        signInClient.authenticate(result, activity)?.addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {

                userUid = signInClient.getUid()
                username = text_field_username.text.toString()

                saveAuthentication()

                // Enable online sync for meaningful storages:
                SKILLS.setOnlineSync(true)
                MEDICAL_INFO.setOnlineSync(true)
                USER_COOKIE.setOnlineSync(true)

                checkToSAndLaunchIfNotAcceptedElseMain()
            } else {
                Toast.makeText(
                    baseContext,
                    "Anonymous authentication failed: " + task.exception,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Creates a field that tests the username input and writes an error back to it
     */
    private fun createUsernameField() {
        text_field_username.doOnTextChanged { text, _, _, _ ->
            when {
                text!!.isEmpty() -> text_layout_username.error = getString(R.string.empty_error_msg)
                text.length > MAX_LENGTH_USERNAME -> text_layout_username.error = ERROR_MESSAGE_ON_LONG_USERNAME
                text.length < MIN_LENGTH_USERNAME -> text_layout_username.error = ERROR_MESSAGE_ON_SHORT_USERNAME
                else -> text_layout_username.error = null
            }
        }
    }

    /**
     * Display a message using a snackbar
     * @param it The view in which the snack should appear
     * @param str The message to display
     */
    private fun displayMessage(it : View, str : String) {
        val snack = Snackbar.make(it, str, Snackbar.LENGTH_LONG)
        snack.setBackgroundTint(ContextCompat.getColor(this, R.color.persimmon))
        snack.show()
    }

    /**
     * Check the validity of the username field
     * @return True if the username is valid and non-empty, otherwise false
     */
    private fun checkUsernameField() : Boolean {
        return text_layout_username.error == null && !text_field_username.text.isNullOrBlank()
    }

    /**
     * Companion used to pass on a global context used to open the local
     * storages
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var globalContext : Context
        var userUid : String? = null
        private var username : String? = null

        const val GUEST_USER = "Guest"

        /**
         * Getter on the global context
         */
        fun getGlobalCtx() : Context {
            return globalContext
        }

        /**
         * Getter on the userUid
         */
        fun getUid() : String? {
            return userUid
        }

        /**
         * Getter on the user's name
         */
        fun getName() : String? {
            return username
        }

        /**
         * Setter on the user's name
         */
        fun setName(newUsername : String) {
            username = newUsername
        }
    }
}