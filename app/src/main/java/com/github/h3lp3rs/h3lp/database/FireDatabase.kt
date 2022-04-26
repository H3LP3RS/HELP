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

    override fun getBoolean(key: String): CompletableFuture<Boolean> {
        return get(key)
    }

    override fun setBoolean(key: String, value: Boolean) {
        db.child(key).setValue(value)
    }

    override fun getString(key: String): CompletableFuture<String> {
        return get(key)
    }

    override fun setString(key: String, value: String) {
        db.child(key).setValue(value)
    }

    override fun getDouble(key: String): CompletableFuture<Double> {
        // This Fix is due to a misconception in firebase:
        // Storing 3.0 in firebase will automatically transform it into a long integer.
        // This causes a type error when getting it since long cannot be directly cast to double.
        // This is fixed by getting the field as the superclass number and then casting it with its function.

        val number :CompletableFuture<Number> = get(key)
        return number.thenApply { n -> n.toDouble() }
    }

    override fun setDouble(key: String, value: Double) {
        db.child(key).setValue(value)
    }

    override fun getInt(key: String): CompletableFuture<Int> {
        // NOTE: We have to recode this case as Firebase natively supports
        // Longs and not Ints.
        return get<Long>(key).thenApply { it.toInt() }
    }

    override fun setInt(key: String, value: Int) {
        db.child(key).setValue(value)
    }

    override fun <T> addListener(key: String, type: Class<T>, action: (T) -> Unit) {
        val l = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val v: T = if(type == String::class.java || type == Int::class.java ||
                    type == Double::class.java || type == Boolean::class.java) {
                    snapshot.getValue(type)!!
                } else {
                    val gson = Gson()
                    gson.fromJson(snapshot.getValue(String::class.java)!!, type)
                }
                action(v)
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

    override fun <T> addListenerIfNotPresent(key: String, type: Class<T>, action: (T) -> Unit) {
        if(!openListeners.containsKey(key)) {
            addListener(key, type, action)
        }
    }

    override fun clearListeners(key: String) {
        val ls = openListeners.getOrDefault(key, emptyList())
        for(l in ls) {
            db.child(key).removeEventListener(l)
        }
        openListeners.remove(key)
    }

    override fun clearAllListeners() {
        val copy = HashMap(openListeners)
        for(key in copy.keys) {
            clearListeners(key)
        }
    }

    override fun delete(key: String) {
        clearListeners(key)
        db.child(key).removeValue()
    }

    override fun incrementAndGet(key: String, increment: Int, onComplete: (String?) -> Unit) {
        val keyRef = db.child(key)
        // A transaction is a set of reads and writes that happen atomically
        keyRef.runTransaction(object : Transaction.Handler {

            /**
             * This method will be called, possibly multiple times, with the current data at this
             * location. It is responsible for inspecting that data and returning a [Result]
             * specifying either the desired new data at the location or that the transaction should be
             * aborted.
             *
             * @param currentData The current counter value associated to the key
             * @return Either the incremented counter, or an indication to abort the transaction
             */
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val oldValue = currentData.getValue<Int>()
                // If oldValue is null, this means that there is no counter associated with this
                // key, we thus initialise it to the requested number (as if the counter were just
                // initialised, it should get value 0 + number = number)
                currentData.value = oldValue?.plus(increment) ?: increment
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