package com.github.h3lp3rs.h3lp.database

import android.annotation.SuppressLint
import com.google.firebase.database.core.utilities.encoding.CustomClassMapper.*
import java.lang.NullPointerException
import com.google.gson.Gson
import java.util.concurrent.CompletableFuture

/**
 * Implementation of a NoSQL mocked external database
 */
class MockDatabase : Database {
    private val db = HashMap<String, Any>()

    // Lists on which values can be added concurrently, has to be a separate object from db since
    // it doesn't have the same behaviour when two writes happen at the same time
    private val concurrentLists = HashMap<String, List<String>>()
    private val listeners = HashMap<String, List<() -> Unit>>()

    /**
     * Utility function to extract values into futures from generic types
     * @param key The key in the database
     */
    private inline fun <reified T : Any> get(key: String): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        if (db.containsKey(key)) future.complete(db[key] as T)
        else future.completeExceptionally(NullPointerException("Key: $key not in the database"))
        return future
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
        if (old != value) {
            triggerListeners(key)
        }
    }


    /**
     * Utility function to trigger all the listeners related to a specific key
     * @param key The key in the database
     */
    private fun triggerListeners(key: String) {
        for (l in listeners.getOrDefault(key, emptyList())) l()
    }

    override fun getBoolean(key: String): CompletableFuture<Boolean> {
        return get(key)
    }

    override fun setBoolean(key: String, value: Boolean) {
        setAndTriggerListeners(key, value)
    }

    override fun getString(key: String): CompletableFuture<String> {
        return get(key)
    }

    override fun setString(key: String, value: String) {
        setAndTriggerListeners(key, value)
    }

    override fun getDouble(key: String): CompletableFuture<Double> {
        return get(key)
    }

    override fun setDouble(key: String, value: Double) {
        setAndTriggerListeners(key, value)
    }

    override fun getInt(key: String): CompletableFuture<Int> {
        return get(key)
    }

    override fun setInt(key: String, value: Int) {
        setAndTriggerListeners(key, value)
    }

    override fun addStringConcurrently(key: String, value: String) {
        synchronized(this) {
            val old = concurrentLists.getOrDefault(key, emptyList())
            concurrentLists[key] = old.plus(value)
            triggerListeners(key)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun <T> addListener(key: String, type: Class<T>, action: (T) -> Unit) {
        val wrappedAction: () -> Unit = {
            val v: T = if(type == String::class.java || type == Int::class.java ||
                type == Double::class.java || type == Boolean::class.java) {
                convertToCustomClass(db[key], type)
            } else {
                getObject(key, type).get()
            }
            action(v)
        }
        // Enrich the list & add to map
        val ls = listeners.getOrDefault(key, emptyList()) + listOf(wrappedAction)
        listeners[key] = ls
        // First time trigger
        wrappedAction()
    }

    override fun <T> addListenerIfNotPresent(key: String, type: Class<T>, action: (T) -> Unit) {
        if(!listeners.containsKey(key)) {
            addListener(key, type, action)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun <T> addListListener(key: String, type: Class<T>, action: (List<T>) -> Unit) {
        val wrappedAction: () -> Unit = {
            // Reconstructing the entire list to be able to call action on it
            val gson = Gson()
            val objectsList = concurrentLists
                .getOrDefault(key, emptyList())
                .map { gson.fromJson(it, type) }
            action(objectsList)
        }
        // Enrich the list & add to map
        val ls = listeners.getOrDefault(key, emptyList()) + listOf(wrappedAction)
        listeners[key] = ls
        // First time trigger
        wrappedAction()
    }

    override fun clearListeners(key: String) {
        listeners.remove(key)
    }

    override fun clearAllListeners() {
        val copy = HashMap(listeners)
        for(key in copy.keys) {
            clearListeners(key)
        }
    }

    override fun delete(key: String) {
        db.remove(key)
        listeners.remove(key)
    }


    override fun incrementAndGet(key: String, increment: Int): CompletableFuture<Int> {
        synchronized(this) {
            val future: CompletableFuture<Int> = CompletableFuture()
            val old = db.getOrDefault(key, 0) as Int
            val new = old + increment
            setAndTriggerListeners(key, new)
            future.complete(new)
            return future
        }
    }
}