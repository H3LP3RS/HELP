package com.github.h3lp3rs.h3lp.signin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth.getInstance

class SignInActivity : AppCompatActivity() {
    lateinit var signInClient : SignInInterface<AuthResult>
    private lateinit var userCookie: LocalStorage
    private lateinit var userSignin: LocalStorage

    private fun checkToSAndLaunchIfNotAcceptedElseMain() {
        // Check ToS agreement
        userCookie = storageOf(Storages.USER_COOKIE) // Fetch from storage
        if(!userCookie.getBoolOrDefault(getString(R.string.KEY_USER_AGREE), false)) {
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
    private fun checkIfSignedIn() {
        if (signInClient.isSignedIn()) {
            userUid = signInClient.getUid()
            username = getInstance().currentUser?.displayName?.substringBefore(" ")
            checkToSAndLaunchIfNotAcceptedElseMain()
        }
    }

    private fun offlineCheckIfSignedIn(){
        userSignin = storageOf(Storages.SIGN_IN) // Fetch from storage
        if(userSignin.getBoolOrDefault(getString(R.string.KEY_USER_SIGNED_IN), false)){
            userUid = userSignin.getStringOrDefault(getString(R.string.KEY_USER_UID),"")
            username = userSignin.getStringOrDefault(getString(R.string.KEY_USER_NAME),"")
            checkToSAndLaunchIfNotAcceptedElseMain()
        }
    }

    private fun saveAuthentication(){
        userUid = signInClient.getUid()
        username = getInstance().currentUser?.displayName?.substringBefore(" ")
        userSignin.setBoolean(getString(R.string.KEY_USER_SIGNED_IN), true)
        userUid?.let { userSignin.setString(getString(R.string.KEY_USER_UID), it) }
        username?.let { userSignin.setString(getString(R.string.KEY_USER_NAME), it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Store the context for local storage use
        globalContext = this

        setContentView(R.layout.activity_sign_in)
        // Initialize Firebase Auth
        findViewById<ImageButton>(R.id.signInButton).setOnClickListener{
            launchSignIn()
        }

        Storages.SIGN_IN.setOnlineSync(false)
        offlineCheckIfSignedIn()
    }

    /**
     * Initialize client and launch the sign in request
     */
    private fun launchSignIn(){
        signInClient = SignIn.get()
        //checkIfSignedIn()
        val signInIntent = signInClient.signIn(this)
        resultLauncher.launch(signInIntent)
    }

    // Handle sign in request result
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result -> authenticateUser(result,this) }

    /**
     * Authenticate user
     *
     * @param result sign in intent result containing the user account
     * @param activity current activity
     */
    fun authenticateUser(result: ActivityResult, activity: Activity){
        signInClient.authenticate(result, activity)
            ?.addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    userUid = signInClient.getUid()
                    // Only get the first name for privacy reasons
                    username = getInstance().currentUser?.displayName?.substringBefore(" ")
                    saveAuthentication()
                    checkToSAndLaunchIfNotAcceptedElseMain()
                }
            }
    }

    /**
     * Companion used to pass on a global context used to open the local
     * storages
     */
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var globalContext: Context
        var userUid: String? = null
        private var username : String? = null

        /**
         * Getter on the global context
         */
        fun getGlobalCtx(): Context {
            return globalContext
        }

        /**
         * Getter on the userUid
         */
        fun getUid(): String? {
            return userUid
        }
        /**
         * Getter on the user's name
         */
        fun getName(): String? {
            return username
        }

        /**
         * Setter on the user's name
         */
        fun setName(newUsername: String) {
            username = newUsername
        }
    }
}