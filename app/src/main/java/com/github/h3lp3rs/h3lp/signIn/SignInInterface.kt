package com.github.h3lp3rs.h3lp.signIn

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import com.google.android.gms.tasks.Task

interface SignInInterface<T> {
    /**
     * Configures the sign in and builds the sign in client
     * @param currentActivity The activity from which the sign in is called to display the sign
     *      in client
     * @return An intent to launch the sign in client
     */
    fun signIn(currentActivity: Activity): Intent

    /**
     * Authenticates the user with the sign in client
     * @param result The result
     * @param currentActivity The current activity to show possible error messages to the user
     * @return A task which finishes the authentication and returns
     *      information about the authentication succeeding or failing
     */
    fun authenticate(result: ActivityResult, currentActivity: Activity): Task<T>?

    /**
     * Signs the current user out
     */
    fun signOut()

    /**
     * Returns a boolean saying if a user is currently signed in or not
     */
    fun isSignedIn(): Boolean
}