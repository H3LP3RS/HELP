package com.github.h3lp3rs.h3lp.signin

import com.google.firebase.auth.AuthResult

object SignIn {
    // signIn contains the currently used sign in method
    private var signIn: SignInInterface<AuthResult> ? = null

    /**
     * Returns the current sign in method (the default sign in method is with Google, unless set
     * otherwise)
     * @return The sign in method
     */
    fun get(): SignInInterface<AuthResult> {
        signIn = signIn ?: GoogleSignInAdapter
        return signIn!!
    }

    /**
     * Used for testing purposes to give mock sign in instances, can also be used to enable
     * multiple sign in methods for the app
     */
    fun set(newSignIn: SignInInterface<AuthResult>) {
        signIn = newSignIn
    }
}