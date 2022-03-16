package com.github.h3lp3rs.h3lp.signIn

import com.google.firebase.auth.AuthResult

object SignIn {
    private var signIn: SignInInterface<AuthResult> ? = null
    fun get(): SignInInterface<AuthResult> {
        signIn = signIn ?: GoogleSignInAdaptor
        return signIn!!
    }
    fun set(newSignIn: SignInInterface<AuthResult>) {
        signIn = newSignIn
    }
}