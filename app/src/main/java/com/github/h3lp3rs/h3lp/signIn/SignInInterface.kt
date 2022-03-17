package com.github.h3lp3rs.h3lp.signIn

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import com.google.android.gms.tasks.Task

interface SignInInterface<T> {
    /**
     * Configures the sign in and builds the sign in client
     * @param currentActivity The activity from which the sign in is called
     * @return An intent corresponding 
     */
    fun signIn(currentActivity: Activity): Intent

    /**
     * 
     */
    fun authenticate(result: ActivityResult, currentActivity: Activity): Task<T>?

    /**
     * 
     */
    fun signOut()

    /**
     * 
     */
    fun isSignedIn(): Boolean
}