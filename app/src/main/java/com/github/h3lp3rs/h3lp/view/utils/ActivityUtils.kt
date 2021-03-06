package com.github.h3lp3rs.h3lp.view.utils

import android.app.Activity
import android.content.Intent
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
        goToActivity(MainPageActivity::class.java)
    }

}