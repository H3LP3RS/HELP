package com.github.h3lp3rs.h3lp

import androidx.test.core.app.ApplicationProvider
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.*
import com.github.h3lp3rs.h3lp.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.signin.SignInActivity.Companion.userUid
import com.github.h3lp3rs.h3lp.storage.LocalStorage
import com.github.h3lp3rs.h3lp.storage.Storages.*
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.resetStorage
import com.github.h3lp3rs.h3lp.storage.Storages.Companion.storageOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class StorageTest : H3lpAppTest() {

    // Dummy class for complex types
    private data class Foo(val a1: Int, val a2: String)
    // Useful variables
    private lateinit var storage: LocalStorage
    private val TEST_KEY = "KEY"
    private val TEST_SEED = Random(0)
    private val BYTES_PER_CHAR = 2

    @Before
    fun setup() {
        globalContext = ApplicationProvider.getApplicationContext()
        userUid = USER_TEST_ID
        setDatabase(PREFERENCES, MockDatabase())
        resetStorage()
        // Will start empty
        USER_COOKIE.setOnlineSync(true)
        storage = storageOf(USER_COOKIE)

    }

    @Test
    fun getAndSetWorkProperly() {
        val int = TEST_SEED.nextInt()
        val string = TEST_SEED.nextBytes(5 * BYTES_PER_CHAR).toString()
        val bool = TEST_SEED.nextBoolean()
        val obj = Foo(int, string)

        storage.setInt(TEST_KEY, int)
        assertEquals(int, storage.getIntOrDefault(TEST_KEY, int + 1))

        storage.setString(TEST_KEY, string)
        assertEquals(string, storage.getStringOrDefault(TEST_KEY, string + "1"))

        storage.setBoolean(TEST_KEY, bool)
        assertEquals(bool, storage.getBoolOrDefault(TEST_KEY, !bool))

        storage.setObject(TEST_KEY, Foo::class.java, obj)
        assertEquals(obj, storage.getObjectOrDefault(TEST_KEY, Foo::class.java, Foo(int + 1, string + "1")))
    }

    @Test
    fun clearWorksAsIntended() {
        val int = TEST_SEED.nextInt()
        storage.setInt(TEST_KEY, int)
        storage.setInt(TEST_KEY + "1", int + 1)

        assertEquals(int, storage.getIntOrDefault(TEST_KEY, int))
        assertEquals(int + 1, storage.getIntOrDefault(TEST_KEY + "1", int + 1))

        resetStorage()

        assertEquals(int - 2, storage.getIntOrDefault(TEST_KEY, int - 2))
        assertEquals(int - 2, storage.getIntOrDefault(TEST_KEY + "1", int - 2))
    }

    @Test
    fun pushAndPullWorksProperly() {
        assertEquals(-1, storage.getIntOrDefault(TEST_KEY, -1))

        storage.setInt(TEST_KEY, 0)
        assertEquals(0, storage.getIntOrDefault(TEST_KEY, -1))

        storage.push()
        resetStorage()
        assertEquals(-1, storage.getIntOrDefault(TEST_KEY, -1))

        storage.pull()
        assertEquals(0, storage.getIntOrDefault(TEST_KEY, -1))
    }
}