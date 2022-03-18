package com.github.h3lp3rs.h3lp.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

/**
 * Convenience class to store the global app preferences
 */
class Preferences(private val file: Files, context: Context) {

    private val pref = context.getSharedPreferences(file.name, AppCompatActivity.MODE_PRIVATE)
    private val editor = pref.edit()

    /**
     * Sets a boolean to the preference file given a key.
     * Throws exception if key not valid.
     */
    fun setBool(key: String, value: Boolean) {
        // Cannot pollute the preference file
        require(file.keys.contains(key))
        editor.putBoolean(key, value)
        editor.commit()
    }

    /**
     * Gets the boolean stored at a given key.
     * Or the default value if it is not present.
     */
    fun getBoolOrDefault(key: String, default: Boolean): Boolean {
        return pref.getBoolean(key, default)
    }

    companion object {
        const val USER_AGREE = "UserAgree"
        enum class Files(val keys: List<String>) {
            PRESENTATION(listOf(USER_AGREE))
        }

        fun clearAllPreferences(context: Context) {
            for(file in Files.values()) {
                Preferences(file, context).editor.clear().commit()
            }
        }
    }
}