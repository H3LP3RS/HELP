package com.github.h3lp3rs.h3lp.signin

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * This object adapts the Google sign in and Firebase authentication to our general SignInInterface.
 * It is used as the central sign in interface in the app
 */
object GoogleSignInAdapter: SignInInterface<AuthResult> {
    private var auth: FirebaseAuth = Firebase.auth
    lateinit var gso: GoogleSignInOptions

    private const val SERVER_CLIENT_ID =
    "899579782202-t3orsbp6aov3i91c99r72kc854og8jad.apps.googleusercontent.com"

    /**
     * Configures the Google sign in and builds the Google sign in client with the options specified
     * @param currentActivity The activity from which the sign in is called to display the sign
     *      in client
     * @return An intent to launch the sign in client
     */
    override fun signIn(currentActivity: Activity): Intent {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(SERVER_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(currentActivity, gso).signInIntent
    }

    /**
     * Authenticates the user with the sign in client
     * @param result The result
     * @param currentActivity The current activity to show possible error messages to the user
     * @return A task which finishes the authentication and returns
     *      information about the authentication succeeding or failing
     */
    override fun authenticate(result: ActivityResult, currentActivity: Activity): Task<AuthResult>? {
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                // The task contains the google account (on success)
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                return firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Firebase authentication failed, display the specific error message to the user
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
     * @param idToken The account's token Id
     */
    private fun firebaseAuthWithGoogle(idToken: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential)
    }

    /**
     * Signs the current user out
     */
    override fun signOut() {
        auth.signOut()
    }

    /**
     * Returns a boolean saying if a user is currently signed in or not
     */
    override fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun getUid(): String? {
        return auth.currentUser?.uid
    }
}