package com.github.h3lp3rs.h3lp.signIn

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object FirebaseAuthAdaptor : AuthenticatorInterface {
    private var auth: FirebaseAuth = Firebase.auth
    override fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun signInWithCredential(credential: AuthCredential): Task<AuthResult> {
        return auth.signInWithCredential(credential)
    }
}