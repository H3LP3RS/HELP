package com.github.h3lp3rs.h3lp

import com.github.h3lp3rs.h3lp.database.Database
import com.github.h3lp3rs.h3lp.database.MockDatabase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class MockDatabaseTest {

    // Dummy class for complex types
    private data class Foo(val a1: Double, val a2: String)
    // Useful variables
    private lateinit var db: Database
    private val TEST_KEY = "KEY"
    private val TEST_SEED = Random(0)
    private val BYTES_PER_CHAR = 2
    private val DELTA = 1e-10

    @Before
    fun setup() {
        db = MockDatabase()
    }

    @Test
    fun setAndGetWorksProperly() {
        val int = TEST_SEED.nextInt()
        val string = TEST_SEED.nextBytes(5 * BYTES_PER_CHAR).toString()
        val bool = TEST_SEED.nextBoolean()
        val double = TEST_SEED.nextDouble()
        val obj = Foo(double, string)

        db.setInt(TEST_KEY, int)
        assertEquals(int, db.getInt(TEST_KEY).get())

        db.setString(TEST_KEY, string)
        assertEquals(string, db.getString(TEST_KEY).get())

        db.setBoolean(TEST_KEY, bool)
        assertEquals(bool, db.getBoolean(TEST_KEY).get())

        db.setDouble(TEST_KEY, double)
        assertEquals(double, db.getDouble(TEST_KEY).get(), DELTA)

        db.setObject(TEST_KEY, Foo::class.java, obj)
        assertEquals(obj, db.getObject(TEST_KEY, Foo::class.java).get())
    }

    @Test
    fun deleteWorksProperly() {
        val int = TEST_SEED.nextInt()
        db.setInt(TEST_KEY, int)
        assertEquals(int, db.getInt(TEST_KEY).get())
        db.delete(TEST_KEY)
        assertTrue(db.getInt(TEST_KEY).isCompletedExceptionally)
    }

    @Test
    fun addListenerIsTriggeredAtFirst() {
        var flag = false
        val int = TEST_SEED.nextInt()
        db.setInt(TEST_KEY, int)
        db.addListener(TEST_KEY, Int::class.java) {
            flag = true
        }
        assertTrue(flag)
    }

    @Test
    fun listenerIsTriggeredAtChange() {
        var flag = false
        val old = TEST_SEED.nextInt()
        db.setInt(TEST_KEY, old)
        db.addListener(TEST_KEY, Int::class.java) {
            if (it != old) {
                flag = true
            }
        }
        db.setInt(TEST_KEY, old + 1)
        assertTrue(flag)
    }

    @Test
    fun listenerAreProperlyDeleted() {
        var flag = false
        val old = TEST_SEED.nextInt()
        db.setInt(TEST_KEY, old)
        db.addListener(TEST_KEY, Int::class.java) {
            if (it != old) {
                flag = true
            }
        }
        db.clearListeners(TEST_KEY)
        db.setInt(TEST_KEY, old + 1)
        assertTrue(!flag)
    }
}