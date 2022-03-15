package com.github.h3lp3rs.h3lp.signIn

import android.app.Activity
import android.content.Intent
import com.google.android.gms.tasks.Task

interface SignInInterface {
    fun getClient(activity: Activity): SignInClientInterface
    fun getTokenInAccountFromIntent(data: Intent): Task<String>
}