package com.github.h3lp3rs.h3lp.model.signin

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

/**
 * This object adapts Firebase anonymous authentication to our general SignInInterface.
 */
object AnonymousSignInAdapter : SignInInterface<AuthResult> {
    var auth : FirebaseAuth = Firebase.auth

    override fun signIn(currentActivity : Activity) : Intent? {
        return null
    }

    override fun authenticate( result : ActivityResult ?, currentActivity : Activity) : Task<AuthResult>? {
            try {
                // Authenticate account with Firebase anonymously
                return auth.signInAnonymously()

            } catch (e : ApiException) {
                // Firebase authentication failed, display the specific error message to the user
                Toast.makeText(currentActivity, e.message, Toast.LENGTH_SHORT).show()

            }
        return null
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun isSignedIn() : Boolean {
        return auth.currentUser != null
    }

    override fun getUid() : String? {
        return auth.currentUser?.uid
    }

    override fun getCreationDate() : String? {
        val timeStamp = auth.currentUser?.metadata?.creationTimestamp
        return timeStamp?.let { Date(it).toString() }
    }
}