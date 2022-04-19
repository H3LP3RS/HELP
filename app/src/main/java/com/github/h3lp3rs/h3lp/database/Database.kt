package com.github.h3lp3rs.h3lp.database

import com.google.gson.Gson
import java.util.concurrent.CompletableFuture

/**
 * Abstraction of a NoSQL external database
 */
interface Database {

    /**
     * Gets a boolean from the database
     * @param key The key in the database
     * @return Future of boolean
     */
    fun getBoolean(key: String): CompletableFuture<Boolean>

    /**
     * Sets a boolean to the database
     * @param key The key in the database
     * @param value The value of the boolean
     */
    fun setBoolean(key: String, value: Boolean)

    /**
     * Gets a string from the database
     * @param key The key in the database
     * @return Future of string
     */
    fun getString(key: String): CompletableFuture<String>

    /**
     * Sets a string to the database
     * @param key The key in the database
     * @param value The value of the string
     */
    fun setString(key: String, value: String)

    /**
     * Gets a double from the database
     * @param key The key in the database
     * @return Future of double
     */
    fun getDouble(key: String): CompletableFuture<Double>

    /**
     * Sets a double to the database
     * @param key The key in the database
     * @param value The value of the double
     */
    fun setDouble(key: String, value: Double)

    /**
     * Gets an int from the database
     * @param key The key in the database
     * @return Future of int
     */
    fun getInt(key: String): CompletableFuture<Int>

    /**
     * Sets an int to the database
     * @param key The key in the database
     * @param value The value of the int
     */
    fun setInt(key: String, value: Int)

    /**
     * Gets an object from the database, considering Json format as value
     * @param key The key in the database
     * @param type The type of the resulting object
     * @return Future of the object
     */
    fun <T> getObject(key: String, type: Class <T>): CompletableFuture<T> {
        val gson = Gson()
        return getString(key).thenApply { s -> gson.fromJson(s, type) }
    }

    /**
     * Sets an object to the database, saves the value as Json
     * @param key The key in the database
     * @param type The type of the resulting object
     * @param value The value of the object
     */
    fun <T> setObject(key: String, type: Class <T>, value: T) {
        val gson = Gson()
        setString(key, gson.toJson(value, type))
    }

    /**
     * Applies an arbitrary action when the value associated to the key changes
     * WARNING: This function automatically triggers at first when linked with a valid key
     * @param key The key in the database
     * @param action The action taken at change
     */
    fun <T> addListener(key: String, type: Class <T>, action: (T) -> Unit)

    /**
     * Clears all listeners related to a given key
     * @param key The key in the database
     */
    fun clearListeners(key: String)

    /**
     * Deletes an entry of a given key from the database
     * @param key The key in the database
     */
    fun delete(key: String)

    /**
     * Atomically increments an integer value of the database and calls the callback with the new
     * value
     * @param key The key in the database
     * @param increment The number to increment by
     * @param onComplete The callback to be called with the new value (the new value can be null
     * in case of a database error, thus why onComplete takes a nullable String)
     */
    fun incrementAndGet(key: String, increment: Int, onComplete: (String?) -> Unit)
}