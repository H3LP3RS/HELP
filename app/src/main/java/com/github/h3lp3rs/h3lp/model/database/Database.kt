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
    fun <T> getObject(key: String, type: Class<T>): CompletableFuture<T> {
        val gson = Gson()
        return getString(key).thenApply { s -> gson.fromJson(s, type) }
    }

    /**
     * Sets an object to the database, saves the value as Json
     * @param key The key in the database
     * @param type The type of the resulting object
     * @param value The value of the object
     */
    fun <T> setObject(key: String, type: Class<T>, value: T) {
        val gson = Gson()
        setString(key, gson.toJson(value, type))
    }

    /**
     * Adds a new object to the end of an ordered list of objects paired to a key in the database,
     * saves its value as Json
     * Can be called concurrently without creating overwriting problems
     * Isn't the same as calling setObject with a list or even a concurrent list since this could
     * easily lead to concurrency problems
     * @param key The key in the database
     * @param type The type of the resulting object
     * @param value The value of the object
     * @return The key of that new object in the database (since to make this adding concurrent, the
     * database chooses what unique key value to return), is null if an error occurred while adding
     * the object to the list
     */
    fun <T> addToObjectsListConcurrently(key: String, type: Class<T>, value: T): String? {
        val gson = Gson()
        return addStringConcurrently(key, gson.toJson(value, type))
    }

    /**
     * Adds a value to the end of an ordered list of values paired to the key in the database
     * Can be called concurrently without creating overwriting problems (all values are eventually
     * added to the list)
     * @param key The key in the database
     * @param value The value of the string
     * @return The key of that new string in the database (since to make this adding concurrent, the
     * database chooses what unique key value to return), is null if an error occurred while adding
     * the string to the list
     */
    fun addStringConcurrently(key: String, value: String): String?

    /**
     * Gets the list of objects added with addToObjectsListConcurrently
     * @param key The key in the database
     * @param type The type of the objects in the list
     * @return Future of the list of objects
     */
    fun <T> getObjectsList(key: String, type: Class<T>): CompletableFuture<List<T>>

    /**
     * Applies an arbitrary action when the value associated to the key changes
     * WARNING: This function automatically triggers at first when linked with a valid key
     * @param key The key in the database
     * @param type The type of the objects in the list
     * @param action The action taken at change
     */
    fun <T> addListener(key: String, type: Class<T>, action: (T) -> Unit)

    /**
     * Applies an arbitrary action when the value associated to the key changes
     * WARNING: This function automatically triggers at first when linked with a valid key
     * Only succeeds when no existing listener is already linked to the key
     * @param key The key in the database
     * @param type The type of the objects in the list
     * @param action The action taken at change
     */
    fun <T> addListenerIfNotPresent(key: String, type: Class<T>, action: (T) -> Unit)

    /**
     * Applies an arbitrary action when the list of values associated to the key changes
     * Is necessary to distinguish between associating a list of values to a key or associating
     * a key to several unique ids (generated by the database) to be able to add values to the list
     * concurrently in addToObjectsList
     * WARNING: This function automatically triggers at first when linked with a valid key
     * @param key The key in the database
     * @param type The type of the objects in the list
     * @param action The action taken at change on the list of values
     */
    fun <T> addListListener(key: String, type: Class<T>, action: (List<T>) -> Unit)

    /**
     * Clears all listeners related to a given key
     * @param key The key in the database
     */
    fun clearListeners(key: String)

    /**
     * Clears all listeners related for this database
     */
    fun clearAllListeners()

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
     * @return A future of the incremented value, completes exceptionally if there was a problem
     * while accessing the database
     */
    fun incrementAndGet(key: String, increment: Int): CompletableFuture<Int>


    /**
     * Adds an event listener to a key in the database
     * @param key The relative path from this reference to the new one or null if the action is to
     * be taken on the entire original path
     * @param type The type of the added object
     * @param onChildAdded The action to take on the new data snapshot when data is added. Could be
     * null
     * @param onChildRemoved The action to take on the new data snapshot when data is removed
     * @param key The relative path from this reference to the new one or null if the action is to
     * be taken on the entire original path
     */
    fun <T> addEventListener(
        key: String?,
        type: Class<T>,
        onChildAdded: ((T) -> Unit)?,
        onChildRemoved: (String) -> Unit
    )
}