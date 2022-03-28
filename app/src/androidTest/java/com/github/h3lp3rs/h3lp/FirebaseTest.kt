package com.github.h3lp3rs.h3lp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseTest {
    // Key used for testing purposes
    private val testingKey = "TESTING_KEY"
    private val testingListenerKey = "TESTING_LISTENER_KEY"
    // Dummy class for complex types
    private data class Foo(val a1: Double, val a2: String)

    @Test
    fun storeAndGetWorks() {
        // Iterate through all databases
        for(e in Databases.values()) {
            val db = e.db
            val foo = Foo(1.1, "1")
            // Force JVM happens-before relationship
            db.setInt(testingKey + 1, 1)
            db.setString(testingKey + 2, "1")
            db.setBoolean(testingKey + 3, false)
            db.setDouble(testingKey + 4, 1.0)
            db.setObject(testingKey + 5, Foo::class.java, foo)
            assertEquals(1, db.getInt(testingKey + 1).get())
            assertEquals("1", db.getString(testingKey + 2).get())
            assertEquals(false, db.getBoolean(testingKey + 3).get())
            assertEquals(1.0, db.getDouble(testingKey + 4).get())
            assertEquals(foo, db.getObject(testingKey + 5, Foo::class.java).get())
        }
    }

    @Test
    fun listenerListensAndCloses() {
        // Start a listener, trigger it and close it directly
        val db = databaseOf(EMERGENCIES)
        val old = db.getDouble(testingListenerKey).get()
        db.addListener(testingListenerKey, Int::class.java) {
            db.setDouble(testingListenerKey, it + 1.0)
            db.clearListeners(testingListenerKey)
            assertEquals(old + 1.0, db.getDouble(testingListenerKey).get())
        }
    }
}