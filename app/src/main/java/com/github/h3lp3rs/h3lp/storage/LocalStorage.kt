package com.github.h3lp3rs.h3lp.storage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import org.json.JSONObject
import java.lang.Boolean.parseBoolean

/**
 * Implementation of a local storage to store data locally. Not meant for
 * direct use.
 */
class LocalStorage(path: String, context: Context, private val enableOnlineSync: Boolean) {
    private val pref = context.getSharedPreferences(path, AppCompatActivity.MODE_PRIVATE)
    private val editor = pref.edit()

    /**
     * Update online parameters if needed
     * @throws NullPointerException if the user is not authenticated AND online sync is enabled.
     */
    fun pull(){
        /*if (enableOnlineSync) { // TODO: Need mocked version!
            // Need to be authenticated if online sync is enabled
            val uid = FirebaseAuth.getInstance().currentUser!!.uid // TODO: Need mocked version!
            val db = databaseOf(PREFERENCES)
            db.getString(uid).exceptionally { JSONObject().toString() }.thenAccept {
                parseOnlinePrefs(it)
            }
        }*/
    }

    /**
     * Auxiliary function to parse the map stored in JSON on the remote DB
     */
    private fun parseOnlinePrefs(s: String) {
        val json = JSONObject(s)
        for(k in json.keys()) {
            editor.putString(k, json.get(k).toString())
        }
    }

    /**
     * Pushes the cached updates to the online storage in a JSON format.
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
     * Sets a Boolean to the preference file given a key.
     */
    fun setBoolean(key: String, value: Boolean) {
        editor.putString(key, value.toString()).commit()
    }

    /**
     * Gets the Boolean stored at a given key.
     * Or the default value if it is not present.
     */
    fun getBoolOrDefault(key: String, default: Boolean): Boolean {
        return parseBoolean(pref.getString(key, default.toString()))
    }

    /**
     * Sets an Int to the preference file given a key.
     */
    fun setInt(key: String, value: Int) {
        editor.putString(key, value.toString()).commit()
    }

    /**
     * Gets the Int stored at a given key.
     * Or the default value if it is not present.
     */
    fun getIntOrDefault(key: String, default: Int): Int {
        return pref.getString(key, default.toString())?.toInt() ?: default
    }

    /**
     * Sets a String to the preference file given a key.
     */
    fun setString(key: String, value: String) {
        editor.putString(key, value).commit()
    }

    /**
     * Gets the String stored at a given key.
     * Or the default value if it is not present.
     */
    fun getStringOrDefault(key: String, default: String): String?{
        return pref.getString(key, default)
    }

    /**
     * Sets an Object to the preference file given a key.
     */
    fun <T> setObject(key: String, type: Class <T>, value: T) {
        val gson = Gson()
        setString(key, gson.toJson(value, type))
    }

    /**
     * Gets the Object stored at a given key.
     * Or the default value if it is not present.
     */
    fun <T> getObjectOrDefault(key: String, type: Class<T>, default: T): T {
        val gson = Gson()
        val s = getStringOrDefault(key, "")
        return if (!s.isNullOrEmpty()) {
            gson.fromJson(s, type)
        } else {
            default
        }
    }

    /**
     * Clears all key value mappings
     */
    fun clearAll() {
        editor.clear().commit()
    }
}