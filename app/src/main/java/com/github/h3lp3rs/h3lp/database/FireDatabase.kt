package com.github.h3lp3rs.h3lp.database

import com.google.firebase.database.*
import com.google.firebase.database.ktx.*
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.concurrent.CompletableFuture

/**
 * Implementation of a NoSQL external database based on Firebase
 */
internal class FireDatabase(path: String) : Database {

    private val db: DatabaseReference = Firebase.database("https://h3lp-signin-default-rtdb.europe-west1.firebasedatabase.app/").reference.child(path)
    private val openListeners = HashMap<String, List<ValueEventListener>>()

    /**
     * Utility function to extract values into futures from generic types
     * Only works on the following types (due to Firebase's policy):
     * - Boolean
     * - String
     * - Int
     * - Double
     * @param key The key in the database
     */
    private inline fun <reified  T: Any> get(key: String): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        db.child(key).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else future.complete(it.value as T)
        }.addOnFailureListener {
            future.completeExceptionally(it)
        }
        return future
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
        db.child(key).setValue(value)
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
        db.child(key).setValue(value)
    }

    /**
     * Gets a double from the database
     * @param key The key in the database
     * @return Future of double
     */
    override fun getDouble(key: String): CompletableFuture<Double> {
        // This Fix is due to a misconception in firebase:
        // Storing 3.0 in firebase will automatically transform it into a long integer.
        // This causes a type error when getting it since long cannot be directly cast to double.
        // This is fixed by getting the field as the superclass number and then casting it with its function.

        val number :CompletableFuture<Number> = get(key)
        return number.thenApply { n -> n.toDouble() }
    }

    /**
     * Sets a double to the database
     * @param key The key in the database
     * @param value The value of the double
     */
    override fun setDouble(key: String, value: Double) {
        db.child(key).setValue(value)
    }

    /**
     * Gets an int from the database
     * @param key The key in the database
     * @return Future of int
     */
    override fun getInt(key: String): CompletableFuture<Int> {
        // NOTE: We have to recode this case as Firebase natively supports
        // Longs and not Ints.
        return get<Long>(key).thenApply { it.toInt() }
    }

    /**
     * Sets an int to the database
     * @param key The key in the database
     * @param value The value of the int
     */
    override fun setInt(key: String, value: Int) {
        db.child(key).setValue(value)
    }

    /**
     * Adds a value to the list of values paired to the key in the database
     * Can be called concurrently without creating overwriting problems (all values are eventually
     * added to the list)
     * @param key The key in the database
     * @param value The value of the string
     */
    override fun addStringConcurrently(key: String, value: String) {
        db.child(key).push().setValue(value)
    }

    /**
     * Generic way of adding listeners to a key in the database, allows us to implement
     * adding of listeners for values and lists of values concisely
     * WARNING: This function automatically triggers at first when linked with a valid key
     * @param key The key in the database
     * @param onDataChange The action to take on the new data snapshot
     */
    private fun genericAddListener(key: String, onDataChange: (DataSnapshot) -> Unit) {
        val l = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onDataChange(snapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Firebase listener error: ${databaseError.toException()}")
            }
        }
        db.child(key).addValueEventListener(l)
        // Enrich the list & add to map
        val ls = openListeners.getOrDefault(key, emptyList()) + listOf(l)
        openListeners[key] = ls
    }

    /**
     * Applies an arbitrary action when the value associated to the value changes
     * WARNING: This function automatically triggers at first when linked with a valid key
     * @param key The key in the database
     * @param type The type of the value associated to the key
     * @param action The action taken at change
     */
    override fun <T> addListener(key: String, type: Class<T>, action: (T) -> Unit) {
        fun onDataChange(snapshot: DataSnapshot) {
            val v: T = if(type == String::class.java || type == Int::class.java ||
                type == Double::class.java || type == Boolean::class.java) {
                snapshot.getValue(type)!!
            } else {
                val gson = Gson()
                gson.fromJson(snapshot.getValue(String::class.java)!!, type)
            }
            action(v)
        }

        genericAddListener(key) { onDataChange(it) }
    }

    /**
     * Is necessary to distinguish between associating a list of values to a key or associating
     * a key to several unique ids (generated by the database) to be able to add values to the list
     * concurrently in addToObjectsList
     * Applies an arbitrary action when the list of values associated to the key changes
     * WARNING: This function automatically triggers at first when linked with a valid key
     * @param key The key in the database
     * @param action The action taken at change
     */
    override fun <T> addListListener(key: String, type: Class<T>, action: (List<T>) -> Unit) {
        fun onDataChange(snapshot: DataSnapshot) {
            val valuesList = mutableListOf<T>()
            // We have call the action on the entire list of values, so we first construct the list
            // from the children of the snapshot, then call the method
            for (postSnapshot in snapshot.children) {
                val v: T = if(type == String::class.java || type == Int::class.java ||
                    type == Double::class.java || type == Boolean::class.java) {
                    postSnapshot.getValue(type)!!
                } else {
                    val gson = Gson()
                    gson.fromJson(postSnapshot.getValue(String::class.java)!!, type)
                }
                valuesList.add(v)
            }
            action(valuesList)
        }

        genericAddListener(key) { onDataChange(it) }
    }

    /**
     * Clears all listeners related to a given key
     * @param key The key in the database
     */
    override fun clearListeners(key: String) {
        val ls = openListeners.getOrDefault(key, emptyList())
        for(l in ls) {
            db.child(key).removeEventListener(l)
        }
        openListeners.remove(key)
    }

    /**
     * Deletes an entry of a given key from the database
     * @param key They key in the database
     */
    override fun delete(key: String) {
        clearListeners(key)
        db.child(key).removeValue()
    }

    /**
     * Atomically increments an integer value of the database
     * @param key The key in the database
     * @param number The number to increment by
     */
    override fun incrementAndGet(key: String, number: Int, onComplete: (String?) -> Unit) {
        val keyRef = db.child(key)
        keyRef.runTransaction(object : Transaction.Handler {
            /**
             * This method will be called, possibly multiple times, with the current data at this
             * location. It is responsible for inspecting that data and returning a [Result]
             * specifying either the desired new data at the location or that the transaction should be
             * aborted.
             *
             * @param currentData The current data at the location. Update this to the desired data at the
             * location
             * @return Either the new data, or an indication to abort the transaction
             */
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val oldValue = currentData.getValue<Int>()
                currentData.value = oldValue?.plus(number) ?: 0
                return Transaction.success(currentData)
            }

            /**
             * This method will be called once with the results of the transaction.
             *
             * @param error null if no errors occurred, otherwise it contains a description of the error
             * @param committed True if the transaction successfully completed, false if it was aborted or
             * an error occurred
             * @param currentData The current data at the location or null if an error occurred
             */
            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                onComplete(currentData?.getValue<Int>()?.toString())
            }

        })
    }
}