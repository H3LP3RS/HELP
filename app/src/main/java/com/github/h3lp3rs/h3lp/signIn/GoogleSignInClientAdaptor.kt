package com.github.h3lp3rs.h3lp.signIn

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class GoogleSignInClientAdaptor(client: GoogleSignInClient) : SignInClientInterface{
    private var googleSignInClient: GoogleSignInClient = client

    override fun signInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
}