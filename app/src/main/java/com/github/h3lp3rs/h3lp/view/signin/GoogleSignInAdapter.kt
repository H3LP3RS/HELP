package com.github.h3lp3rs.h3lp.view.signin

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.globalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

/**
 * This object adapts the Google sign in and Firebase authentication to our general SignInInterface.
 * It is used as the central sign in interface in the app
 */
object GoogleSignInAdapter : SignInInterface<AuthResult> {
    var auth: FirebaseAuth = Firebase.auth
    lateinit var gso: GoogleSignInOptions

    private val SERVER_CLIENT_ID =
        globalContext.resources.getString(R.string.firebase_auth_server_id)

    override fun signIn(currentActivity: Activity): Intent {
        // Configuring the Google sign in
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(SERVER_CLIENT_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(currentActivity, gso).signInIntent
    }

    override fun authenticate(
        result: ActivityResult,
        currentActivity: Activity
    ): Task<AuthResult>? {
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
            Toast.makeText(currentActivity, "Sorry authentication failed.", Toast.LENGTH_SHORT)
                .show()
        }
        return null
    }


    /**
     * Authenticate account with Firebase
     * @param idToken The account's token Id
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

    override fun getUid(): String? {
        return auth.currentUser?.uid
    }

    override fun getCreationDate(): String? {
        val timeStamp = auth.currentUser?.metadata?.creationTimestamp
        return timeStamp?.let { Date(it).toString() }
    }
}