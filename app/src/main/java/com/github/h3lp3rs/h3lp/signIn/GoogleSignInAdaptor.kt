package com.github.h3lp3rs.h3lp.signIn

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat.startActivity
import com.github.h3lp3rs.h3lp.MainPageActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object GoogleSignInAdaptor: SignInInterface<AuthResult> {
    private var auth: FirebaseAuth = Firebase.auth
    lateinit var gso: GoogleSignInOptions

    private const val SERVER_CLIENT_ID =
    "899579782202-t3orsbp6aov3i91c99r72kc854og8jad.apps.googleusercontent.com"


    /**
     * Configure Google Sign In and build a Google sign in client with the options specified
     */
    override fun signIn(currentActivity: Activity): Intent {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(SERVER_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(currentActivity, gso).signInIntent
    }

    override fun authenticate(result: ActivityResult, currentActivity: Activity): Task<AuthResult>? {
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                return firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Firebase authentication failed, display a message to the user
                Toast.makeText(currentActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            // Google Sign In failed, display a message to the user
            Toast.makeText(currentActivity, "Sorry authentication failed.", Toast.LENGTH_SHORT).show()
        }
        return null
    }


    /**
     * Authenticate account with Firebase
     *
     * @param idToken account's token Id
     */
    private fun firebaseAuthWithGoogle(idToken: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential)
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }
}