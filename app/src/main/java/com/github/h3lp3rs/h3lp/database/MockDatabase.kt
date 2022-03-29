package com.github.h3lp3rs.h3lp.database

import android.annotation.SuppressLint
import com.google.firebase.database.core.utilities.encoding.CustomClassMapper
import java.lang.NullPointerException
import java.util.concurrent.CompletableFuture

/**
 * Implementation of a NoSQL mocked external database
 */
class MockDatabase : Database {

    private val db = HashMap<String, Any>()
    private val listeners = HashMap<String, List<() -> Unit>>()

    /**
     * Utility function to extract values into futures from generic types
     * @param key The key in the database
     */
    private inline fun <reified  T: Any> get(key: String): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        if(db.containsKey(key)) future.complete(db[key] as T)
        else future.completeExceptionally(NullPointerException("Key: $key not in the database"))
        return future
    }

    /**
     * Utility function checking whether the key is associated with a value.
     * Throws a custom NullPointerException if the key is not present.
     * @param map The map to perform the check on
     * @param key The key in the database
     */
    private fun checkHasKey(map: Map<String,Any>, key: String) {
        if(!map.containsKey(key)) throw NullPointerException("Key: $key not in the database")
    }

    /**
     * Utility function to set a value and trigger all listeners related to a key if the value
     * has changed
     * @param key The key in the database
     * @param value The value to set
     */
    private fun setAndTriggerListeners(key: String, value: Any) {
        val old = db.getOrDefault(key, value)
        db[key] = value
        if(old != value) {
            for (l in listeners.getOrDefault(key, emptyList())) l()
        }
    }

    /**
     * Gets a boolean from the database
     * @param key The key in the database
     * @return Future of boolean
     */
    override fun getBoolean(key: String): CompletableFuture<Boolean> {
        return get(key)
    }

    /**
     * Sets a boolean to the database
     * @param key The key in the database
     * @param value The value of the boolean
     */
    override fun setBoolean(key: String, value: Boolean) {
        setAndTriggerListeners(key, value)
    }

    /**
     * Gets a string from the database
     * @param key The key in the database
     * @return Future of string
     */
    override fun getString(key: String): CompletableFuture<String> {
        return get(key)
    }

    /**
     * Sets a string to the database
     * @param key The key in the database
     * @param value The value of the string
     */
    override fun setString(key: String, value: String) {
        setAndTriggerListeners(key, value)
    }

    /**
     * Gets a double from the database
     * @param key The key in the database
     * @return Future of double
     */
    override fun getDouble(key: String): CompletableFuture<Double> {
        return get(key)
    }

    /**
     * Sets a double to the database
     * @param key The key in the database
     * @param value The value of the double
     */
    override fun setDouble(key: String, value: Double) {
        setAndTriggerListeners(key, value)
    }

    /**
     * Gets an int from the database
     * @param key The key in the database
     * @return Future of int
     */
    override fun getInt(key: String): CompletableFuture<Int> {
        return get(key)
    }

    /**
     * Sets an int to the database
     * @param key The key in the database
     * @param value The value of the int
     */
    override fun setInt(key: String, value: Int) {
        setAndTriggerListeners(key, value)
    }

    /**
     * Applies an arbitrary action when the value associated to the key changes
     * WARNING: This function automatically triggers at first when linked with a valid key
     * @param key The key in the database
     * @param action The action taken at change
     */
    @SuppressLint("RestrictedApi")
    override fun <T> addListener(key: String, type: Class<T>, action: (T) -> Unit) {
        checkHasKey(db, key)
        val wrappedAction: () -> Unit = {
            val v: T = CustomClassMapper.convertToCustomClass(db[key], type)
            action(v)
        }
        // Enrich the list & add to map
        val ls = listeners.getOrDefault(key, emptyList()) + listOf(wrappedAction)
        listeners[key] = ls
        // First time trigger
        wrappedAction()
    }

    /**
     * Clears all listeners related to a given key
     * @param key The key in the database
     */
    override fun clearListeners(key: String) {
        checkHasKey(listeners, key)
        listeners.remove(key)
    }

    /**
     * Deletes an entry of a given key from the database
     * @param key They key in the database
     */
    override fun delete(key: String) {
        db.remove(key)
        listeners.remove(key)
    }
}