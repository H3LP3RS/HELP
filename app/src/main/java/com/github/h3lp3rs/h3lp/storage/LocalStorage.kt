package com.github.h3lp3rs.h3lp.storage

import android.content.Context
import android.security.keystore.UserNotAuthenticatedException
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.PREFERENCES
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.getUid
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.SyncPref
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.lang.Boolean.parseBoolean

/**
 * Implementation of a local storage to store data locally. Not meant for
 * direct use.
 * @param path The path to the local storage (there are several, as defined in Storages)
 * @param context The context with which the local storage is accessed (to be able to access the
 * shared preferences)
 */
class LocalStorage(private val path: String, val context: Context) {
    private val pref = context.getSharedPreferences(path, MODE_PRIVATE)
    private val editor = pref.edit()

    /**
     * Checks whether a storage is meant to be synced online or not. It's
     * re-evaluated everytime because the behaviour can change at run time
     * depending on the user's desire.
     * @return true if the storage should be synced online and false otherwise.
     * Default value is true
     */
    private fun isOnlineSyncEnabled(): Boolean {
        return parseBoolean(
            context.getSharedPreferences(SyncPref, MODE_PRIVATE).getString(path, "false")
        )
    }

    /**
     * Update online parameters if needed
     * @throws UserNotAuthenticatedException if the user is not authenticated AND online sync is enabled.
     */
    fun pull() {
        if (isOnlineSyncEnabled()) {
            // Need to be authenticated if online sync is enabled
            val uid = getUid()
            if (uid != null) {
                val db = databaseOf(PREFERENCES)
                db.getString("$path/$uid").exceptionally { JSONObject().toString() }
                    .thenAccept {
                        parseOnlinePrefs(it)
                    }
            } else {
                throw UserNotAuthenticatedException()
            }
        }
    }

    /**
     * Delete online data synchronized with the preferences
     */
    fun clearOnlineSync() {
        val uid = getUid()!!
        val db = databaseOf(PREFERENCES)
        db.delete("$path/$uid")
    }

    /**
     * Auxiliary function to asynchronously parse the map stored in JSON on the remote DB
     * @param s String corresponding to the preferences map stored on the remote DB
     */
    private fun parseOnlinePrefs(s: String) {
        runBlocking {
            // Run the parsing asynchronously since it requires slow I/O operations to write the
            // preferences
            launch {
                val json = JSONObject(s)
                for (k in json.keys()) {
                    setString(k, json.get(k).toString())
                }
            }
        }
    }

    /**
     * Asynchronously pushes the cached updates to the online storage in a JSON format.
     */
    fun push() {
        if (isOnlineSyncEnabled()) {
            runBlocking {
                // Run the push asynchronously since it requires slow I/O operations to read the
                // preferences
                launch {
                    if (isOnlineSyncEnabled()) {
                        val uid = getUid()!!
                        val db = databaseOf(PREFERENCES)

                        val json = JSONObject()
                        for (entry in pref.all.entries) {
                            json.put(entry.key.toString(), entry.value.toString())
                        }
                        db.setString("$path/$uid", json.toString())
                    }
                }
            }
        }
    }

    /**
     * Asynchronously sets a Boolean to the preference file given a key.
     * @param key The key to store the boolean value to in the preference file
     * @param value The boolean value to store
     */
    fun setBoolean(key: String, value: Boolean) {
        runBlocking {
            // Run the setting asynchronously since it requires slow I/O operations
            launch {
                editor.putString(key, value.toString()).commit()
            }
        }
    }

    /**
     * Gets the Boolean stored at a given key.
     * Or the default value if it is not present.
     * @param key The key to get the boolean value from in the preference file
     * @param default The default boolean value to return in case the key didn't map to a boolean (or to
     * anything)
     */
    fun getBoolOrDefault(key: String, default: Boolean): Boolean {
        return parseBoolean(pref.getString(key, default.toString()))
    }

    /**
     * Asynchronously sets an Int to the preference file given a key.
     * @param key The key to store the int value to in the preference file
     * @param value The int value to store
     */
    fun setInt(key: String, value: Int) {
        runBlocking {
            // Run the setting asynchronously since it requires slow I/O operations
            launch {
                editor.putString(key, value.toString()).commit()
            }
        }
    }

    /**
     * Gets the Int stored at a given key.
     * Or the default value if it is not present.
     * @param key The key to get the int value from in the preference file
     * @param default The default int value to return in case the key didn't map to a boolean (or to
     * anything)
     */
    fun getIntOrDefault(key: String, default: Int): Int {
        return pref.getString(key, default.toString())?.toInt() ?: default
    }

    /**
     * Asynchronously sets a String to the preference file given a key.
     * @param key The key to store the string value to in the preference file
     * @param value The string value to store
     */
    fun setString(key: String, value: String) {
        runBlocking {
            // Run the setting asynchronously since it requires slow I/O operations
            launch {
                editor.putString(key, value).commit()
            }
        }
    }

    /**
     * Gets the String stored at a given key.
     * Or the default value if it is not present.
     * @param key The key to get the string value from in the preference file
     * @param default The default string value to return in case the key didn't map to a boolean (or to
     * anything)
     */
    fun getStringOrDefault(key: String, default: String): String? {
        return pref.getString(key, default)
    }

    /**
     * Asynchronously sets an Object to the preference file given a key.
     * @param key The key to store the object value to in the preference file
     * @param type The object's type
     * @param value The object value to store
     */
    fun <T> setObject(key: String, type: Class<T>, value: T) {
        runBlocking {
            // Run the setting asynchronously since it requires slow I/O operations
            launch {
                val gson = Gson()
                setString(key, gson.toJson(value, type))
            }
        }
    }

    /**
     * Gets the Object stored at a given key.
     * Or the default value if it is not present.
     * @param key The key to get the object value from in the preference file
     * @param type The object's type
     * @param default The default object value to return in case the key didn't map to a boolean (or
     * to anything)
     */
    fun <T> getObjectOrDefault(key: String, type: Class<T>, default: T?): T? {
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