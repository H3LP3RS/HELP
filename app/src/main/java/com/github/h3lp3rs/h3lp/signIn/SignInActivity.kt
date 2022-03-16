package com.github.h3lp3rs.h3lp.signIn

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.github.h3lp3rs.h3lp.R
import com.google.firebase.auth.AuthResult

class SignInActivity : AppCompatActivity() {
    lateinit var signInClient : SignInInterface<AuthResult>

    override fun onStart() {
        super.onStart()
        //checkIfSignedIn()
    }

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

    private fun launchSignIn(){
        signInClient = SignIn.get()
        checkIfSignedIn()
        val signInIntent = signInClient.signIn(this)
        // Launch the sign in request
        resultLauncher.launch(signInIntent)
    }

    // Handle sign in request result
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        signInClient.authenticate(result, this)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update activity with the signed-in user's information
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                }
            }
    }
}