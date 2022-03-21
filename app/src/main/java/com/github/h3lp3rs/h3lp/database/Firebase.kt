package com.github.h3lp3rs.h3lp.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.CompletableFuture

class Firebase(private val db: DatabaseReference) : Database {

    /**
     * Gets a boolean from the database
     * @param key The key in the database
     * @return Future of boolean
     */
    override fun getBoolean(key: String): CompletableFuture<Boolean> {
        TODO("Not yet implemented")
    }

    /**
     * Gets a string from the database
     * @param key The key in the database
     * @return Future of string
     */
    override fun getString(key: String): CompletableFuture<String> {
        TODO("Not yet implemented")
    }

    /**
     * Sets a string to the database
     * @param key The key in the database
     * @param value The value of the string
     */
    override fun setString(key: String, value: String) {
        TODO("Not yet implemented")
    }

    /**
     * Gets a double from the database
     * @param key The key in the database
     * @return Future of double
     */
    override fun getDouble(key: String): CompletableFuture<Double> {
        TODO("Not yet implemented")
    }

    /**
     * Sets a double to the database
     * @param key The key in the database
     * @param value The value of the double
     */
    override fun setDouble(key: String, value: Double) {
        TODO("Not yet implemented")
    }

    /**
     * Gets an int from the database
     * @param key The key in the database
     * @return Future of int
     */
    override fun getInt(key: String): CompletableFuture<Int> {
        TODO("Not yet implemented")
    }

    /**
     * Sets an int to the database
     * @param key The key in the database
     * @param value The value of the int
     */
    override fun setInt(key: String, value: Int) {
        TODO("Not yet implemented")
    }

    /**
     * Applies an arbitrary action when the value associated to the value changes
     * @param key The key in the database
     * @param action The action taken at change
     */
    override fun whenChange(key: String, action: () -> Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                action()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Firebase listener error: ${databaseError.toException()}")
            }
        }
        db.child(key).addListenerForSingleValueEvent(listener)
    }
}