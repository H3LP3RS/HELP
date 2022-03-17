package com.github.h3lp3rs.h3lp

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.github.h3lp3rs.h3lp.preferences.Preferences
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.Files
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.Files.*
import com.github.h3lp3rs.h3lp.preferences.Preferences.Companion.USER_AGREE
import com.github.h3lp3rs.h3lp.presentation.PresArrivalActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// TODO: We should reach a consensus on where to put these things
const val ORIGIN: String = "ORIGIN"
class SignInActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val serverClientId = "899579782202-t3orsbp6aov3i91c99r72kc854og8jad.apps.googleusercontent.com"

    override fun onStart() {
        super.onStart()

        check()
    }

    /**
     * Check if the current user is already signed in and update activity accordingly
     */
    private fun check() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        // TODO: Clear all pref to get a fresh app every time (manual testing code!)
        Preferences.clearAllPreferences(this) // TODO: Remove line for production
        // First check ToS agreement
        if(!Preferences(PRESENTATION, this).getBoolOrDefault(USER_AGREE, false)) {
            val i = Intent(this, PresArrivalActivity::class.java)
                .putExtra(ORIGIN, MainPageActivity::class.qualifiedName) // TODO: Change to SignIn later
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Initialize Firebase Auth
        auth = Firebase.auth

        makeRequest()

        findViewById<ImageButton>(R.id.signInButton).setOnClickListener{ signIn() }
    }

    /**
     * Configure Google Sign In and build a Google sign in client with the options specified
     */
    private fun makeRequest() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    /**
     * Launch the Google sign in request
     */
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    // Handle sign in request result
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
            if(result.resultCode == Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, display a message to the user.
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Authenticate account with Firebase
     *
     * @param idToken account's token Id
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update activity with the signed-in user's information
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Sorry authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}