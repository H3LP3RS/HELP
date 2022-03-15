package com.github.h3lp3rs.h3lp.signIn

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface AuthenticatorInterface {
    fun isSignedIn(): Boolean

    fun signInWithCredential(credential: AuthCredential): Task<AuthResult>
}