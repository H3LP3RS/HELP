package com.github.h3lp3rs.h3lp.signIn

object Authenticator {
    private var currentAuthenticator: AuthenticatorInterface? = null
    fun get(): AuthenticatorInterface {
        currentAuthenticator = currentAuthenticator ?: FirebaseAuthAdaptor
        return currentAuthenticator!!
    }
    fun set(customAuthenticator: AuthenticatorInterface) {
        currentAuthenticator = customAuthenticator
    }
}