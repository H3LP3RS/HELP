package com.github.h3lp3rs.h3lp.signIn

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

object GoogleSignInAdaptor: SignInInterface{
    private val serverClientId = "899579782202-t3orsbp6aov3i91c99r72kc854og8jad.apps.googleusercontent.com"

    override fun getClient(activity: Activity): SignInClientInterface {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()
        return GoogleSignInClientAdaptor(GoogleSignIn.getClient(activity, gso))
    }

    override fun getTokenInAccountFromIntent(data: Intent): Task<String> {
        }
}