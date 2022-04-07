package com.github.h3lp3rs.h3lp

import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.database.models.EmergencyInformation
import com.github.h3lp3rs.h3lp.database.repositories.EmergencyInfoRepository
import com.github.h3lp3rs.h3lp.database.repositories.Repository
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

class EmergencyInfoRepositoryTest {
    private val repository: Repository<EmergencyInformation> =
        EmergencyInfoRepository(MockDatabase())
    private val testKey = "KEY"
    private val testDoubleValue = 0.0
    private val testObject =
        EmergencyInformation(testKey, testDoubleValue, testDoubleValue, ArrayList(), Date(), "")

    @Test
    fun createdObjectIsCorrect() {
        repository.insert(testObject)
        val res = repository.get(testKey).get()
        assertEquals(res.latitude, testDoubleValue)
        assertEquals(res.longitude, testDoubleValue)
        assertEquals(res.meds, ArrayList<String>())
    }


    @Test
    fun deleteObjectWorks() {
        repository.insert(testObject)
        repository.delete(testKey)
        val res = repository.get(testKey)
        assertTrue(res.isCompletedExceptionally)
    }
}