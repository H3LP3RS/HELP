package com.github.h3lp3rs.h3lp.signin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.github.h3lp3rs.h3lp.storage.Storages
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import com.google.firebase.auth.AuthResult

const val ORIGIN: String = "ORIGIN"

class SignInActivity : AppCompatActivity() {
    lateinit var signInClient : SignInInterface<AuthResult>
    private lateinit var userCookie: LocalStorage

    private fun checkToSAndLaunchIfNotAccepted() {
        // Check ToS agreement
        userCookie = storageOf(Storages.USER_COOKIE) // Fetch from storage
        if(!userCookie.getBoolOrDefault(getString(R.string.KEY_USER_AGREE), false)) {
            val i = Intent(this, PresArrivalActivity::class.java)
                .putExtra(ORIGIN, SignInActivity::class.qualifiedName)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    /**
     * Check if the current user is already signed in and update activity accordingly
     */
    private fun checkIfSignedIn() {
        if (signInClient.isSignedIn()) {
            checkToSAndLaunchIfNotAccepted()
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
        }
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
    }

    /**
     * Initialize client and launch the sign in request
     */
    private fun launchSignIn(){
        signInClient = SignIn.get()
        checkIfSignedIn()
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
                    checkToSAndLaunchIfNotAccepted()
                    // Sign in success, update activity with the signed-in user's information
                    val intent = Intent(activity, MainPageActivity::class.java)
                    startActivity(intent)
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

        /**
         * Getter on the global context
         */
        fun getGlobalCtx(): Context {
            return globalContext
        }
    }
}