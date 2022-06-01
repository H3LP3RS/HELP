package com.github.h3lp3rs.h3lp.model.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.github.h3lp3rs.h3lp.view.mainpage.MainPageActivity

// Object that defines utility functions that are useful in many activities to avoid code
// duplication
object ActivityUtils {

    /**
     * Starts an activity by sending intent
     * @param activity The activity to launch
     */
    fun Activity.goToActivity(activity: Class<*>?) {
        val intent = Intent(this.applicationContext, activity)
        startActivity(intent)
    }

    /**
     * Goes back to the main page
     */
    fun Activity.goToMainPage() {
        val intent = Intent(this.applicationContext, MainPageActivity::class.java)
        startActivity(intent)
    }

}