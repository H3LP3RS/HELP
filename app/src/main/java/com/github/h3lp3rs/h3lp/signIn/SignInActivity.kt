package com.github.h3lp3rs.h3lp.signIn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.google.firebase.auth.AuthResult

class SignInActivity : AppCompatActivity() {
    lateinit var signInClient : SignInInterface<AuthResult>

    /**
     * Check if the current user is already signed in and update activity accordingly
     */
    private fun checkIfSignedIn() {
        if (signInClient.isSignedIn()) {
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    // Sign in success, update activity with the signed-in user's information
                    val intent = Intent(activity, MainPageActivity::class.java)
                    startActivity(intent)
                }
            }
    }
}