package com.github.h3lp3rs.h3lp.database

import com.github.h3lp3rs.h3lp.model.database.Database
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.concurrent.thread
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
    fun addToObjectsListConcurrentlyWorksAtomically() {
        val string1 = TEST_SEED.nextBytes(5 * BYTES_PER_CHAR).toString()
        val string2 = TEST_SEED.nextBytes(5 * BYTES_PER_CHAR).toString()
        val string3 = TEST_SEED.nextBytes(5 * BYTES_PER_CHAR).toString()

        var strings = Collections.synchronizedList<String>(mutableListOf())

        // We check that when the objects are sent to the list, no overwriting happens, thus that
        // strings contains all the strings (string1..) that were sent
        db.addListListener(TEST_KEY, String::class.java) { strings = it }

        val t1 = thread { db.addToObjectsListConcurrently(TEST_KEY, String::class.java, string1) }
        val t2 = thread { db.addToObjectsListConcurrently(TEST_KEY, String::class.java, string2) }

        db.addToObjectsListConcurrently(TEST_KEY, String::class.java, string3)
        t1.join(); t2.join()


        assertThat(strings, containsInAnyOrder(string1, string2, string3))
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

    @Test
    fun incrementIsAtomic() {
        val old = TEST_SEED.nextInt()
        db.setInt(TEST_KEY, old)

        val expectedUnordered = listOf(old + 1, old + 2, old + 3)

        // We test that each thread atomically adds 1 to the value and each one sees a unique value
        val incrementValues = Collections.synchronizedList<Int>(mutableListOf())
        val callBack: (Int) -> Unit = { incrementValues.add(it) }

        val t1 = thread { db.incrementAndGet(TEST_KEY, 1).thenApply { callBack(it) } }
        val t2 = thread { db.incrementAndGet(TEST_KEY, 1).thenApply { callBack(it) } }
        db.incrementAndGet(TEST_KEY, 1).thenApply { callBack(it) }
        t1.join(); t2.join()

        assertEquals(old + 3, db.getInt(TEST_KEY).get())

        assertThat(
            incrementValues,
            containsInAnyOrder(expectedUnordered[0], expectedUnordered[1], expectedUnordered[2])
        )
    }
}