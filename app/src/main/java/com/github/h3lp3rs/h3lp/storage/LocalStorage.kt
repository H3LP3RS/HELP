package com.github.h3lp3rs.h3lp.storage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject


/**
 * Implementation of a local storage to store data locally. Not meant for
 * direct use.
 */
class LocalStorage(private val path: String, private val context: Context, private val enableOnlineSync: Boolean) {
    private val pref = context.getSharedPreferences(path, AppCompatActivity.MODE_PRIVATE)
    private val editor = pref.edit()


    /**
     * Update online parameters if needed
     * @throws NullPointerException if the user not authenticated and online
     * sync is enabled.
     */
    fun pull( ){
        if (enableOnlineSync) {
            // need to be authenticated if online sync is enabled
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val db = databaseOf(PREFERENCES)
            db.getString(uid).thenAccept{
                parseOnlinePrefs(it)
            }
        }
    }

    private fun parseOnlinePrefs(s: String) {
        val json = JSONObject(s)
        for(k in json.keys()){
            editor.putString(k, json.get(k).toString())
        }
    }

    /**
     * Pushes the cached updates to the online storage
     */
    fun push() {
        if (enableOnlineSync) {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val db = databaseOf(PREFERENCES)

            val json = JSONObject()
            for (entry in pref.all.entries){
                json.put(entry.key.toString(), entry.value.toString())
            }

            db.setString(uid, json.toString())
        }
    }


    /**
     * Sets a boolean to the preference file given a key.
     */
    fun setBoolean(key: String, value: Boolean) {
        editor.putString(key, value.toString()).commit()
    }

    /**
     * Gets the boolean stored at a given key.
     * Or the default value if it is not present.
     */
    fun getBoolOrDefault(key: String, default: Boolean): Boolean {
        return pref.getString(key, default.toString()) == true.toString()
    }

    fun setInt(key: String, value: Int) {
        editor.putString(key, value.toString()).commit()
    }

    fun getIntOrDefault(key: String, default: Int): Int {
        return pref.getString(key, default.toString())?.toInt() ?: default
    }

    fun setString(key: String, value: String) {
        editor.putString(key, value).commit()
    }

    fun getStringOrDefault(key: String, default: String): String?{
        return pref.getString(key, default)
    }

}