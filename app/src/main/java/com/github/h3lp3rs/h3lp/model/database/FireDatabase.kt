package com.github.h3lp3rs.h3lp.model.database

import android.content.Context
import android.util.Log
import com.github.h3lp3rs.h3lp.R
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.util.concurrent.CompletableFuture

/**
 * Implementation of a NoSQL external database based on Firebase
 * @param path The path to the Firebase database (we use several databases as defined in Databases),
 * in actuality, they are all on the same Firebase database but are different children on it to
 * allow us to separate the database into its different uses
 * @param context The context of the activity using the Database to have access to the resources
 */
class FireDatabase(private val path: String, private val context: Context) : Database {
    private val db: DatabaseReference = Firebase
        .database(context.resources.getString(R.string.firebase_url))
        .reference.child(path)
    private val openValueListeners = HashMap<String, List<ValueEventListener>>()
    private val openEventListeners = HashMap<String, List<ChildEventListener>>()

    /**
     * Utility function to extract values into futures from generic types
     * Only works on the following types (due to Firebase's policy):
     * - Boolean
     * - String
     * - Int
     * - Double
     * @param key The key in the database
     * @return A future containing the requested value (or a future completing exceptionally if
     * the key doesn't correspond to any such value)
     */
    private inline fun <reified T : Any> get(key: String): CompletableFuture<T> {
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
        // This is fixed by getting the field as the superclass number and then casting it with its
        // function.

        val number: CompletableFuture<Number> = get(key)
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

    override fun addStringConcurrently(key: String, value: String): String? {
        // Push generates a unique key for each new child, thus several clients can add children to
        // the same location at the same time without worrying about write conflicts
        val ref = db.child(key).push()
        ref.setValue(value)
        return ref.key
    }

    override fun <T> getObjectsList(key: String, type: Class<T>): CompletableFuture<List<T>> {
        val future = CompletableFuture<List<T>>()
        db.child(key).get().addOnSuccessListener {
            if (it.value == null) future.completeExceptionally(NoSuchFieldException())
            else {
                val valuesList = getSnapshotChildren(it, type)
                future.complete(valuesList)
            }
        }
        return future
    }

    /**
     * Generic way of adding listeners to a key in the database, allows us to implement
     * adding of listeners for values (addListener) and lists of values (addListListener) concisely
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
                Log.e("Firebase listener error:", databaseError.toException().toString())
            }
        }
        db.child(key).addValueEventListener(l)
        // Enrich the list & add to map
        val ls = openValueListeners.getOrDefault(key, emptyList()) + listOf(l)
        openValueListeners[key] = ls
    }

    override fun <T> addListenerIfNotPresent(key: String, type: Class<T>, action: (T) -> Unit) {
        if (!openValueListeners.containsKey(key)) {
            addListener(key, type, action)
        }
    }

    override fun <T> addListener(key: String, type: Class<T>, action: (T) -> Unit) {
        fun onDataChange(snapshot: DataSnapshot) {
            val v: T =
                if (type == String::class.java || type == Int::class.java || type == Double::class.java || type == Boolean::class.java) {
                    snapshot.getValue(type)!!
                } else {
                    val gson = Gson()
                    gson.fromJson(snapshot.getValue(String::class.java)!!, type)
                }
            action(v)
        }

        genericAddListener(key) { onDataChange(it) }
    }

    override fun <T> addListListener(key: String, type: Class<T>, action: (List<T>) -> Unit) {
        fun onDataChange(snapshot: DataSnapshot) {
            // We have to call the action on the entire list of values, so we first construct the
            // list from the children of the snapshot (all the values pushed in
            // addStringConcurrently), then call the method
            val valuesList = getSnapshotChildren(snapshot, type)

            // Run the callback on the newly constructed list of values
            action(valuesList)
        }

        genericAddListener(key) { onDataChange(it) }
    }

    override fun clearListeners(key: String) {
        val ls = openValueListeners.getOrDefault(key, emptyList())
        for (l in ls) {
            db.child(key).removeEventListener(l)
        }
        val els = openEventListeners.getOrDefault(key, emptyList())
        val database = if (key.isEmpty()) db else db.child(key)
        for (l in els) {
            database.removeEventListener(l)
        }
        openValueListeners.remove(key)
        openEventListeners.remove(key)
    }

    override fun clearAllListeners() {
        val copy = HashMap(openValueListeners)
        for (key in copy.keys) {
            clearListeners(key)
        }
        val clone = HashMap(openEventListeners)
        for (key in clone.keys) {
            clearListeners(key)
        }
    }

    override fun delete(key: String) {
        clearListeners(key)
        if (key.isEmpty()) db.removeValue()
        else db.child(key).removeValue()
    }

    private fun setEventListener(
        childKey: String?,
        onDataChange: (DataSnapshot) -> Unit,
        onDataRemoved: (DataSnapshot) -> Unit
    ) {
        val eventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                onDataChange(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                onDataRemoved(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase listener error:", error.toException().toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        }

        val key = if (childKey == null) {
            db.addChildEventListener(eventListener)
            // The listener is added to the entire db
            ""
        } else {
            db.child(childKey).addChildEventListener(eventListener)
            childKey
        }

        // Enrich the list & add to map
        val ls = openEventListeners.getOrDefault(key, emptyList()) + listOf(eventListener)
        openEventListeners[key] = ls
    }

    override fun <T> addEventListener(
        key: String?,
        type: Class<T>,
        onChildAdded: ((T) -> Unit)?,
        onChildRemoved: (String) -> Unit
    ) {
        fun onDataChange(snapshot: DataSnapshot) {
            onChildAdded?.let {
                val v: T = Gson().fromJson(snapshot.getValue(String::class.java), type)
                it(v)
            }
        }

        fun onDataRemoved(snapshot: DataSnapshot) {
            // It is only necessary to know which element has been deleted, not its value, thus the
            // action is only executed on the key of the element
            onChildRemoved(snapshot.key!!)
        }
        setEventListener(key, { onDataChange(it) }, { onDataRemoved(it) })
    }

    /**
     * Atomically increments an integer value of the database and calls the callback with the new
     * value
     * @param key The key in the database
     * @param increment The number to increment by
     * @param onComplete The callback to be called with the new value (the new value can be null
     * in case of a database error, thus why onComplete takes a nullable Int)
     */
    override fun incrementAndGet(key: String, increment: Int): CompletableFuture<Int> {
        val keyRef = db.child(key)
        val future = CompletableFuture<Int>()

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
                error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?
            ) {
                if (error != null) {
                    future.completeExceptionally(error.toException())
                } else {
                    future.complete(currentData?.getValue<Int>())
                }
            }
        })
        return future
    }

    /**
     * Extracts the functionality of getting the objects corresponding to the children of an element
     * in the database
     * @param snapshot The data snapshot from which to get the list of objects
     * @param type The type of the objects in the list
     * @return The current associated list of objects in the database
     */
    private fun <T> getSnapshotChildren(snapshot: DataSnapshot, type: Class<T>): List<T> {
        val valuesList = mutableListOf<T>()

        for (postSnapshot in snapshot.children) {
            val gson = Gson()
            val v = gson.fromJson(postSnapshot.getValue(String::class.java)!!, type)
            valuesList.add(v)
        }
        return valuesList
    }
}